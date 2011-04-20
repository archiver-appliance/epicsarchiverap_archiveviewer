package epics.archiveviewer;

/**
 * Encapsulates information on available range axis scale types
 * @author serge
 */
public abstract class RangeAxisType {
	/** normal range axis scale*/
	public static final RangeAxisType NORMAL = new RangeAxisType("normal"){};
	/** logarithmic range axis scale*/
	public static final RangeAxisType LOG = new RangeAxisType("log"){};	
	
	/**
	 * Returns the RangeAxisType object for specified string
	 * @param s the string representation of a range axis type
	 * @return the RangeAxisType object for specified string
	 */
	public static RangeAxisType getRangeAxisType(String s)
	{
		if(s.equalsIgnoreCase("normal"))
			return NORMAL;
		if(s.equalsIgnoreCase("log"))
			return LOG;
		return null;
	}
	
	/** the default string representation of this range axis type*/
	private final String defaultStringRepresentation;
	
	/**
	 * Constructor
	 * @param s the string representation of this range axis type
	 */
	protected RangeAxisType(String s)
	{
		this.defaultStringRepresentation = s;
	}
	
	/**
	 * Returns the string representation of this range axis type
	 * @return the string representation of this range axis type
	 */
	public String toString()
	{
		return this.defaultStringRepresentation;
	}
}
