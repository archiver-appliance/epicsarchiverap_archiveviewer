package epics.archiveviewer.base.fundamental;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.util.formula.FormulaCalculator;

/**
 * <code>CalculatedArchiveEntry</code> ... DOCUMENT ME!
 * 
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar </a>
 * @version $Id: CalculatedArchiveEntry.java,v 1.4 2004/08/02 16:06:03 schevtsov
 *          Exp $
 * 
 * @since Jul 15, 2004.
 */
public class CalculatedValuesContainer implements ValuesContainer
{
	private class TimestampedValue
	{
		private final double timestamp;
		private final Double value;
		
		public TimestampedValue(double time, Double v)
		{
			this.timestamp = time;
			this.value = v;
		}
		
		public double getTimestamp() {
			return this.timestamp;
		}
		
		public Double getValue() {
			return this.value;
		}
	}
	
	private final AVEntry avEntry;
	
	//Integer to String
	private final HashMap invalidIndicesAndLabels;
	
	private final DecimalFormat numberFormat;

	private ArrayList timestampedValues;
	
	private int precision;
	
	private double validMin;
	
	private double validMax;
	
	private double validMinPos;
	
	private TimestampedValue getTimestampedValue(int index)
	{
		return (TimestampedValue) this.timestampedValues.get(index);
	}
	
	private void calculate(String term, Map argumentsAndAENames, ValuesContainer[] vcs) throws Exception
	{
		if(term == null || term.equals(""))
			throw new NullPointerException("No term");
		
		if(argumentsAndAENames == null || argumentsAndAENames.isEmpty())
			throw new NullPointerException("No arguments");
		
		if(vcs == null || vcs.length == 0)
			throw new NullPointerException("No data");
		
		this.invalidIndicesAndLabels.clear();
		
		FormulaCalculator fc = new FormulaCalculator(term, argumentsAndAENames);
		
		this.validMin = Double.MAX_VALUE;
		this.validMinPos = Double.MAX_VALUE;
		this.validMax = - 1 * Double.MAX_VALUE;
		
		int i=0;
		//set precision
		for(i=0; i<vcs.length; i++)
		{
			if(vcs[i].getPrecision() < this.precision)
				this.precision = vcs[i].getPrecision();
		}
		
		this.numberFormat.setGroupingSize(0);
		this.numberFormat.setMaximumFractionDigits(this.precision);
		
		getAVEntry().getMetaData().put("precision", new Integer(this.precision));
		
		//look for 'global' parameters in the first ValuesContainer only
		int nrValues = vcs[0].getNumberOfValues();

		this.timestampedValues = new ArrayList(nrValues);
		
		double timestamp = -1;
		Double value = null;
		
		HashMap aeNamesAndValues = new HashMap();

		for (i = 0; i < nrValues; i++)
		{
			timestamp = vcs[0].getTimestampInMsec(i);
			
			boolean isInvalidArgument = false;

			for (int j = 0; j < vcs.length; j++)
			{
				//check data types only on the first run
				if (i == 0)
				{
					if(vcs[j].isWaveform())
					{
						throw new IllegalArgumentException(
								vcs[j].getAVEntry().getName() +
								" is a waveform and cannot be used in a formula");
					}
					else if(Number.class.isAssignableFrom(vcs[j].getDataType()) == false)
					{
						throw new IllegalArgumentException(
								"Can not use the values of the AV entry " + 
								vcs[j].getAVEntry().getName() +
								" in a formula");
					}
				}
				if(vcs[j].isValid(i) == false)
				{
					// System.out.println("Invalid argument vcs[" + j + "](" + i + ")");
					isInvalidArgument = true;
					break;
				}
				aeNamesAndValues.put(vcs[j].getAVEntry().getName(), vcs[j].getValue(i).get(0));
			}
			
			if(isInvalidArgument)
			{
				this.invalidIndicesAndLabels.put(new Integer(i), "Invalid Argument");
				value = new Double(0);
			}
			else
				value = fc.calculate(aeNamesAndValues);
			
			if(value == null )
				this.invalidIndicesAndLabels.put(new Integer(i), "NULL");
			else if(
					value.isInfinite() || 
					value.isNaN()
					)
			{
				this.invalidIndicesAndLabels.put(new Integer(i), value.toString());
				value = new Double(0);
			}
			else
			{
				//valid value
				double v = value.doubleValue();
				if(v < this.validMin)
					validMin = v;
				if(v > this.validMax)
					validMax = v;
				if(v > 0 && v < this.validMinPos)
					this.validMinPos = v;
			}
			
			this.timestampedValues.add(new TimestampedValue(timestamp, value));
			
			aeNamesAndValues.clear();
		}
	}

