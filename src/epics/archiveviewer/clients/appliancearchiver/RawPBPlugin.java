package epics.archiveviewer.clients.appliancearchiver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.archiverappliance.Event;
import org.epics.archiverappliance.EventStream;
import org.epics.archiverappliance.EventStreamDesc;
import org.epics.archiverappliance.config.ArchDBRTypes;
import org.epics.archiverappliance.data.DBRTimeEvent;
import org.epics.archiverappliance.retrieval.RemotableEventStreamDesc;
import org.epics.archiverappliance.retrieval.client.RawDataRetrieval;
import org.epics.archiverappliance.retrieval.client.RetrievalEventProcessor;
import org.epics.archiverappliance.utils.TimeUtils;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.AVEntryInfo;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.ProgressTask;
import epics.archiveviewer.RequestObject;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.clients.channelarchiver.RetrievalMethodImpl;

public class RawPBPlugin implements ClientPlugin {
	private static Logger logger = Logger.getLogger(RawPBPlugin.class.getName());
	private String originalURL = null;
	private String serverURL = null;
	private RetrievalMethod[] retrievalMethodsForPlot = {
			new RetrievalMethodImpl(new Integer(0), "raw", "Raw PB return", false, true),
			new RetrievalMethodImpl(new Integer(2), "average", "Use the mean postprocessor", false, true)
			};  
	
	@Override
	public String getName() {
		return "RawPB plugin for the archiver appliance";
	}

	@Override
	public void connect(String urlStr, ProgressTask progressInfo) throws Exception {
		logger.info("Connect called " + urlStr);
		originalURL = urlStr;
		serverURL = urlStr.replace("pbraw://", "http://");
		URL url = new URL(serverURL + "/ping");
		InputStream is = url.openStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int bytesRead = is.read(buf);
		while(bytesRead > 0) {
			bos.write(buf, 0, bytesRead);
			bytesRead = is.read(buf);
		}
		is.close();
		String pingresponse = bos.toString();
		logger.info(pingresponse);
		
		
		
		// We ping this PV to make sure we have the right client libraries etc...
		String archApplPingPV = "ArchApplPingPV";
		String[] pvNamesArray = new String[] {  archApplPingPV };

		Timestamp start = new Timestamp(System.currentTimeMillis()-1000);
		Timestamp end = new Timestamp(System.currentTimeMillis());
		boolean useReducedDataset = false;
		RawDataRetrieval rawDataRetrieval = new RawDataRetrieval(serverURL + "/data/getData.raw");
		EventStream strm = rawDataRetrieval.getDataForPVS(pvNamesArray, start, end, new RetrievalEventProcessor() {
			@Override
			public void newPVOnStream(EventStreamDesc arg0) {
			}
		}, useReducedDataset);
		long totalValues = 0;
		if(strm != null) {
			try {
				for(Event e : strm) {
					DBRTimeEvent dbrevent = (DBRTimeEvent) e;
					long epochSeconds = e.getEpochSeconds();
					totalValues++;
				}
			} finally {
				strm.close();
			}
		} else {
			logger.warning("We got an empty stream for the ping PV " + archApplPingPV);
		}
	}
	
	
	@Override
	public ValuesContainer[] retrieveData(final AVEntry[] archiveEntries,
			RequestObject requestObject, ProgressTask progressInfo)
			throws Exception {
		ArrayList<String> pvNames = new ArrayList<String>();
		for(AVEntry archiveEntry : archiveEntries) {
			pvNames.add(archiveEntry.getName());
		}
		
		int totalPVs = archiveEntries.length;
		EventStreamValuesContainer[] valueContainers = new EventStreamValuesContainer[totalPVs];

		ExecutorService executor = Executors.newCachedThreadPool();

		try { 
			ArrayList<Callable<String>> callables = new ArrayList<Callable<String>>();
			int pvIndex = 0;
			for(String pvName : pvNames) {
				callables.add(new FetchDataFromAppliance(pvName, archiveEntries[pvIndex], valueContainers, pvIndex, requestObject));
				pvIndex++;
			}

			long before = System.currentTimeMillis();
			executor.invokeAll(callables);
			long after = System.currentTimeMillis();
			logger.info("Retrieved data for " + totalPVs + " pvs in " + (after-before) + "(ms)");
			
		} finally { 
			executor.shutdown();
		}
		
		
		return valueContainers;

	}

	private class FetchDataFromAppliance implements Callable<String> {
		String pvName;
		AVEntry avEntry;
		ValuesContainer[] valueContainers;
		int resultIndex;
		RequestObject requestObject;
		int requestedNumberOfValues;
		String requestedMethodKey;
		
		public FetchDataFromAppliance(String pvName, AVEntry avEntry, ValuesContainer[] valueContainers, int resultIndex, RequestObject requestObject) {
			this.pvName = pvName;
			this.avEntry = avEntry;
			this.valueContainers = valueContainers;
			this.resultIndex = resultIndex;
			this.requestObject = requestObject;
			this.requestedNumberOfValues = requestObject.getRequestedNrOfValues();
			if(requestObject.getMethod() != null && requestObject.getMethod().getKey() != null) {
				this.requestedMethodKey = requestObject.getMethod().getKey().toString();
			} else { 
				logger.info("Defaulting to raw request method");
				this.requestedMethodKey = "0";
			}
			System.out.println("After creating callable....");
		}



