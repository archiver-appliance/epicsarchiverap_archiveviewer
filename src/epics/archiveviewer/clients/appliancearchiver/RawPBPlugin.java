package epics.archiveviewer.clients.appliancearchiver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.epics.archiverappliance.Event;
import org.epics.archiverappliance.EventStream;
import org.epics.archiverappliance.data.DBRTimeEvent;
import org.epics.archiverappliance.retrieval.client.RawDataRetrieval;

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
	private String serverURL = null;
	private RetrievalMethod[] retrievalMethodsForPlot = {
			new RetrievalMethodImpl(new Integer(0), "raw", "Raw PB return", false, true)
			};  
	
	@Override
	public String getName() {
		return "RawPB plugin for the archiver appliance";
	}

	@Override
	public void connect(String urlStr, ProgressTask progressInfo) throws Exception {
		logger.info("Connect called");
		URL url = new URL(urlStr + "/ping");
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
		
		serverURL = urlStr;
	}
	
	
	@Override
	public ValuesContainer[] retrieveData(final AVEntry[] archiveEntries,
			RequestObject requestObject, ProgressTask progressInfo)
			throws Exception {
		ArrayList<String> pvNames = new ArrayList<String>();
		for(AVEntry archiveEntry : archiveEntries) {
			pvNames.add(archiveEntry.getName());
		}
		String[] pvNamesArray = pvNames.toArray(new String[0]); 

		// We are skipping the nanos when making the request to the server.
		Timestamp start = new Timestamp((long) requestObject.getStartTimeInMsecs());
		Timestamp end = new Timestamp((long) requestObject.getEndTimeInMsecs());

		// The path here does not have the retrieval as we need the ping which is also part of the retrieval war.
		RawDataRetrieval rawDataRetrieval = new RawDataRetrieval(serverURL + "/data/getData.raw");

		long before = System.currentTimeMillis();
		RawRetrievalEventProcessor retrievalProcessor = new RawRetrievalEventProcessor(archiveEntries);
		EventStream strm = rawDataRetrieval.getDataForPVS(pvNamesArray, start, end, retrievalProcessor);
		long totalValues = 0;
		if(strm != null) {
			try {
				for(Event e : strm) {
					DBRTimeEvent dbrevent = (DBRTimeEvent) e;
					retrievalProcessor.currentVals.add(dbrevent);
					totalValues++;
				}
			} finally {
				strm.close();
			}

			long after = System.currentTimeMillis();
			logger.info("Retrieved " + totalValues	+ " values in " + (after-before) + "(ms)");
			return retrievalProcessor.valueContainers;
		}

		return null;
	}



	@Override
	public String getConnectionParameter() {
		logger.info("getConnectionParameter called");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reconnect(ProgressTask progressInfo) throws Exception {
		logger.info("reconnect called");
		// TODO Auto-generated method stub
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
				return "Default directory";
			}
			
			@Override
			public Object getIDKey() {
				return "Default directory";
			}
		};
		return directories;
	}

	@Override
	public RetrievalMethod[] getRetrievalMethodsForExport() {
		logger.info("getRetrievalMethodsForExport called");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RetrievalMethod[] getRetrievalMethodsForPlot() {
		return retrievalMethodsForPlot;
	}

	@Override
	public RetrievalMethod[] getRetrievalMethodsForCalculation() {
		logger.info("getRetrievalMethod called");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RetrievalMethod getRetrievalMethod(String methodName) {
		logger.info("getRetrievalMethod called");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerInfoText() throws Exception {
		logger.info("getServerInfoText called");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxNrValuesPerPVPerRequest(int nrPVs) {
		logger.info("getMaxNrValuesPerPVPerRequest called");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AVEntry[] search(ArchiveDirectory ad, String pattern,
			ProgressTask progressInfo) throws Exception {
		logger.info("search called");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVEntryInfo getAVEInfo(AVEntry ave) {
		logger.info("getAVEInfo called");
		// TODO Auto-generated method stub
		return null;
	}

}
