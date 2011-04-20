package epics.archiveviewer;

/**
 * Encapsulates parameters of a retrieval methods
 * @author serge
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RetrievalMethod extends Object
{
	/** the key of this method the server might expect*/
	private final Object key;

	/** the user-friendly, short name of this retrieval method*/
	private final String name;

	/** the method description*/
	private final String description;

	/** flag indicating if data that was requested with this method returns with aligned timestamps*/
	private final boolean timestampAligned;

	/** flag indicating if data that was requested with this method returns with reduced resolution*/
	private final boolean resolutionReduced;

	/***
	 * Constructor
	 * @param key the method key the server might expect
	 * @param name the method name (short)
	 * @param description the description of this method
	 * @param timestampAligned the flag indicating if this method aligns timestamps
	 * @param resolutionReduced the flag indicating if this method resolution
	 */
	protected RetrievalMethod(Object key, String name, String description,
			boolean timestampAligned, boolean resolutionReduced)
	{
		this.key = key;
		this.name = name;
		this.description = description;
		this.timestampAligned = timestampAligned;
		this.resolutionReduced = resolutionReduced;
	}

	/**
	 * Returns the name of this method (should be short)
	 * @return the name of this method
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Returns the key of the method (might be useful for the server)
	 * @return the key of the method
	 */
	public Object getKey()
	{
		return this.key;
	}

	/** @see #getName()*/
	public String toString()
	{
		return getName();
	}
	
	/**
	 * Returns description of this method
	 * @return description of this method
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Returns true if this method reduces resolution; false otherwise
	 * @return true if this method reduces resolution; false otherwise
	 */
	public boolean reducesResolution()
	{
		return this.resolutionReduced;
	}

	/**
	 * Returns true if this method aligns timestamps; false otherwise
	 * @return true if this method aligns timestamps; false otherwise
	 */
	public boolean alignsTimestamps()
	{
		return this.timestampAligned;
	}

}