package epics.archiveviewer.clients.appliancearchiver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;
import org.epics.archiverappliance.retrieval.client.RawDataRetrieval;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;
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
	private static RetrievalMethodImpl[] retrievalMethodsForPlot = {
			new RetrievalMethodImpl(new Integer(0), "raw", "Raw PB return", false, true),
			new RetrievalMethodImpl(new Integer(2), "average", "Use the mean postprocessor", false, true),
			new RetrievalMethodImpl(new Integer(4), "linear", "Use the binned linear postprocessor", false, true),
			new RetrievalMethodImpl(new Integer(5), "loess", "Use the binned loess postprocessor", false, true)
			};
	private static RetrievalMethodImpl[] retrievalMethodsForExport = {
		new RetrievalMethodImpl(new Integer(0), "raw", "Raw PB return", false, false),
		new RetrievalMethodImpl(new Integer(2), "average", "Use the mean postprocessor", false, true)
		};


	private String originalURL = null;
	private String serverURL = null;
	
	@Override
	public String getName() {
		return "RawPB plugin for the archiver appliance";
	}

	@Override
	public void connect(String urlStr, ProgressTask progressInfo) throws Exception {
		try { 
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

			Timestamp start = new Timestamp(System.currentTimeMillis()-1000);
			Timestamp end = new Timestamp(System.currentTimeMillis());
			boolean useReducedDataset = false;
			RawDataRetrieval rawDataRetrieval = new RawDataRetrieval(serverURL + "/data/getData.raw");
			GenMsgIterator strm = rawDataRetrieval.getDataForPV(archApplPingPV, start, end, useReducedDataset);
			long totalValues = 0;
			if(strm != null) {
				try {
					for(EpicsMessage dbrevent : strm) {
						long epochSeconds = dbrevent.getTimestamp().getTime()/1000;
						totalValues++;
					}
				} finally {
					strm.close();
				}
			} else {
				logger.warning("We got an empty stream for the ping PV " + archApplPingPV);
			}
		} catch(Exception ex) { 
			logger.log(Level.SEVERE, "Exception establishing connection with the server", ex);
			throw ex;
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
				callables.add(new FetchDataFromAppliance(pvName, archiveEntries[pvIndex], valueContainers, pvIndex, requestObject, archiveEntries.length));
				pvIndex++;
			}

			long before = System.currentTimeMillis();
			executor.invokeAll(callables);
			long after = System.currentTimeMillis();
			logger.info("Retrieved data for " + totalPVs + " pvs in " + (after-before) + "(ms)");
			
		} finally { 
			executor.shutdown();
		}
		
		String exporterId = requestObject.getExporterID();
		int totalPVSInRequest = archiveEntries.length;
		
		if(exporterId != null && exporterId.equals("spreadsheet") && totalPVSInRequest > 1) {
			logger.info("Using client side spreadsheet interpolation");
			valueContainers = SpreadSheetExporter.spreadSheetInterpolate(valueContainers);
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
		String exporterId;
		private int totalPVSInRequest;
		
		public FetchDataFromAppliance(String pvName, AVEntry avEntry, ValuesContainer[] valueContainers, int resultIndex, RequestObject requestObject, int totalPVSInRequest) {
			this.pvName = pvName;
			this.avEntry = avEntry;
			this.valueContainers = valueContainers;
			this.resultIndex = resultIndex;
			this.requestObject = requestObject;
			this.requestedNumberOfValues = requestObject.getRequestedNrOfValues();
			this.exporterId = requestObject.getExporterID();
			this.totalPVSInRequest = totalPVSInRequest;
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
				if(requestedMethodKey != null) { 
					int binSize = (int) (end.getTime() - start.getTime())/(1000*requestedNumberOfValues);
					if(binSize <= 0) { 
						logger.fine("Using a default bin size of 1");
						binSize = 1;
					}
					if (requestedMethodKey.equals("2")) {
						logger.fine("Using the average postprocessor");
						postProcessor = "mean_" + binSize;
					} else if(requestedMethodKey.equals("4")) {
						logger.fine("Using the linear postprocessor");
						postProcessor = "linear_" + binSize;
					} else if(requestedMethodKey.equals("5")) {
						logger.fine("Using the loess postprocessor");
						postProcessor = "loess_" + binSize;
					}
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
				GenMsgIterator strm = rawDataRetrieval.getDataForPV(pvName, start, end, useReducedDataset, extraParams);

				PayloadInfo info = null;
				if(strm != null) { 
					info =  strm.getPayLoadInfo();
				} else { 
					Calendar startCal = Calendar.getInstance();
					startCal.setTime(start);
					info = PayloadInfo.newBuilder()
					.setPvname(pvName)
					.setType(PayloadType.SCALAR_DOUBLE)
					.setYear(startCal.get(Calendar.YEAR)).build();
				}
				EventStreamValuesContainer currentVals = new EventStreamValuesContainer(avEntry, info);
				valueContainers[resultIndex] = currentVals;
				if(strm != null) { 
					strm.onInfoChange(currentVals);
				}

				long totalValues = 0;
				if(strm != null) {
					try {
						for(EpicsMessage dbrevent : strm) {
							currentVals.add(dbrevent, true);
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
		return retrievalMethodsForExport;
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
