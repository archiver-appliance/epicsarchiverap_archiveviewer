package epics.archiveviewer.clients.channelarchiver;

import java.io.Serializable;
import java.util.Vector;

/**
 * Encapsulates the information about an archived value
 * 
 * @author Craig McChesney
 * @author Peregrine McGehee
 * @author Sergei Chevtsov
 */
public class ValueInfo implements Serializable
{

	/** the archiving time in seconds since 1/1/1970 */
	protected long seconds;

	/** the nanoseconds part of the archiving time */
	protected long nanoseconds;

	/** the status of this <CODE>ValueInfo</CODE> */
	protected String status;

	/**
	 * the severity information about this <CODE>ValueInfo</CODE>
	 * 
	 * @see SeverityInfo
	 */
	protected SeverityInfo severity;

	/**
	 * a <CODE>Vector</CODE> of actual archived values Contains at least one
	 * element (which can be null) if a PV to which this <CODE>ValueInfo
	 * </CODE> belongs is a waveform, the vector contains more than one element
	 */
	protected Vector value;
	
	private final boolean isValid;

	/**
	 * Creates a new <CODE>ValueInfo</CODE> object The parameters val must
	 * contain at least one element!
	 * 
	 * @param secs
	 *            see {@link #seconds seconds}
	 * @param nanos
	 *            see {@link #nanoseconds nanoseconds}
	 * @param status
	 *            see {@link #status status}
	 * @param severity
	 *            see {@link #severity severity}
	 * @param val
	 *            see {@link #value value}
	 * @throws IllegalArgumentException
	 *             if val is null or contains no values
	 */
	protected ValueInfo(long secs, long nanos, String _status,
			SeverityInfo _severity, Vector val) throws IllegalArgumentException
	{
		seconds = secs;
		nanoseconds = nanos;
		if(val == null || val.size() == 0 ||
				val.get(0) == null)
		{
			this.value = new Vector();
			this.value.add(new Integer(0));
			this.severity = SeverityInfo.createSeverity("NULL", false);
			
		}
		else
		{
			this.value = val;
			this.severity = _severity;
		}
		this.status = _status;
		
		int num = severity.getSeverityNum();
		if (num == -1 || num == 3 || num == 3904 || num == 3872 || num == 3848)
			isValid = false;
		else
			isValid = severity.hasValue();
	}

	// Accessing ===============================================================

	/**
	 * Returns the status of this <CODE>ValueInfo</CODE>
	 * 
	 * @return the status of this <CODE>ValueInfo</CODE>
	 * @see #status
	 */
	protected String getStatus()
	{
		return status;
	}

	/**
	 * Returns the severity info of this <CODE>ValueInfo</CODE>
	 * 
	 * @return the severity info of this <CODE>ValueInfo</CODE>
	 * @see #severity
	 */
	protected SeverityInfo getSeverity()
	{
		return severity;
	}

	/**
	 * Returns the archived time of this <CODE>ValueInfo</CODE> in msec since
	 * 1/1/1970
	 * 
	 * @return the archived time of this <CODE>ValueInfo</CODE> in msec since
	 *         1/1/1970
	 */
	protected double getTimeStampInMsec()
	{
		double result = seconds * 1.0e3 + nanoseconds * 1.0e-6;
		return result;
	}

	//for export
	/**
	 * Returns the seconds part of the timestamp of this <CODE>ValueInfo
	 * </CODE>
	 * 
	 * @return the seconds part of the timestamp of this <CODE>ValueInfo
	 *         </CODE>
	 */
	protected long getSeconds()
	{
		return seconds;
	}

	/**
	 * Returns the nanoseconds part of the timestamp of this <CODE>ValueInfo
	 * </CODE>
	 * 
	 * @return the nanoseconds part of the timestamp of this <CODE>ValueInfo
	 *         </CODE>
	 */
	protected long getNanoseconds()
	{
		return nanoseconds;
	}

	/**
	 * Returns the archived value of this <CODE>ValueInfo</CODE>
	 * 
	 * @return the archived value of this <CODE>ValueInfo</CODE>
	 * @see #value
	 */
	protected Vector getValue()
	{
		return this.value;
	}

	/**
	 * Returns false, if the archived data is not valid; true otherwise
	 * 
	 * @return false, if the archived data is not valid; true otherwise
	 */
	protected boolean isValid()
	{
		return this.isValid;
	}
}