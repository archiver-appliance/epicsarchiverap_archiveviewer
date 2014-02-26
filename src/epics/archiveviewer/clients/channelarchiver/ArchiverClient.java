package epics.archiveviewer.clients.channelarchiver;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClientLite;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.AVEntryInfo;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.ProgressTask;
import epics.archiveviewer.RequestObject;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.ValuesContainer;

/**
 * A class that implements the client side of the archiver XML-RPC
 * (www.xmlrpc.com) library and allows access to the archived data with hiding
 * all implementation details. Please refer to the archiver manual for details.
 * 
 * @author Craig McChesney
 * @author Sergei Chevtsov
 * @version 14-Sep-2009 Bob Hall. Changed to retrieve sparcified data if specified.
 */
public class ArchiverClient implements ClientPlugin
{
	// Instance Variables ======================================================
	/** a constant for the limit of values that the server can deliver per pv in each request 
	 *	must be less than 10000
	 * last modified John Lee
	 */
	private static final int MAX_NR_VALUES_PER_PV_PER_REQUEST = 10000;

	/** an instance of the XML-RPC client. */
	private XmlRpcClientLite client;

	/** information about the archiver server */
	private ArchiverServerInfo archiverServerInfo;
	
	private final HashMap retrievalMethods;
	
	//set these if progress not simulated
	private int progressValue;
	//message to show with the progressValue
	private String progressMessage;
	
	/**
	 * Calls archiver.info if not yet done so; stores the result in the local
	 * archiverInfo variable
	 * 
	 * @see #archiverInfo
	 * @see ArchiveServerInfo
	 * @throws Exception
	 *             if the server request could not be executed
	 */
	private void getArchiverServerInfoIfNecessary() throws Exception
	{
		if (archiverServerInfo == null)
		{
			Vector params = new Vector();
			Hashtable result = null;
			try
			{
				result = (Hashtable) client.execute("archiver.info", params);
			}
			catch (Exception e)
			{
				throw new Exception(
						"Couldn't execute the archiver.info request "
								+ e.toString());
			}

			// get version
			int ver = ((Integer) result.get("ver")).intValue();

			// get description
			String desc = (String) result.get("desc");

			// get list of "hows" supported by server
			Vector howVector = (Vector) result.get("how");
			String[] howArray = new String[howVector.size()];
			howVector.toArray(howArray);

			// get array of status values supported by server
			Vector statusVector = (Vector) result.get("stat");
			String[] statusArray = new String[statusVector.size()];
			statusVector.toArray(statusArray);

			HashMap severityInfos = new HashMap();
			// get array of severity structs supported by server
			Vector severityVec = (Vector) result.get("sevr");
			Iterator sevIt = severityVec.iterator();
			while (sevIt.hasNext())
			{
				Hashtable sevStruct = (Hashtable) sevIt.next();
				int num = ((Integer) sevStruct.get("num")).intValue();
				String sevr = (String) sevStruct.get("sevr");
				boolean hasValue = ((Boolean) sevStruct.get("has_value"))
						.booleanValue();
				boolean statusIsText = ((Boolean) sevStruct.get("txt_stat"))
						.booleanValue();
				severityInfos.put(new Integer(num), new SeverityInfo(num, sevr,
						hasValue, statusIsText));
			}
			archiverServerInfo = new ArchiverServerInfo(ver, desc, statusArray,
					severityInfos);

			//methods

			try
			{

				RetrievalMethod raw = new RetrievalMethodImpl(new Integer(0),
						howArray[0], "", false, false);
				retrievalMethods.put(raw.getName(), raw);
				
				RetrievalMethod spreadsheet = new RetrievalMethodImpl(
						new Integer(1), howArray[1], "", true, false);
				retrievalMethods.put(spreadsheet.getName(), spreadsheet);
				
				RetrievalMethod interpolated = new RetrievalMethodImpl(
						new Integer(2), howArray[2], "", true, true);
				retrievalMethods.put(interpolated.getName(), interpolated);
				
				RetrievalMethod binning = new RetrievalMethodImpl(
						new Integer(3), howArray[3], "", false, true);
				retrievalMethods.put(binning.getName(), binning);
				
				RetrievalMethod average = new RetrievalMethodImpl(
						new Integer(4), howArray[4], "", true, true);
				this.retrievalMethods.put(average.getName(), average);
			}
			catch (Exception e)
			{
				//do nothing
			}
		}
	}
	
