package epics.archiveviewer;

/**
 * The interface for a logical archive directory
 * @author serge
 *
 */
public interface ArchiveDirectory extends Comparable
{
	/**
	 * Returns the name of the archive, preferably trimmed; but the ArchiveViewer application takes care
	 * of it, too
	 * @return the name of the archive
	 */
	public String getName();

	/**
	 * Returns the archive key object that the client might use for server requests. If your client uses
	 * archive names directly, return those here
	 * @return the archive key object
	 */
	public Object getIDKey();
	
	/**
	 * Returns true, if the parameter is an archive directory with the same key object
	 * otherwise returns false
	 * @param o an object
	 * @return true, if the parameter is an archive directory with the same key object
	 * otherwise returns false
	 */

	public boolean equals(Object o);
	
	/**
	 * Returns the string representation of this archive object; should be descriptive enough to let the 
	 * user pick the right archive directory 
	 * @return the string representation of this archive object;
	 */
	public String toString();
}