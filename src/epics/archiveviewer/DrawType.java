package epics.archiveviewer;

/**
 * Encapsulates information on available draw types
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class DrawType {
	/**
	 * The draw type "steps"
	 */
	public static final DrawType STEPS = new DrawType("steps"){};
	/**
	 * The draw type "scatter"
	 */
	public static final DrawType SCATTER = new DrawType("scatter"){};
	/**
	 * The draw type "lines"
	 */
	public static final DrawType LINES = new DrawType("lines"){};
	
	/**
	 * Returns the DrawType object for specified string 
	 * @param s the string representation of a DrawType
	 * @return the DrawType object for specified string 
	 */
	public static DrawType getDrawType(String s)
	{
		if(s.equalsIgnoreCase("scatter"))
			return SCATTER;
		if(s.equalsIgnoreCase("steps"))
			return STEPS;
		if(s.equalsIgnoreCase("lines"))
			return LINES;
		return null;
	}
	
	/** the default string representation of this draw type*/
	private final String defaultStringRepresentation;
	
	/***
	 * Constructor; creates new draw type for the specified string
	 * @param s the string representation of this DrawType
	 */
	protected DrawType(String s)
	{
		this.defaultStringRepresentation = s;
	}
	
	/**
	 * Returns the string representation of this DrawType
	 * @return the string representation of this DrawType
	 */
	public String toString()
	{
		return this.defaultStringRepresentation;
	}
}
