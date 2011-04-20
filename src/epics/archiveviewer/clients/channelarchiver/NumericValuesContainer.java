package epics.archiveviewer.clients.channelarchiver;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;

/**
 * A ValuesContainer for non-enumerated values as specified in the archiver
 * manual.
 * 
 * @author Sergei Chevtsov, Craig McChesney
 * @see ValuesContainer
 */
public class NumericValuesContainer extends AbstractValuesContainer implements
		Serializable
{
	// Instance Variables ======================================================

	/** a meta value as specified in the archiver manual */
	private final double disp_low;

	/** a meta value as specified in the archiver manual */
	private final double disp_high;

	/** a meta value as specified in the archiver manual */
	private final double alarm_low;

	/** a meta value as specified in the archiver manual */
	private final double alarm_high;

	/** a meta value as specified in the archiver manual */
	private final double warn_low;

	/** a meta value as specified in the archiver manual */
	private final double warn_high;

	/** the units of the values in this <CODE>RegularValuesContainer</CODE> */
	private final String units;

	/** the precision of the values in this <CODE>RegularValuesContainer</CODE> */
	private final int precision;

	// for numbers, whose absolute value is < 1
	private final DecimalFormat numberFormat0;

	// for numbers, whose absolute value > 1 formats number with exactly
	// precision digits after the period
	private final DecimalFormat numberFormat1 ;

	/**
	 * Creates a new instance of <CODE>RegularValuesContainer</CODE>
	 * 
	 * @param name
	 *            the name of the values container
	 * @param dataType
	 *            see {@link ValuesContainer#dataType ValuesContainer}
	 * @param valueInfos
	 *            see {@link ValuesContainer#valueInfos ValuesContainer}
	 * @param isWaveform
	 *            see {@link ValuesContainer#isWaveform ValuesContainer}
	 * @param disp_low
	 *            see {@link #disp_low disp_low}
	 * @param disp_high
	 *            see {@link #disp_high disp_high}
	 * @param alarm_low
	 *            see {@link #alarm_low alarm_low}
	 * @param alarm_high
	 *            see {@link #alarm_high alarm_high}
	 * @param warn_low
	 *            see {@link #warn_low warn_low}
	 * @param warn_high
	 *            see {@link #warn_high warn_high}
	 * @param units
	 *            see {@link #units units}
	 * @param precision
	 *            see {@link #precision precision}
	 * @throws IllegalArgumentException
	 *             see
	 *             {@link ValuesContainer#ValuesContainer(String, ValueInfo[], Class, boolean) ValuesContainer}
	 */
	protected NumericValuesContainer(
			AVEntry ae,
			Class dataType,
			Vector valueInfos,
			boolean isWaveform,
			double vMin,
			double vMax,
			double vMinPos,
			double disp_low,
			double disp_high,
			double alarm_low,
			double alarm_high,
			double warn_low,
			double warn_high,
			String units,
			int precision)
	{
		super(ae, vMin, vMax, vMinPos, isWaveform);
		super.setValueInfos(valueInfos);
		super.setDataType(dataType);

		this.disp_low = disp_low;
		this.disp_high = disp_high;
		this.alarm_low = alarm_low;
		this.alarm_high = alarm_high;
		this.warn_low = warn_low;
		this.warn_high = warn_high;
		this.units = units;
		this.precision = precision;

		StringBuffer formatSB = new StringBuffer();
		formatSB.append('0');
		if(this.precision > 0)
		{
			formatSB.append('.');
			int i=0;
			while(i < this.precision)
			{
				formatSB.append('#');
				i++;
			}
		}
				
		this.numberFormat0 = new DecimalFormat(formatSB.toString() + "E0");		
		
		this.numberFormat1 = new DecimalFormat();
		this.numberFormat1.setGroupingSize(0);
		this.numberFormat1.setMinimumFractionDigits(this.precision);
		this.numberFormat1.setMaximumFractionDigits(this.precision);

		LinkedHashMap m = new LinkedHashMap(8);
		if (isWaveform() == false)
			m.put("type", "double");
		else
			m.put("type", "waveform");

		m.put("disp_low", new Double(this.disp_low));
		m.put("disp_high", new Double(this.disp_high));
		m.put("alarm_low", new Double(this.alarm_low));
		m.put("alarm_high", new Double(this.alarm_high));
		m.put("warn_low", new Double(this.warn_low));
		m.put("warn_high", new Double(this.warn_high));
		m.put("precision", new Integer(this.precision));
		m.put("units", this.units);
		getAVEntry().setMetaData(m);
	}

	// Accessing

	/**
	 * Returns the value of the <i>disp_low </i> field.
	 * 
	 * @return the value of the <i>disp_low </i> field
	 * @see #disp_low
	 */
	protected double getDispLow()
	{
		return disp_low;
	}

	/**
	 * Returns the value of the <i>disp_high </i> field.
	 * 
	 * @return the value of the <i>disp_high </i> field
	 * @see #disp_high
	 */
	protected double getDispHigh()
	{
		return disp_high;
	}

	/**
	 * Returns the value of the <i>alarm_low </i> field.
	 * 
	 * @return the value of the <i>alarm_low </i> field
	 * @see #alarm_low
	 */
	protected double getAlarmLow()
	{
		return alarm_low;
	}

	/**
	 * Returns the value of the <i>alarm_high </i> field.
	 * 
	 * @return the value of the <i>alarm_high </i> field
	 * @see #alarm_high
	 */
	protected double getAlarmHigh()
	{
		return alarm_high;
	}

	/**
	 * Returns the value of the <i>warn_low </i> field.
	 * 
	 * @return the value of the <i>warn_low </i> field
	 * @see #warn_low
	 */
	protected double getWarnLow()
	{
		return warn_low;
	}

	/**
	 * Returns the value of the <i>warn_high </i> field.
	 * 
	 * @return the value of the <i>warn_high </i> field
	 * @see #warn_high
	 */
	protected double getWarnHigh()
	{
		return warn_high;
	}

	/**
	 * Returns the value of the <i>precision </i> field.
	 * 
	 * @return the value of the <i>precision </i> field
	 * @see #precision
	 */
	public int getPrecision()
	{
		return this.precision;
	}

	/**
	 * Returns the value of the <i>units </i> field.
	 * 
	 * @return the value of the <i>units </i> field
	 * @see #units
	 */
	public String getUnits()
	{
		return units;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#getValueLabel(int)
	 */
	public String getDisplayLabel(int index) throws IndexOutOfBoundsException
	{
		if (this.isWaveform())
			return "Waveform";

		if (this.isValid(index))
		{
			return valueToString(index, 0);
		}

		return getValueInfo(index).getSeverity().getLabel();
	}

	public String valueToString(int valueIndex, int item)
			throws IndexOutOfBoundsException
	{
		double d = ((Number) getValue(valueIndex).get(item)).doubleValue();
		if(Math.abs(d) < 1)
		{
			return this.numberFormat0.format(d);
		}
		return this.numberFormat1.format(d);
	}

	public boolean isDiscrete()
	{
		return false;
	}

	public String getRangeLabel(String separator)
	{
		StringBuffer sb = new StringBuffer();

		double validMin = getMinValidValue();
		if (Double.isNaN(validMin))
			return "";

		if(Math.abs(validMin) < 1)
		{
			sb.append(this.numberFormat0.format(validMin));
		}
		else
		{
			sb.append(this.numberFormat1.format(validMin));
		}

		sb.append(separator);

		double validMax = getMaxValidValue();

		if(Math.abs(validMax) < 1)
		{
			sb.append(this.numberFormat0.format(validMax));
		}
		else
		{
			sb.append(this.numberFormat1.format(validMax));
		}


		return sb.toString();
	}

}