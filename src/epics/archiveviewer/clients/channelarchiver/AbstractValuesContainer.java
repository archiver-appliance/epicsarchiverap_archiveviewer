package epics.archiveviewer.clients.channelarchiver;

import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;

/**
 * Encapsulates archived data which is the result of the archiver.values
 * request. Simulates also a formula result for uniform access methods. The
 * container contains meta information (limits, units, etc.) and the retrieved
 * <CODE>ValueInfo</CODE>s.
 * 
 * @author Craig McChesney, Sergei Chevtsov
 */
public abstract class AbstractValuesContainer implements ValuesContainer
{

	// Instance Variables ======================================================
	private final AVEntry avEntry;
	
	private final double validMin;
	
	private final double validMax;
	
	private final double validMinPos;
	
	/** a flag indicating if this <CODE>ValuesContainer</CODE> is a waveform */
	private final boolean isWaveform;

	/**
	 * the data type of all the values stored inside this <CODE>ValuesContainer
	 * </CODE>
	 */
	private Class dataType;

	/**
	 * a vector of retrieved <CODE>ValuesInfo</CODE>s; needs to be expandable
	 * if more than 10000 values requested (server issues)
	 */
	private Vector valueInfos;


	protected AbstractValuesContainer(AVEntry ae, double vMin, double vMax, double vMinPos, boolean isWF) throws IllegalArgumentException, NullPointerException
	{
		if(ae == null)
			throw new NullPointerException(
				"A ValuesContainer must be instantiated for a valid archive entry");
		this.avEntry = ae;
		this.validMin = vMin;
		this.validMax = vMax;
		this.validMinPos = vMinPos;
		this.isWaveform = isWF;
	}
	
	/***/
	protected final ValueInfo getValueInfo(int index)
	{
		return ((ValueInfo) valueInfos.get(index));
	}
	
	protected final void setValueInfos(Vector _valueInfos)
	{
		this.valueInfos = _valueInfos;
	}

	/**
	 * Returns a <CODE>Vector</CODE> containing all <CODE>ValueInfo</CODE> s
	 * of this <CODE>ValuesContainer</CODE>
	 * 
	 * @return a <CODE>Vector</CODE> containing all <CODE>ValueInfo</CODE> s
	 *         of this <CODE>ValuesContainer</CODE>
	 */
	protected Vector getValueInfos()
	{
		return this.valueInfos;
	}
	
	/**
	 * Adds the <CODE>ValueInfo</CODE> s in the specified Vector to this
	 * <CODE>ValuesContainer</CODE>.
	 * 
	 * @param newValueInfos
	 *            a <CODE>Vector</CODE> of new ValueInfos
	 */
	protected void addValueInfos(Vector newValueInfos)
	{
		this.valueInfos.addAll(newValueInfos);
	}
	
	/** Removes the first value info in this <CODE>ValuesContainer</CODE> */
	protected void removeFirstValueInfo()
	{
		this.valueInfos.removeElementAt(0);
	}
	
	protected void removeLastValueInfo()
	{
		this.valueInfos.removeElementAt(getNumberOfValues() - 1);
	}
	
	/**
	 * @param class1
	 */
	protected void setDataType(Class class1)
	{
		dataType = class1;
	}


	// Accessing ===============================================================

	public AVEntry getAVEntry()
	{
		return this.avEntry;
	}

	/**
	 * Returns true if this <CODE>ValuesContainer</CODE> is for waveform PV;
	 * false otherwise
	 * 
	 * @return true if this <CODE>ValuesContainer</CODE> is for waveform PV;
	 *         false otherwise
	 * @see #isWaveform
	 */
	public boolean isWaveform()
	{
		return isWaveform;
	}

	/**
	 * Returns the <CODE>ValueInfo</CODE> at the specified array address
	 * 
	 * @see #valueInfos
	 * @param index
	 *            array address to look at
	 * @return the <CODE>ValueInfo</CODE> at the specified array address
	 * @throws ArrayIndexOutOfBoundsException
	 *             if no <CODE>ValueInfo</CODE> exists at the specified
	 *             position
	 */
	public Vector getValue(int index) throws ArrayIndexOutOfBoundsException
	{
		return getValueInfo(index).getValue();
	}

	/**
	 * Returns the data type of the values stored inside this <CODE>
	 * ValuesContainer</CODE>
	 * 
	 * @return the data type of the values stored inside this <CODE>
	 *         ValuesContainer</CODE>
	 * @see #dataType
	 */
	public Class getDataType()
	{
		return dataType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#clear()
	 */
	public void clear()
	{
		if (valueInfos != null)
			valueInfos.removeAllElements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#getNumberOfValues()
	 */
	public int getNumberOfValues() throws NullPointerException
	{
		return this.valueInfos.size();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#getTimestampInMsec(int)
	 */
	public double getTimestampInMsec(int index)
			throws IndexOutOfBoundsException, NullPointerException
	{
		return getValueInfo(index).getTimeStampInMsec();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#isValid(int)
	 */
	public boolean isValid(int index) throws IndexOutOfBoundsException,
			NullPointerException
	{
		return getValueInfo(index).isValid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#release()
	 */
	public void release()
	{
		// TODO Auto-generated method stub

	}
	
	public String getStatus(int index)
	{
		return 
			this.getValueInfo(index).getSeverity().getLabel() +
			"\t" +
			this.getValueInfo(index).getStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ArchiveEntry#getDimension()
	 */
	public int getDimension() throws NullPointerException
	{
		if (valueInfos == null || valueInfos.size() == 0)
			throw new NullPointerException("the archive entry for " + this.avEntry.getName()
					+ "contains no values");

		//ignore null valueInfos
		int i = 0;
		ValueInfo vi = null;
		do
		{
			vi = (ValueInfo) valueInfos.get(i);
			i++;
		}
		while (vi == null && i < valueInfos.size());
		
		if (vi == null)
			throw new NullPointerException("the archive entry for " + this.avEntry.getName()
					+ "contains no values");
		else
			return vi.getValue().size();
	}
	
	public double getMaxValidValue() {
		return this.validMax;
	}

	public double getMinPosValidValue() {
		return this.validMinPos;
	}

	public double getMinValidValue() {
		return this.validMin;
	}
}