package epics.archiveviewer.base.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.Exporter;
import epics.archiveviewer.ValuesContainer;

/**
 * Provides functionality for exporting data retrieved from the archiver server.
 * 
 * @author Craig McChesney, Sergei Chevtsov
 */
public class SpreadSheetExporter extends Exporter
{

	/** the default date format for timestamps */
	private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss");
	
	//nanoseconds are 10-9 seconds
	private static final DecimalFormat NSECS_FORMAT = new DecimalFormat("#########");
	
	/**
	 * the flag indicating if nanoseconds should be appended
	 */
	private static boolean appendNsecs = true;

	/** the date format for timestamps */
	private static SimpleDateFormat dateFormat;

	private static void printTimestamp(PrintWriter pw, ValuesContainer vc,
			int index) throws Exception
	{
		double timestampInMSecs = vc.getTimestampInMsec(index);
		if (appendNsecs == true)
		{
			//print seconds (in whatever format desired).nanoseconds
			long timestampInSecs = (long) (timestampInMSecs/1000);
			pw.print(dateFormat.format(new Date(timestampInSecs * 1000)));

			double nsecs = (timestampInMSecs/1000 - timestampInSecs) * 1000000000;
			pw.print("." + NSECS_FORMAT.format(nsecs));
		}
		else
		{
			//print milliseconds in whatever format desired
			pw.print(dateFormat.format(new Date((long)timestampInMSecs)));
		}
		pw.print("\t");
	}

	/**
	 * Exports the value vector of the specified <CODE>ValuesContainer</CODE>.
	 * The values of the waveform are treated like elements of an array, the
	 * values header of such VC would look like this: #TimeStamp VCNAME[0]
	 * VCNAME[1] ... STATUS SEVERITY The last two fields are optional and their
	 * occurance depends on the detailsLevel parameter.
	 * 
	 * @param pw
	 *            the PrintWriter to which the data is written
	 * @param vc
	 *            a <CODE>ValuesContainer</CODE> of a waveform PV
	 * @param detailsLevel
	 *            see {@link ClientPlugin ClientFacade}
	 * @throws IOException
	 *             if IO errors occur
	 */
	private static void exportWaveform(PrintWriter pw, ValuesContainer vc, int firstIndex, int lastIndex,
			int detailsLevel, boolean exportHeader) throws Exception
	{
		int i = 0;
		int dimension = vc.getDimension();
		if(exportHeader)
		{
			//export the header first
			pw.print("#Timestamp\t");
			for (i = 0; i < dimension; i++)
			{
				pw.print(vc.getAVEntry().getName() + "[" + i + "]" + "\t");
			}
			if (detailsLevel == EXPORT_DATA_AND_STATUS)
			{
				pw.print("Status \t");
			}
			pw.println();
		}

		//now the actual values
		int numberOfValues = vc.getNumberOfValues();
		//assume that all VCs contain the same amount of ValueInfos
		for (i = firstIndex; i <= lastIndex; i++)
		{
			if(i==numberOfValues)
				return;
			printTimestamp(pw, vc, i);
			for (int j = 0; j < dimension; j++)
			{
				pw.print(vc.valueToString(i, j) + "\t");
			}
			if (detailsLevel == EXPORT_DATA_AND_STATUS)
			{
				//print severity
				pw.print(vc.getStatus(i) + "\t");
			}
			pw.println();
		}
	}

	/**
	 * Exports the first elements of the value vectors from the specified <CODE>
	 * ValuesContainer</CODE>s. The header for the values looks like this:
	 * #Timestamp VCNAME1 STATUS SEVERITY VCNAME2 STATUS SEVERITY VCNAME3 etc.
	 * The STATUS and SEVERITY fields are optional and their occurance depends
	 * on the detailsLevel parameter
	 * 
	 * @param pw
	 *            the PrintWriter to which the data is written
	 * @param vcs
	 *            a Vector containing non-null <CODE>ValuesContainer</CODE> s
	 * @param detailsLevel
	 *            see {@link ClientPlugin ClientFacade}
	 * @throws IOException
	 *             if IO errors occur
	 */
	private static void exportRegularData(PrintWriter pw, Vector vcs, int firstIndex, int lastIndex,
			int detailsLevel, boolean exportHeader) throws Exception
	{
		int i = 0;
		if(exportHeader)
		{
			//export the header first
			pw.print("#Timestamp\t");
			for (i = 0; i < vcs.size(); i++)
			{
				pw.print(((ValuesContainer) vcs.get(i)).getAVEntry().getName() + "\t");
				if (detailsLevel == EXPORT_DATA_AND_STATUS)
				{
					pw.print("Status \t");
				}
			}
			pw.println();
		}

		//now the actual values
		ValuesContainer vc = (ValuesContainer) vcs.get(0);
		int numberOfValues = vc.getNumberOfValues();

		//assume that all VCs contain the same amount of ValueInfos
		for (i = firstIndex; i <= lastIndex; i++)
		{
			if(i == numberOfValues)
				return;
			printTimestamp(pw, vc, i);
			for (int j = 0; j < vcs.size(); j++)
			{
				vc = (ValuesContainer) vcs.get(j);
				pw.print(vc.valueToString(i, 0) + "\t");
				if (detailsLevel == EXPORT_DATA_AND_STATUS)
				{
					//print severity
					pw.print(vc.getStatus(i) + "\t");
				}
			}
			pw.println();
		}
	}

