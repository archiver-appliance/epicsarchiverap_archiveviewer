package epics.archiveviewer;

import java.util.Map;

/**
 * The basic unit in ArchiveViewer; either a PV or formula name plus its archive directory
 * @author serge
 */
public class AVEntry
{
	/**
	 * the name of the AV entry
	 */
	private final String name;
	/**
	 * the archive directory of the AV entry
	 */
	private final ArchiveDirectory archiveDirectory;
	/**
	 * A map for meta data
	 */
	private Map metaData;
	
	/**
	 * Constructor; creates a new AV entry
	 * @param _name the name of the AV entry
	 * @param ad the archive directory of the AV entry
	 * @throws Exception if either the name or archive directory are NULL
	 */
	public AVEntry(String _name, ArchiveDirectory ad) throws Exception
	{
		if(_name == null || _name.equals(""))
			throw new IllegalArgumentException("Name must not be null");
		this.name = _name;
		if(ad == null)
			throw new IllegalArgumentException("Archive directory must not be null");
		this.archiveDirectory = ad;
	}

	/**
	 * Returns the name of this AV entry
	 * @return the name of this AV entry
	 */
	public final String getName()
	{
		return this.name;
	}
	
	/**
	 * Returns the archive directory of this AV entry
	 * @return the archive directory of this AV entry
	 */
	public final ArchiveDirectory getArchiveDirectory()
	{
		return this.archiveDirectory;
	}
	
	/**
	 * Sets the meta data map
	 * @param m the map with new meta data
	 */
	public void setMetaData(Map m)
	{
		this.metaData = m;
	}
	
	/**
	 * Returns the meta data map
	 * @return  the meta data map
	 */
	public Map getMetaData()
	{
		return this.metaData;
	}    
	
	/**
	 * Returns the hash code of this AV entry
	 * @see Object#hashCode()
	 */
	public final int hashCode()
	{
		return this.name.hashCode() + this.archiveDirectory.getName().hashCode();
	}
	
	/**
	 * Returns true if the specified object is an AVEntry and it has the same name and archive directory 
	 * as this AV Entry
	 * @see Object#equals(Object o)
	 */
	public final boolean equals(Object o)
	{
		if(o instanceof AVEntry)
		{
			AVEntry other = (AVEntry) o;
			return this.name.equals(other.getName()) && this.archiveDirectory.equals(other.getArchiveDirectory());
		}
		return false;
	}
	
	/**
	 * Returns the string representation of this AVEntry
	 * @return the string representation of this AVEntry
	 */
	public String toString()
	{
	    StringBuffer sb = new StringBuffer();
	    sb.append(this.name);
	    sb.append("#");
	    sb.append(this.archiveDirectory.getName());
	    return sb.toString();
	}
}
