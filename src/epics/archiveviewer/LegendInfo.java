package epics.archiveviewer;

/**
 * Encapsulated parameters for the plot legend
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LegendInfo {

	/** flag indicating whether the name of AV entries is to be shown*/
	private boolean showAVEName;
	/** flag indicating whether the archive directory of AV entries is to be shown*/
	private boolean showArchiveName;
	/** flag indicating whether the range of the data from AV entries is to be shown*/
	private boolean showRange;
	/** flag indicating whether the units of an AV entry are to be shown*/
	private boolean showUnits;
	
	/** constructor; creates a default legend info*/
	public LegendInfo()
	{
		this(true, false, true, true);
	}
	
	/**
	 * Constructor
	 * @param _showAVEName flag indicating if the name of an AV entry is to be shown
	 * @param _showArchiveName flag indicating if the archive directory of an AV entry is to be shown
	 * @param _showRange flag indicating if range of an AV entry is to be shown
	 * @param _showUnits flag indicating if units of an AV entry are to be shown
	 */
	public LegendInfo(boolean _showAVEName, boolean _showArchiveName, boolean _showRange, boolean _showUnits)
	{
		setShowAVEName(_showAVEName);
		setShowArchiveName(_showArchiveName);
		setShowRange(_showRange);
		setShowUnits(_showUnits);
	}
	
	/**
	 * Returns true if names of AV entries are to be shown; false otherwise
	 * @return true if names of AV entries are to be shown; false otherwise
	 */
	public boolean getShowAVEName() {
		return showAVEName;
	}
	
	/**
	 * Sets whether names of AV entries are to be displayed
	 * @param flag a flag
	 */
	public void setShowAVEName(boolean flag) {
		this.showAVEName = flag;
	}
	
	/**
	 * Returns true if archive directories of AV entries are to be shown; false otherwise
	 * @return true if archive directories of AV entries are to be shown; false otherwise
	 */
	public boolean getShowArchiveName() {
		return showArchiveName;
	}
	
	/**
	 * Sets whether archive directories of AV entries are to be displayed
	 * @param flag a flag
	 */
	public void setShowArchiveName(boolean flag) {
		this.showArchiveName = flag;
	}
	
	/**
	 * Returns true if ranges of AV entries are to be shown; false otherwise
	 * @return true if ranges of AV entries are to be shown; false otherwise
	 */
	public boolean getShowRange() {
		return showRange;
	}
	
	/**
	 * Sets whether ranges of AV entries are to be displayed
	 * @param flag a flag
	 */
	public void setShowRange(boolean flag) {
		this.showRange = flag;
	}
	
	/**
	 * Returns true if units of AV entries are to be shown; false otherwise
	 * @return true if units of AV entries are to be shown; false otherwise
	 */
	public boolean getShowUnits() {
		return showUnits;
	}
	
	/**
	 * Sets whether units of AV entries are to be displayed
	 * @param flag a flag
	 */
	public void setShowUnits(boolean flag) {
		this.showUnits = flag;
	}
}