		@Override
		public String call() throws Exception {
			try {
				System.out.println("Making a call");
				boolean useReducedDataset = requestObject.getIncludeSparcified();
				// We are skipping the nanos when making the request to the server.
				Timestamp start = new Timestamp((long) requestObject.getStartTimeInMsecs());
				Timestamp end = new Timestamp((long) requestObject.getEndTimeInMsecs());
				
				String postProcessor = null;
				if(requestedMethodKey != null && requestedMethodKey.equals("2")) {
					logger.fine("Using the average postprocessor");
					int binSize = (int) (end.getTime() - start.getTime())/(1000*requestedNumberOfValues);
					postProcessor = "mean_" + binSize + "_fill";
				}


				long before = System.currentTimeMillis();
				// The path here does not have the retrieval as we need the ping which is also part of the retrieval war.
				RawDataRetrieval rawDataRetrieval = new RawDataRetrieval(serverURL + "/data/getData.raw");
				HashMap<String, String> extraParams = new HashMap<String,String>();
				extraParams.put("ca_count", Integer.toString(requestedNumberOfValues));
				extraParams.put("ca_how", requestedMethodKey);
				if(postProcessor != null) {
					extraParams.put("pp", postProcessor);					
				}
				EventStream strm = rawDataRetrieval.getDataForPVS(new String[] { pvName }, start, end, new RetrievalEventProcessor() {
					@Override
					public void newPVOnStream(EventStreamDesc arg0) {
					}
				}, useReducedDataset, extraParams);

				EventStreamValuesContainer currentVals = new EventStreamValuesContainer(avEntry, (strm == null ? new RemotableEventStreamDesc(ArchDBRTypes.DBR_SCALAR_DOUBLE, pvName, TimeUtils.getCurrentYear()) : (RemotableEventStreamDesc) strm.getDescription()));
				valueContainers[resultIndex] = currentVals;

				long totalValues = 0;
				if(strm != null) {
					try {
						for(Event e : strm) {
							DBRTimeEvent dbrevent = (DBRTimeEvent) e;
							currentVals.add(dbrevent);
							totalValues++;
						}
					} finally {
						strm.close();
					}

					long after = System.currentTimeMillis();
					logger.info("Retrieved " + totalValues	+ " values  for pv " + pvName + " in " + (after-before) + "(ms)");
				}

			} catch(Throwable t) {
				logger.log(Level.SEVERE, "Exception fetching data for pv " + pvName, t);
				t.printStackTrace();
			}
			return this.pvName;
		}
	}

	@Override
	public String getConnectionParameter() {
		return originalURL;
	}

	@Override
	public void reconnect(ProgressTask progressInfo) throws Exception {
		logger.info("reconnect called");
		URL url = new URL(serverURL + "/ping");
		InputStream is = url.openStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int bytesRead = is.read(buf);
		while(bytesRead > 0) {
			bos.write(buf, 0, bytesRead);
			bytesRead = is.read(buf);
		}
		is.close();
		String pingresponse = bos.toString();
		logger.info(pingresponse);
	}

	@Override
	public ArchiveDirectory[] getAvailableArchiveDirectories() throws Exception {
		ArchiveDirectory[] directories = new ArchiveDirectory[1];
		directories[0] = new ArchiveDirectory() {
			@Override
			public int compareTo(Object o) {
				return 0;
			}
			
			@Override
			public String getName() {
				return "Default";
			}
			
			@Override
			public Object getIDKey() {
				return "Default";
			}
		};
		return directories;
	}

	@Override
	public RetrievalMethod[] getRetrievalMethodsForExport() {
		return retrievalMethodsForPlot;
	}

	@Override
	public RetrievalMethod[] getRetrievalMethodsForPlot() {
		return retrievalMethodsForPlot;
	}

	@Override
	public RetrievalMethod[] getRetrievalMethodsForCalculation() {
		return retrievalMethodsForPlot;
	}

	@Override
	public RetrievalMethod getRetrievalMethod(String methodName) {
		logger.info("getRetrievalMethod called for method " + methodName);
		for(RetrievalMethod retrievalMethod : retrievalMethodsForPlot) {
			if(retrievalMethod.getName().equals(methodName)) return retrievalMethod;
		}
		logger.warning("Cannot find retrieval method for method name " + methodName);
		return null;
	}

	@Override
	public String getServerInfoText() throws Exception {
		logger.info("getServerInfoText called");
		return null;
	}

	@Override
	public int getMaxNrValuesPerPVPerRequest(int nrPVs) {
		logger.info("getMaxNrValuesPerPVPerRequest called");
		return Integer.MAX_VALUE;
	}

	@Override
	public AVEntry[] search(ArchiveDirectory ad, String pattern, ProgressTask progressInfo) throws Exception {
		logger.info("search called for pattern " + pattern);
		String searchURL = serverURL + "/bpl/searchForPVsRegex?regex=" + URLEncoder.encode(pattern, "UTF-8");
		URL url = new URL(searchURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			LinkedList<AVEntry> ret = new LinkedList<AVEntry>();
			try(InputStream is = connection.getInputStream(); LineNumberReader reader = new LineNumberReader(new InputStreamReader(is))) {
				String pvName = reader.readLine();
				while(pvName != null) {
					AVEntry entry = new AVEntry(pvName, ad);
					ret.add(entry);
					pvName = reader.readLine();
				}
			}
			return ret.toArray(new AVEntry[0]);
		} else {
			logger.warning("Invalid HTTP response from server " + connection.getResponseCode());
			return new AVEntry[0]; 
		}
	}

	@Override
	public AVEntryInfo getAVEInfo(AVEntry ave) {
		logger.info("getAVEInfo called");
		// TODO Auto-generated method stub
		return null;
	}

}