	/**
	 * All aes must be from the same archive directory!!!!
	 */
	private AbstractValuesContainer[] sendServerRequestForData(AVEntry[] aes, RequestObject request)
			throws Exception, IllegalArgumentException
	{
		/* Results in overrun:
		int start_sec = (int) (request.getStartTimeInMsecs() / 1000);
		int start_nano = (int) ((request.getStartTimeInMsecs() - start_sec * 1000) * 1000000);
		int end_sec = (int) (request.getEndTimeInMsecs() / 1000);
		int end_nano = (int) ((request.getEndTimeInMsecs() - end_sec * 1000) * 1000000);
		*/
                
		double start_msec = request.getStartTimeInMsecs();
		int start_sec = (int) (start_msec / 1000);
		int start_nano = (int) ((start_msec - (long)start_sec * 1000) * 1000000);
		double end_msec = request.getEndTimeInMsecs();
		int end_sec = (int) (end_msec / 1000);
		int end_nano = (int) ((end_msec - (long)end_sec * 1000) * 1000000);

		Vector params = new Vector();

		boolean includeSparcified = true;
		// Does the ChannelArchiver support operators?
		if(request.getSparsificationOperator() != null & request.getSparsificationOperator().equals("Raw")) { 
			includeSparcified = false;
		}
                if (includeSparcified)
                {
                    ArchiveDirectory[] ads = getAvailableArchiveDirectories();

                    Object sparceKey = (Object) new Integer(0);
                    boolean found = false;
                    int i = 0;
                    while ((i < ads.length) && (!found))
                    {
                        String archiveName = ads[i].getName().trim();
                        if (archiveName.indexOf("LCLS_SPARCE") != -1)
                        {
                            found = true;
                            sparceKey = ads[i].getIDKey();
                        }
                        else
                        {
                            i++;
                        }
                    }

                    //if (found)
                    //{
                    //    System.out.println("found sparceKey = " + sparceKey);
                    //}

                    if (!found)
                    {
                        throw new Exception("Couldn't find LCLS_SPARCE in archive directories");
                    }

                    params.addElement(sparceKey);
                }
                else
                {
                    //System.out.println("not sparcified IDKey = " +
                    //    aes[0].getArchiveDirectory().getIDKey());

                    params.addElement(aes[0].getArchiveDirectory().getIDKey());
                }
		
		// System.out.println("Sending archiver.values request:");
		HashMap pvNamesAndAEs = new HashMap();
		Vector pvNames = new Vector();
		for(int i=0; i<aes.length; i++)
		{
			//System.out.println("name            : '" + aes[i].getName() + "'");
			pvNamesAndAEs.put(aes[i].getName(), aes[i]);
			pvNames.add(aes[i].getName());
		}
		params.addElement(pvNames);

		
		//System.out.println("start_sec, _nano: " + start_sec + ", " + start_nano);
		//System.out.println("end_sec,   _nano: " + end_sec   + ", " + end_nano);
		//System.out.println("count           : " + request.getRequestedNrOfValues());
		//System.out.println("how             : " + request.getMethod().getKey());
		params.addElement(new Integer(start_sec));
		params.addElement(new Integer(start_nano));
		params.addElement(new Integer(end_sec));
		params.addElement(new Integer(end_nano));
		params.addElement(new Integer(request.getRequestedNrOfValues()));
		params.addElement(request.getMethod().getKey());

		Vector result = null;
		try
		{
			//System.err.println ("Requesting From Server-->"+Calendar.getInstance().getTime().toString());
			result = (Vector) client.execute("archiver.values", params);
			//System.err.println ("Done            Server-->"+Calendar.getInstance().getTime().toString());
		}
		catch (Exception e)
		{
			throw new Exception("Couldn't execute the archiver.values request "
					+ e.toString());
		}

		Vector vcs = new Vector();
		int vcCounter = 0;
		Iterator containerIt = result.iterator();
		while (containerIt.hasNext())
		{
			Hashtable containerStruct = (Hashtable) containerIt.next();

			// parse name

			AVEntry ae = (AVEntry)pvNamesAndAEs.get(containerStruct.get("name"));
			if (ae == null)
				continue;

			// parse the meta info

			Hashtable metaStruct = (Hashtable) containerStruct.get("meta");
			double disp_low = -.0, disp_high = -.0, warn_low = -.0, warn_high = -.0, alarm_low = -.0, alarm_high = -.0;

			String[] states = null;

			int precision = -1;
			String units = "";

			int metaType = ((Integer) metaStruct.get("type")).intValue();

			if (metaType == 0)
			{
				Vector v = (Vector) metaStruct.get("states");
				states = new String[v.size()];
				for (int i = 0; i < v.size(); i++)
				{
					states[i] = v.get(i).toString();
				}
			}
			else
			{
				// parse limits, units, precision
				disp_low = ((Double) metaStruct.get("disp_low")).doubleValue();
				disp_high = ((Double) metaStruct.get("disp_high"))
						.doubleValue();
				warn_low = ((Double) metaStruct.get("warn_low")).doubleValue();
				warn_high = ((Double) metaStruct.get("warn_high"))
						.doubleValue();
				alarm_low = ((Double) metaStruct.get("alarm_low"))
						.doubleValue();
				alarm_high = ((Double) metaStruct.get("alarm_high"))
						.doubleValue();
				units = ((String) metaStruct.get("units"));
				precision = ((Integer) metaStruct.get("prec")).intValue();
			}

			// parse value type

			Class dataType = null;
			switch (((Integer) containerStruct.get("type")).intValue())
			{
			case 0:
				dataType = String.class;
				break;
			case 1:
				dataType = Integer.class;
				break;
			case 2:
				dataType = Integer.class;
				break;
			case 3:
				dataType = Double.class;
			}
			// parse count

			boolean isWaveform = ((Integer) containerStruct.get("count"))
					.intValue() > 1;

			// parse the vector of values

			Vector values = (Vector) containerStruct.get("values");
			if (values == null || values.size() == 0)
				continue;

			Iterator valIt = values.iterator();
			Vector valInfos = new Vector(values.size());
			int valCounter = 0;
			
			double validMin = Double.MAX_VALUE;
			double validMax = Double.MAX_VALUE * -1;
			double validMinPos = Double.MAX_VALUE;
			
			boolean isDataTypeAsignableFromClassNumber = Number.class.isAssignableFrom(dataType);
			
			while (valIt.hasNext())
			{
				Hashtable valStruct = (Hashtable) valIt.next();
				int secs = ((Integer) valStruct.get("secs")).intValue();

				int nanos = ((Integer) valStruct.get("nano")).intValue();
				int status = ((Integer) valStruct.get("stat")).intValue();
				int severity = ((Integer) valStruct.get("sevr")).intValue();
				Vector vals = ((Vector) valStruct.get("value"));
				SeverityInfo sevInfo = archiverServerInfo.getSeverity(severity);
				String statusStr = null;
				if (sevInfo.isStatusText())
					statusStr = archiverServerInfo.getStates()[status];
				else
					//there is no string assigned to the returned int
					statusStr = String.valueOf(status);
				ValueInfo valueInfo = new ValueInfo(secs, nanos, statusStr,
						sevInfo, vals);
				
				/*sort by timestamp, ascending
				int insertPos = valInfos.size();
				final double newTimeStamp = valueInfo.getTimeStampInMsec();
				while(
						insertPos > 0 &&
						((ValueInfo)(valInfos.get(insertPos - 1))).getTimeStampInMsec() > newTimeStamp
					)
				{
					insertPos--;
				}
				valInfos.add(insertPos, valueInfo);
				*/
				valInfos.add(valueInfo);
				
				//check only non-discrete values
				if(isDataTypeAsignableFromClassNumber && valueInfo.isValid())
				{
					double temp = -1;
					double currentMinValue = Double.MAX_VALUE;
					double currentMaxValue = -1 * Double.MAX_VALUE;
					double currentMinPosValue = Double.MAX_VALUE;
					for(int j=0; j<vals.size(); j++)
					{
						temp =  ((Number)vals.get(j)).doubleValue();
						if(currentMinValue > temp)
							currentMinValue = temp;
						if(currentMaxValue < temp)
							currentMaxValue = temp;
						if(temp > 0 && currentMinPosValue > temp)
							currentMinPosValue = temp;
					}
					if(validMin > currentMinValue)
						validMin = currentMinValue;
					if(validMax < currentMaxValue)
						validMax = currentMaxValue;
					if(validMinPos > currentMinPosValue)
						validMinPos = currentMinPosValue;
				}
				
				valCounter++;
			}
			
			if(validMin == Double.MAX_VALUE)
			{
				validMin = Double.NaN;
				validMax = Double.NaN;
			}
			
			if(validMinPos == Double.MAX_VALUE)
			{
				validMinPos = Double.NaN;
			}

			// create a values container and put it in the result v
			if (metaType == 0)
			{
				//replace
				vcs.add(new DiscreteValuesContainer(ae, validMin, validMax, validMinPos, dataType, valInfos, states,
						isWaveform));
			}
			else
			// create a regular container
			{
				vcs.add(new NumericValuesContainer(ae, dataType, valInfos,
							isWaveform, validMin, validMax, validMinPos, disp_low, disp_high, alarm_low,
							alarm_high, warn_low, warn_high, units, precision));
			}
			vcCounter++;
		}
		return (AbstractValuesContainer[]) vcs.toArray(new AbstractValuesContainer[vcs.size()]);
	}

