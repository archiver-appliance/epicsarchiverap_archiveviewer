package epics.archiveviewer.clients.channelarchiver;

import java.io.Serializable;

/**
 * Encapsulates information about an archiver severity
 * 
 * @author Craig McChesney, Sergei Chevtsov
 */
public class SeverityInfo implements Serializable
{

	/**
	 * Returns an artificial <CODE>SeverityInfo</CODE> for data that is the
	 * result of a calculation and not archiving
	 * @param display TODO
	 * @param forValidValue
	 *            flag indicating if the desired severity is for a valid or
	 *            invalid value
	 * 
	 * @return an artificial <CODE>SeverityInfo</CODE> for data that is the
	 *         result of a calculation and not archiving
	 */
	protected static SeverityInfo createSeverity(String errorDisplay, boolean forValidValue)
	{
		if (forValidValue == true)
			return new SeverityInfo(0, "NO_ALARM", true, true);
		else
			return new SeverityInfo(-1, errorDisplay, false, true);
	}

	// Instance Variables ======================================================

	/** the id number of the severity */
	private int num;

	/** the string representation of the severity */
	private String label;

	/** the flag indicating the existance of a value */
	private boolean hasValue;

	/** the flag indicating if the status is text */
	private boolean isStatusText;

	/**
	 * Constructs a new <CODE>SeverityInfo</CODE> object
	 * 
	 * @param id
	 *            see {@link #num num}
	 * @param idLabel
	 *            see {@link #label label}
	 * @param hasVal
	 *            see {@link #hasValue hasValue}
	 * @param txtStatus
	 *            see {@link #isStatusText isStatusText}
	 */
	protected SeverityInfo(int id, String idLabel, boolean hasVal,
			boolean txtStatus)
	{
		num = id;
		label = idLabel;
		hasValue = hasVal;
		isStatusText = txtStatus;
	}

	// Public Accessing ========================================================

	/**
	 * Returns the severity id number of this <CODE>SeverityInfo</CODE>
	 * 
	 * @return the severity id number of this <CODE>SeverityInfo</CODE>
	 * @see #num
	 */
	protected int getSeverityNum()
	{
		return num;
	}

	/**
	 * Returns the severity label of this <CODE>SeverityInfo</CODE>
	 * 
	 * @return the severity label of this <CODE>SeverityInfo</CODE>
	 * @see #label
	 */
	protected String getLabel()
	{
		return label;
	}

	/**
	 * Returns false, if this <CODE>SeverityInfo</CODE> belongs to a <CODE>
	 * ValueInfo</CODE> with no value; else true
	 * 
	 * @return false, if this <CODE>SeverityInfo</CODE> belongs to a <CODE>
	 *         ValueInfo</CODE> with no value; else true
	 * @see #hasValue
	 */
	protected boolean hasValue()
	{
		return hasValue;
	}

	/**
	 * Returns true, if the status of the <CODE>ValueInfo</CODE>
	 *  <CODE>
	 * ValueInfo</CODE> to which this <CODE>SeverityInfo</CODE> belongs is
	 * text; false otherwise
	 * 
	 * @return true, if the status of the <CODE>ValueInfo</CODE>
	 *  <CODE>
	 *         ValueInfo</CODE> to which this <CODE>SeverityInfo</CODE>
	 *         belongs is text; false otherwise
	 * @see #hasValue
	 */
	protected boolean isStatusText()
	{
		return isStatusText;
	}

	/**
	 * Return the <CODE>String</CODE> representation of this <CODE>
	 * SeverityInfo</CODE>
	 * 
	 * @return the <CODE>String</CODE> representation of this <CODE>
	 *         SeverityInfo</CODE>
	 */
	public String toString()
	{
		return num + "\t" + label + "\t" + (hasValue ? "true" : "false")
				+ (isStatusText ? "true" : "false");
	}
}