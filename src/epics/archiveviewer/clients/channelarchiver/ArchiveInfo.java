package epics.archiveviewer.clients.channelarchiver;

import epics.archiveviewer.ArchiveDirectory;

/**
 * Encapsulates the information about an archive.
 * 
 * @author Craig McChesney, Sergei Chevtsov
 */
public class ArchiveInfo implements ArchiveDirectory
{

	// Instance Variables ======================================================

	/**
	 * the numeric key that is used by the data server to select the appropriate
	 * archive
	 */
	private Integer key;

	/**
	 * the description of the archive (often cryptic) that can be a help for the
	 * user when selecting an archive
	 */
	private String name;

	/**
	 * the path to the index file on the file system where the data server runs
	 */
	private String path;

	// Constructors ============================================================

	/**
	 * Constructs a new <CODE>ArchiveInfo</CODE> object.
	 * 
	 * @param aKey
	 *            see {@link #key key}
	 * @param aName
	 *            {@link #name name}
	 * @param aPath
	 *            {@link #path path}
	 * @throws IllegalArgumentException
	 *             when aPath and/or aName are <CODE>NUll</CODE> or empty
	 *             <CODE>String</CODE> s
	 */
	protected ArchiveInfo(int aKey, String aName, String aPath)
			throws IllegalArgumentException
	{
		if (aName == null || aName.equals(""))
			throw new NullPointerException(
					"the name of the archive can not be null or an empty string");
		if (aPath == null || aPath.equals(""))
			throw new NullPointerException(
					"the path of the archive can not be null or an empty string");
		key = new Integer(aKey);
		name = aName;
		path = aPath;
	}

	// State Accessing =========================================================

	/**
	 * Returns the key of this <CODE>ArchiveInfo</CODE>
	 * 
	 * @return the key of this <CODE>ArchiveInfo</CODE>
	 * @see #key
	 */
	public Object getIDKey()
	{
		return key;
	}

	/**
	 * Returns the name of this <CODE>ArchiveInfo</CODE>
	 * 
	 * @return the name of this <CODE>ArchiveInfo</CODE>
	 * @see #name
	 */
	public String getName()
	{
		return name;
	}

	public boolean equals(Object o)
	{
		if (o instanceof ArchiveDirectory)
		{
			return this.key.equals(((ArchiveDirectory) o).getIDKey());
		}
		else
			return false;
	}

	public int compareTo(Object arg0) throws ClassCastException
	{
		return this.toString().compareTo(arg0.toString());
	}
	
	public String toString()
	{
		return this.name + " " + this.path;
	}
}