	// Debugging ===============================================================

	/**
	 * Creates a new instance of the <CODE>ArchiverClient</CODE>
	 */
	public ArchiverClient()
	{
		this.retrievalMethods = new HashMap();
		this.progressValue = 0;
		this.progressMessage = null;
	}
	
	public String getName()
	{
		return "ChannelArchiver";
	}

	/*
	 * * Tries to create a connection to the data server at the specified URL.
	 * @param url the URL of the data server @throws IllegalArgumentException if
	 * the connection could not be established
	 */
	public void connect(String url, ProgressTask progressInfo)
			throws IllegalArgumentException
	{
		String originalURL = null;
		try
		{
			originalURL = client.getURL().toExternalForm();
			if (originalURL.equals(url))
				return;
		}
		catch (Exception e)
		{
			//continue
		}
		try
		{
			client = new XmlRpcClientLite(url);
			
			if(progressInfo != null && progressInfo.interrupted())
			    return;

			this.archiverServerInfo = null;
			this.retrievalMethods.clear();
			getArchiverServerInfoIfNecessary();
		}
		catch (Exception e)
		{
			if (originalURL != null)
			{
				//restore the client
				try
				{
					client = new XmlRpcClientLite(originalURL);
				}
				catch (Exception ex)
				{
					//shouldn't happen
				}
			}
			else
			    client = null;
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void reconnect(ProgressTask progressInfo) throws Exception
	{
		this.client = new XmlRpcClientLite(client.getURL().toExternalForm());
	}

	/**
	 * Returns a formatted <CODE>String</CODE> that describes the data server.
	 * 
	 * @return a formatted <CODE>String</CODE> that describes the data server
	 * @throws NullPointerException
	 *             if there is no connection to a server
	 */
	public String getServerInfoText() throws NullPointerException
	{
		try
		{
			getArchiverServerInfoIfNecessary();

			int i = 0; //counter
			final int LINE_SIZE = 40;
			StringBuffer buffer = new StringBuffer(256);
			buffer.append(getConnectionParameter().toString());
			buffer.append("\nServer version: ");
			buffer.append(archiverServerInfo.getVersion());
			buffer.append("\n");

			for (i = 0; i < LINE_SIZE; i++)
			{
				buffer.append("-");
			}
			buffer.append("\nDescription\n");
			buffer.append(archiverServerInfo.getDescription().trim());
			buffer.append("\n");
			for (i = 0; i < LINE_SIZE; i++)
			{
				buffer.append("-");
			}
			buffer.append("\nMethods\n");

			Iterator methodsIt = retrievalMethods.values().iterator();
			while (methodsIt.hasNext())
			{
				//change to toString()
				buffer.append(((RetrievalMethod) methodsIt.next()).getName());
				buffer.append("\n");
			}

			return buffer.toString();
		}
		catch (Exception e)
		{
			throw new NullPointerException();
		}
	}

	/**
	 * Processes the request for plain archived data and also handles formula
	 * calculations. All necessary parameters are obtained from the <CODE>
	 * GUIFacade</CODE>, depending on the specified <CODE>isForPlot</CODE>
	 * flag
	 * 
	 * @see epics.archiver.gui.AVBaseFacade
	 * @param timeAxisLabel
	 *            the label of the time axis the data is requesated for
	 * @param isForPlot
	 *            if true, data will be used for plot; if false, for export
	 */
	public ValuesContainer[] retrieveData(final AVEntry[] aes, RequestObject requestObject, final ProgressTask progressInfo)
			throws Exception
	{
                //System.out.println("entering retrieveData with includeSparcified = " +
                //    requestObject.getIncludeSparcified());

		if (aes == null || aes.length == 0)
			return null;
		
		if(requestObject.getRequestedNrOfValues() > MAX_NR_VALUES_PER_PV_PER_REQUEST)
		    throw new IllegalArgumentException
		    (	
	            "The client can retrieve up to " + 
	            MAX_NR_VALUES_PER_PV_PER_REQUEST +
	            " values only. " + 
	            requestObject.getRequestedNrOfValues() + 
	            " values were requested"
            );
		
		
		int i=0;

		HashMap adsAndAEVectors = new HashMap();
		
		Vector v = null;
		
		//determine the archive directories 
		for(i = 0; i<aes.length; i++)
		{
			v = (Vector) adsAndAEVectors.get(aes[i].getArchiveDirectory());
			if(v == null)
			{
				v = new Vector();
				adsAndAEVectors.put(aes[i].getArchiveDirectory(), v);
			}
			v.add(aes[i]);				
		}			

		//retrieve data
		Vector resultVCs = new Vector();
		Iterator aDIt = adsAndAEVectors.keySet().iterator();
		while(aDIt.hasNext())
		{
			ArchiveDirectory ad = (ArchiveDirectory) aDIt.next();
			Vector aesForAd = (Vector)adsAndAEVectors.get(ad);
			AbstractValuesContainer[] vcs = sendServerRequestForData(
					(AVEntry[]) aesForAd.toArray(new AVEntry[aesForAd.size()]), requestObject);
			for(i=0; i<vcs.length; i++)
			{
				resultVCs.add(vcs[i]);
			}
		}
		return (ValuesContainer[]) resultVCs.toArray(new ValuesContainer[resultVCs.size()]);
	}

	/**
	 * Processes the search for PVs, returns the names that match the user's
	 * regular expression pattern in an alphabetical order. All necessary
	 * parameters are obtained from the <CODE>GUIFacade</CODE>
	 * 
	 * @see epics.archiver.gui.AVBaseFacade
	 * @param pattern
	 *            the regular expression pattern to be matched
	 * @return an Array of <CODE>String</CODE> s containing the pvNames;
	 *         ordered alphabetically
	 */
	public AVEntry[] search(ArchiveDirectory ad, String pattern, ProgressTask progressInfo)
			throws Exception
	{
		try
		{
			if (ad == null)
				throw new IllegalArgumentException(
						"the archive key must not be null");
			if (pattern == null || pattern.equals(""))
				throw new IllegalArgumentException(
						"the pattern must not be null, nor an empty string");

			Vector params = new Vector();
			params.addElement(ad.getIDKey());
			params.addElement(pattern);
			Vector result = null;
			try
			{
				result = (Vector) client.execute("archiver.names", params);
			}
			catch (Exception e)
			{
				throw new Exception(
						"Couldn't execute the archiver.names request "
								+ e.toString());
			}
			if(progressInfo != null && progressInfo.interrupted())
			    throw new InterruptedIOException("Search interrupted");
			
			AVEntry[] sortedAEs = new AVEntry[result.size()];
			
			Iterator nameIt = result.iterator();
			int counter = 0;
			while (nameIt.hasNext())
			{
				Hashtable nameStruct = (Hashtable) nameIt.next();
				String name = (String) nameStruct.get("name");
				double start_s = ((Integer) nameStruct.get("start_sec"))
						.doubleValue();
				double start_n = ((Integer) nameStruct.get("start_nano"))
						.doubleValue();
				double end_s = ((Integer) nameStruct.get("end_sec"))
						.doubleValue();
				double end_n = ((Integer) nameStruct.get("end_nano"))
						.doubleValue();
				//we don't know anything about data at this point,
				//perhaps subject to change in that a request for one data point is sent
				
				sortedAEs[counter] = new CAEntry(name, ad, start_s
						* 1000 + start_n / 1000000, end_s * 1000 + end_n
						/ 1000000);
				//they come sorted from the server
				counter++;
			}
			return sortedAEs;
		}
		catch (Exception e)
		{
			//do nothing
			throw e;
		}
	}

	/**
	 * Returns the String representation of the URL of the data server we
	 * successfully connected to, or NULL
	 * 
	 * @return the URL of the data server we successfully connected to, or NULL
	 */
	public String getConnectionParameter()
	{
		try
		{
			return client.getURL().toExternalForm();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ClientFacade#getAvailableArchiveDirectories()
	 */
	public ArchiveDirectory[] getAvailableArchiveDirectories() throws Exception
	{
		Vector params = new Vector();
		Vector result = null;
		try
		{
			result = (Vector) client.execute("archiver.archives", params);
		}
		catch (Exception e)
		{
			throw new Exception(
					"Couldn't execute archiver.archives request "
							+ e.toString());
		}
		ArchiveDirectory[] ads = new ArchiveDirectory[result.size()];
		for(int i=0; i<result.size(); i++)
		{
			Hashtable archiveStruct = (Hashtable) result.get(i);
			int key = ((Integer) archiveStruct.get("key")).intValue();
			String name = (String) archiveStruct.get("name");
			String path = (String) archiveStruct.get("path");
			ArchiveInfo archiveInfo = new ArchiveInfo(key, name, path);
			ads[i] = archiveInfo;
		}
		return ads;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ClientFacade#getRetrievalMethodsForCalculation()
	 */
	public RetrievalMethod[] getRetrievalMethodsForCalculation()
	{
		Vector v = new Vector();
		Iterator methodsIt = retrievalMethods.values().iterator();
		while (methodsIt.hasNext())
		{
			RetrievalMethod rm = (RetrievalMethod) methodsIt.next();
			if (rm.alignsTimestamps())
			{
				if(rm.reducesResolution())
					v.add(0, rm);
				else
					v.add(rm);
			}
		}
		return (RetrievalMethod[]) v.toArray(new RetrievalMethod[v.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ClientFacade#getRetrievalMethodsForExport()
	 */
	public RetrievalMethod[] getRetrievalMethodsForExport()
	{
		Vector v = new Vector();
		Iterator methodsIt = retrievalMethods.values().iterator();
		while (methodsIt.hasNext())
		{
			RetrievalMethod rm = (RetrievalMethod) methodsIt.next();
			if (rm.alignsTimestamps())
			{
				if(rm.reducesResolution() == false)
					//the first returned retrieval method is going to be the default
					v.add(0, rm);
				else 
					v.add(rm);
			}
			
		}
		return (RetrievalMethod[]) v.toArray(new RetrievalMethod[v.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see epics.archiveviewer.ClientFacade#getRetrievalMethodsForPlot()
	 */
	public RetrievalMethod[] getRetrievalMethodsForPlot()
	{
		ArrayList v = new ArrayList();
		Iterator methodsIt = retrievalMethods.values().iterator();
		while (methodsIt.hasNext())
		{
			RetrievalMethod rm = (RetrievalMethod) methodsIt.next();
			if (rm.reducesResolution())
			{
				if(rm.getName().equalsIgnoreCase("plot-binning"))
					v.add(0, rm);
				else
					v.add(rm);
			}
		}
		return (RetrievalMethod[]) v.toArray(new RetrievalMethod[v.size()]);
	}
	
	public RetrievalMethod getRetrievalMethod(String methodName)
	{
		Iterator methodsIt = retrievalMethods.values().iterator();
		while (methodsIt.hasNext())
		{
			RetrievalMethod rm = (RetrievalMethod) methodsIt.next();
			if (rm.getName().equalsIgnoreCase(methodName))
				return rm;
		}
		return null;
	}
	
	public int getMaxNrValuesPerPVPerRequest(int nrPVs){
		// should have equation, nrPVs and number of values
		// input from panel...
		return MAX_NR_VALUES_PER_PV_PER_REQUEST;
	}
	
	public AVEntryInfo getAVEInfo(AVEntry ae)
	{
		AVEntryInfo info = new AVEntryInfo();
		info.setPVName(ae.getName());
		if(ae instanceof CAEntry)
		{
			CAEntry caae = (CAEntry) ae;
			info.setArchivingTimes(caae.getStartTimeInMsecs(), caae.getEndTimeInMsecs());
		}
		else
		{
			info.setArchivingTimes(-1, -1);
		}
		return info;
	}
}
