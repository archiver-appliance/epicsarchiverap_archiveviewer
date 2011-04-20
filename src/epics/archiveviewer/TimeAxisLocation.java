package epics.archiveviewer;

/**
 * Encapsulates information on available time axis locations
 * @author serge
 */
public abstract class TimeAxisLocation {
	/** time axis location on the top*/
	public static final TimeAxisLocation TOP = new TimeAxisLocation("top"){};
	/** time axis location on the bottom*/
	public static final TimeAxisLocation BOTTOM = new TimeAxisLocation("bottom"){};
	/** time axis not visible */
	public static final TimeAxisLocation NOT_VISIBLE = new TimeAxisLocation(""){};
	
	/**
	 * Returns the TimeAxisLocation object for specified string
	 * @param s the string representation of a time axis location
	 * @return the TimeAxisLocation object for specified string
	 */
	public static TimeAxisLocation getAxisLocation(String s)
	{
		if(s.equalsIgnoreCase("top"))
			return TOP;
		if(s.equalsIgnoreCase("bottom"))
			return BOTTOM;
		if(s == null || s.trim().equals(""))
			return NOT_VISIBLE;
		return null;
	}
	/** the default string representation of this time axis location*/
	private final String defaultStringRepresentation;
	
	/**
	 * Constructor
	 * @param s the string representation of this time axis location
	 */
	protected TimeAxisLocation(String s)
	{
		this.defaultStringRepresentation = s;
	}
	
	/**
	 * Returns the string representation of this time axis location
	 * @return the string representation of this time axis location
	 */
	public String toString()
	{
		return this.defaultStringRepresentation;
	}

}
