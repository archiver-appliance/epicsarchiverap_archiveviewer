package epics.archiveviewer;


/**
 * Encapsulates information on available range axes locations
 * @author serge
 */
public abstract class RangeAxisLocation {
	/** The range axis location on the left*/
	public static final RangeAxisLocation LEFT = new RangeAxisLocation("left"){};
	/** The range axis location on the right*/
	public static final RangeAxisLocation RIGHT = new RangeAxisLocation("right"){};
	/** The range axis not visible*/
	public static final RangeAxisLocation NOT_VISIBLE = new RangeAxisLocation(""){};
	
	/**
	 * Returns the RangeAxisLocation object for specified string
	 * @param s the string representation of a range axis location
	 * @return the RangeAxisLocation object for specified string
	 */
	public static RangeAxisLocation getAxisLocation(String s)
	{
		if(s.equalsIgnoreCase("left"))
			return LEFT;
		if(s.equalsIgnoreCase("right"))
			return RIGHT;
		if(s == null || s.trim().equals(""))
			return NOT_VISIBLE;
		return null;
	}
	
	/** the default string representation of this range axis location*/
	private final String defaultStringRepresentation;
	
	/**
	 * Constructor
	 * @param s the string representation of this range axis location
	 */
	protected RangeAxisLocation(String s)
	{
		this.defaultStringRepresentation = s;
	}
	
	/**
	 * Returns the string representation of this range axis location
	 * @return the string representation of this range axis location
	 */
	public final String toString()
	{
		return this.defaultStringRepresentation;
	}
}

