package epics.archiveviewer;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Extend this class to create an exporter plugin
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Exporter {
	
	/**
	 * a constant for exported data only
	 */
	public static final int EXPORT_DATA_ONLY = 0;

	/**
	 * a constant for exporting data and status
	 */
	public static final int EXPORT_DATA_AND_STATUS = 1;
	
	/**
	 * The default constructor; must be present in all subclasses!!!
	 */
	public Exporter()
	{
		
	}
	
	/**
	 * Returns a short string that identifies this Exporter and is descriptive enough to let 
	 * the user make the right selection (e.g. "matlab")
	 * @return a short string that identifies this Exporter and is descriptive enough to let 
	 * the user make the right selection
	 */
	public abstract String getId();
	
	/**
	 * Returns the extension of this Exporter
	 * @return the extension of this Exporter
	 */
	public abstract String getExt();
	/**
	 * Exports data from specified values containers between specified indices to specified writer
	 * @param vcs the values containers to be exported
	 * @param firstIndex the first index of data to be exported
	 * @param lastIndex the last index of data to be exported
	 * @param writer the writer to which data is written
	 * @param append a flag, indicating if data should be appended to the writer or not
	 * @param detailsLevel see fields of this class
	 * @param tsFormat a string containing the Java timestamp format (may be NULL)
	 * @throws Exception
	 */
	public abstract void export(ValuesContainer[] vcs, int firstIndex, int lastIndex, Writer writer,
			boolean append, int detailsLevel, String tsFormat) throws Exception;
}
