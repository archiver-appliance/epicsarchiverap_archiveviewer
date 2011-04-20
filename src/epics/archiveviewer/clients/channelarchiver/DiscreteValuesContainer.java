package epics.archiveviewer.clients.channelarchiver;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.RequestObject;

/**
 * A ValuesContainer for enumerated values as specified in the archiver manual.
 * The server returns the enumerated values as integers. One must then look at
 * {@link #states}to determine what the values mean.
 * 
 * @author Sergei Chevtsov
 * @author Craig McChesney
 * @see ValuesContainer
 */
public class DiscreteValuesContainer extends AbstractValuesContainer
		implements Serializable
{

	/** An array of textual representations of the integer values */
	private String[] states;

	protected DiscreteValuesContainer(AVEntry ae, double vMin, double vMax, double vMinPos, Class dataType, Vector valueInfos, String[] states,
			boolean isWaveform)
	{
		super(ae, vMin, vMax, vMinPos, isWaveform);
		super.setValueInfos(valueInfos);
		super.setDataType(dataType);
		this.states = states;
		LinkedHashMap m = new LinkedHashMap(this.states.length);
		String type = "";
		if(getDataType().equals(String.class))
			type = "string";
		else if(getDataType().equals(Integer.class))
			type = "enum";
		else if(isWaveform())
			type = "waveform";
		
		m.put("type", type);
		if(states != null || states.length > 0)
		{
			for(int i=0; i < this.states.length; i++)
			{
				m.put(new Integer(i), this.states[i]);
			}
		}
		
		getAVEntry().setMetaData(m);
	}

	/**
	 * Returns the array of <CODE>String</CODE> s that explain the enumerated
	 * values.
	 * 
	 * @return the array of <CODE>String</CODE> s that explain the enumerated
	 *         values.
	 * @see #states
	 */
	protected String[] getStates()
	{
		return states;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#getUnits()
	 */
	public String getUnits() throws NullPointerException
	{
		return "";
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
	
	public String valueToString(int value, int item) throws IndexOutOfBoundsException
	{
		try
		{
			int state = ((Integer) this.getValue(value).get(item)).intValue();
			return states[state];
		}
		catch(Exception e)
		{
			return this.getValue(value).get(item).toString();
		}
	}
	
	public boolean isDiscrete()
	{
		return true;
	}
	
	public int getPrecision()
	{
		//only integers supported
		return 0;
	}
	
	public double getMaxValidValue() {
		if(Number.class.isAssignableFrom(getDataType()))
			return super.getMaxValidValue();
		return Double.NaN;
	}

	public double getMinPosValidValue() {
		if(Number.class.isAssignableFrom(getDataType()))
			return super.getMinPosValidValue();
		return Double.NaN;
	}

	public double getMinValidValue() {
		if(Number.class.isAssignableFrom(getDataType()))
			return super.getMinValidValue();
		return Double.NaN;
	}

	public String getRangeLabel(String separator) {
		if(Number.class.isAssignableFrom(getDataType()))
		{
			StringBuffer sb = new StringBuffer();
			sb.append((int) super.getMinValidValue());
			sb.append(separator);
			sb.append((int) super.getMaxValidValue());
			return sb.toString();
		}
		else if(getDataType().equals(String.class))
			return "string";
		return "N/A";
	}
}