	public CalculatedValuesContainer(AVEntry ave, Formula f, ValuesContainer[] argVCs) throws Exception
	{
		this.avEntry = ave;
		this.invalidIndicesAndLabels = new HashMap();
		//just any large number
		this.precision = 100;
		this.numberFormat = new DecimalFormat();
		try
		{
			FormulaParameter[] formulaParameters = f.getFormulaParameters();
			HashMap argsAndAVENames = new HashMap();
			for(int i=0; i<formulaParameters.length; i++)
			{
				argsAndAVENames.put(formulaParameters[i].getArg(), formulaParameters[i].getAVEName());
			}
			calculate(f.getTerm(), argsAndAVENames, argVCs);
		}
		catch(Exception e)
		{
			//empty
			this.timestampedValues = new ArrayList();
			//throw e;
		}
	}

	public int getNumberOfValues() throws NullPointerException
	{
		return this.timestampedValues.size();
	}

	public double getTimestampInMsec(int index)
			throws IndexOutOfBoundsException, NullPointerException
	{
		return getTimestampedValue(index).getTimestamp();
	}

	public Vector getValue(int index) throws IndexOutOfBoundsException
	{
		Vector v = new Vector();
		v.add(getTimestampedValue(index).getValue());
		return v;
	}

	public String getDisplayLabel(int index) throws IndexOutOfBoundsException
	{
		Object o = this.invalidIndicesAndLabels.get(new Integer(index));
		if(o == null)
			return valueToString(index, 0) + " calculated";
		return o + " calculated";
	}
	
	public String valueToString(int valueIndex, int item) throws IndexOutOfBoundsException
	{
		if(item !=0)
			throw new IndexOutOfBoundsException();
		
		double d = ((Double)getTimestampedValue(valueIndex).getValue()).doubleValue();
		return this.numberFormat.format(d);
	}

	public boolean isValid(int index) throws IndexOutOfBoundsException,	NullPointerException
	{
		return !this.invalidIndicesAndLabels.containsKey(new Integer(index));
	}

	public int getPrecision()
	{
		return this.precision;
	}
	
	public AVEntry getAVEntry()
	{
		return this.avEntry;
	}
	
	public Class getDataType() {
		return Double.class;
	}
	
	public int getDimension() throws NullPointerException {
		return 1;
	}
	
	public String getUnits() throws NullPointerException {
		return "";
	}
	public boolean isDiscrete() {
		return false;
	}
	
	public boolean isWaveform() {
		return false;
	}

	public String getStatus(int index)
	{
		return "";
	}
	
	public void clear()
	{
		this.timestampedValues.clear();
	}
	
	public double getMaxValidValue() {
		if(isWaveform())
			return Double.NaN;
		return this.validMax;
	}

	public double getMinPosValidValue() {
		return this.validMinPos;
	}

	public double getMinValidValue() {
		return this.validMin;
	}

	public String getRangeLabel(String separator) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.numberFormat.format(this.validMin));
		
		sb.append(separator);
		
		sb.append(this.numberFormat.format(this.validMax));
		
		return sb.toString();		
	}
}