	/**
	 * Exports the meta information of each specified <CODE>ValuesContainer
	 * </CODE>. see the archiver manual for details
	 * 
	 * @param pw
	 *            the PrintWriter to which the data is written
	 * @param vcs
	 *            a Vector containing non-null the <CODE>ValuesContainer
	 *            </CODE> s
	 * @throws IOException
	 *             if IO errors occur
	 */

	private static void exportMetaInfo(PrintWriter pw, Vector vcs)
	{
		AVEntry ae = null;
		for (int i = 0; i < vcs.size(); i++)
		{
			ae = ((ValuesContainer)vcs.get(i)).getAVEntry();
			pw.println("# " + ae.getName());
	    	pw.println("# " + ae.getArchiveDirectory().getName());
	    	Map m = ae.getMetaData();
	    	if(m != null && m.isEmpty() == false)
	    	{	
		    	Iterator metaIt = m.entrySet().iterator();
		    	Map.Entry entry = null;
		    	while(metaIt.hasNext())
		    	{
		    		entry = (Map.Entry) metaIt.next();
		    		pw.print("# ");
		    		pw.print(entry.getKey());
		    		pw.print(": ");
		    		pw.print(entry.getValue());;
		    		pw.print("\n");			
		    	}
	    	}
	    	pw.println("#");
		}
	}

	/**
	 * Exports the specified <CODE>ValuesContainer</CODE> s to the specified
	 * file, using the specified details level. Only <CODE>ValuesContainer
	 * </CODE> s that have data are exported; and in the case that a <CODE>
	 * ValuesContainer</CODE> contains waveform data, there must not be any
	 * other <CODE>ValuesContainer</CODE> s
	 * 
	 * @param vcs
	 *            a <CODE>Vector</CODE> of <CODE>ValuesContainer</CODE> s
	 * @param file
	 *            the file the data is written to
	 * @param append
	 *            the flag indicating if the specified file is to be appended to
	 *            or overwritten
	 * @param detailsLevel
	 *            see {@link ClientPlugin ClientFacade}
	 * @param tsFormat
	 *            a <CODE>String</CODE> containing the date format for
	 *            timestamps; all standard Java tokens (y, M, H, h, m, S, s) may
	 *            be used; if NULL or an empty <CODE>String</CODE>, or
	 *            parsing errors occur,
	 *            {@link #DEFAULT_DATE_FORMAT the standard date format}is used
	 * @throws Exception
	 */
	public void export(ValuesContainer[] vcs, int firstIndex, int lastIndex, Writer w,
			boolean append, int detailsLevel, String tsFormat)
			throws Exception
	{

		if (tsFormat == null || tsFormat.equals(""))
			dateFormat = DEFAULT_DATE_FORMAT;
		else
		{
			try
			{
				int index = tsFormat.indexOf("n");
				if (index >= 0)
				{
					appendNsecs = true;
					//inclusive, exclusive; also leave the punctuation
					dateFormat = new SimpleDateFormat(tsFormat.substring(0,
							index-1));
				}
				else
				{
					appendNsecs = false;
					dateFormat = new SimpleDateFormat(tsFormat);
				}
			}
			catch (Exception e)
			{
				dateFormat = DEFAULT_DATE_FORMAT;
			}
		}
		//create a writer
		PrintWriter pw = new PrintWriter(w);
		//clean from null vcs
		Vector nonNullVCs = new Vector();
		int i=0;
		for (i = 0; i < vcs.length; i++)
		{
			if (vcs[i] != null && vcs[i].getNumberOfValues() > 0)
				nonNullVCs.add(vcs[i]);
		}
		if (nonNullVCs.size() == 0)
			throw new NullPointerException("No values to be exported in this request");
		if(append == false)
			exportMetaInfo(pw, nonNullVCs);
		ValuesContainer firstNonNullVC = (ValuesContainer) nonNullVCs.get(0);
		if (nonNullVCs.size() == 1 && firstNonNullVC.isWaveform())
		{
			if (vcs.length == 1)
				exportWaveform(pw, firstNonNullVC, firstIndex, lastIndex, detailsLevel, !append);
			else
				pw.print("\n Can not export more than one waveform at a time!");
		}
		else
			exportRegularData(pw, nonNullVCs, firstIndex, lastIndex, detailsLevel, !append);
		pw.flush();
		//writer.flush();
	}
	
	public String getId()
	{
		return "spreadsheet";
	}
	public String getExt(){
		return "";
	}
}