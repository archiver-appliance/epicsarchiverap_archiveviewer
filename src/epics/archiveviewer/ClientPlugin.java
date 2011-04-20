package epics.archiveviewer;

/**
 * Implement this interface to develop new ArchiveViewer client plugins
 * @author Sergei Chevtsov
 */
public interface ClientPlugin
{
	/**
	 * Returns the client name
	 * @return the client name
	 */
	public String getName();
	
	/**
	 * Tries to create a connection to the data server at the specified connection parameter. The
	 * internal state of the object should not change, if the connection could not be established
	 * 
	 * @param param a connection parameter
	 * @param progressInfo the progress interface (for GUI feedback)
	 * @throws Exception
	 */
	public void connect(String param, ProgressTask progressInfo) throws Exception;
	
	/**
	 * Returns the current connection parameter
	 * @return the current connection parameter
	 */
	public String getConnectionParameter();
	
	/**
	 * Reestablishes current connection
	 * @param progressInfo the progress interface for feedback
	 * @throws Exception
	 */
	public void reconnect(ProgressTask progressInfo) throws Exception;

	/**
	 * Returns an array of archive directories known by the server; should not cache them as
	 * ArchiveViewer base does it already
	 * @return an array of archive directories known by the server
	 * @throws Exception
	 */
	public ArchiveDirectory[] getAvailableArchiveDirectories() throws Exception;

	/**
	 * Returns an array of retrieval methods for data that is going to be exported;
	 * the first element should be the default method
	 * @return an array of retrieval methods for data that is going to be exported
	 */
	public RetrievalMethod[] getRetrievalMethodsForExport();

	/**
	 * Returns an array of retrieval methods for data that is going to be plotted
	 * the first element should be the default method
	 * @return an array of retrieval methods for data to be plotted
	 */
	public RetrievalMethod[] getRetrievalMethodsForPlot();

	/**
	 * Returns an array of retrieval methods for data that is going to be used to calculate a formula; 
	 * the first element should be the default method
	 * @return an array of retrieval methods for data that is going to be used to calculate a formula
	 */
	public RetrievalMethod[] getRetrievalMethodsForCalculation();

	/**
	 * Returns the retrieval method with the specified name
	 * @return the retrieval method with the specified name
	 */
	public RetrievalMethod getRetrievalMethod(String methodName);

	/**
	 * Returns a formatted description of the server
	 * @return a formatted description of the server
	 * @throws Exception
	 */
	public String getServerInfoText() throws Exception;

	/**
	 * Retrieves archived data for specified AV entries and the specified request parameters;
	 * returns an array of values containers (elements can be NULL)
	 * @param archiveEntries the AV entries whose data is to be retrieved
	 * @param requestObject the request parameters
	 * @param progressInfo the progress interface for feedback
	 * @return an array of values containers (may contain NULL elements)
	 * @throws Exception
	 */
	public ValuesContainer[] retrieveData(
			AVEntry[] archiveEntries, 
			RequestObject requestObject, 
			ProgressTask progressInfo) throws Exception;

	/**
	 * Returns the maximum number of values the server can retrieve per PV per request;
	 * due to desired interactions with users, ArchiveViewer base has an own limit of 1000
	 * @param nrPVs number of PVs whose request for data is going to be sent (may be unnecessary) 
	 * @return the maximum number of values the server can retrieve per PV per request
	 */
	public int getMaxNrValuesPerPVPerRequest(int nrPVs);

	/**
	 * Sends a server query for PV names in the specified archive directory that match specified regular
	 * expression pattern; returns an array of found AV entries
	 * @param ad the archive directory to search within
	 * @param pattern a regular expression for PV names to match
	 * @param progressInfo the progress interface for feedback
	 * @return an array of found AV entries
	 * @throws Exception
	 */
	public AVEntry[] search(ArchiveDirectory ad, String pattern, ProgressTask progressInfo)
			throws Exception;
	
	/**
	 * Returns an AVEntryInfo object for the specified AV entry; may return NULL
	 * @param ave the AV entry
	 * @return an AVEntryInfo object for the specified AV entry
	 */
	public AVEntryInfo getAVEInfo(AVEntry ave);
}