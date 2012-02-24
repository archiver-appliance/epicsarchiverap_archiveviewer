package epics.archiveviewer.clients.switcher;

import java.util.logging.Logger;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.AVEntryInfo;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.ProgressTask;
import epics.archiveviewer.RequestObject;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.ValuesContainer;

public class SwitchingClient implements ClientPlugin {
	private static Logger logger = Logger.getLogger(SwitchingClient.class.getName());
	private ClientPlugin thePlugin;
	private static final String CHANNEL_ARCHIVER_CLIENT_CLASS_NAME = "epics.archiveviewer.clients.channelarchiver.ArchiverClient";
	private static final String ARCHIVER_APPLIANCE_CLIENT_CLASS_NAME = "epics.archiveviewer.clients.appliancearchiver.RawPBPlugin";

	public String getName() {
		if (thePlugin != null) { 
			return thePlugin.getName();
		}
		return "Switching client";
	}

	public void connect(String urlStr, ProgressTask progressInfo)
			throws Exception {
		logger.info("Initializing the switching client for " + urlStr);
		if(urlStr.startsWith("pbraw://")) {
			thePlugin = (ClientPlugin) Class.forName(ARCHIVER_APPLIANCE_CLIENT_CLASS_NAME).newInstance();
			thePlugin.connect(urlStr, progressInfo);
		} else {
			thePlugin = (ClientPlugin) Class.forName(CHANNEL_ARCHIVER_CLIENT_CLASS_NAME).newInstance();
			thePlugin.connect(urlStr, progressInfo);
		}
	}

	public String getConnectionParameter() {
		return thePlugin.getConnectionParameter();
	}

	public void reconnect(ProgressTask progressInfo) throws Exception {
		thePlugin.reconnect(progressInfo);
	}

	public ArchiveDirectory[] getAvailableArchiveDirectories() throws Exception {
		if(thePlugin != null) return thePlugin.getAvailableArchiveDirectories();
		return null;
	}

	public RetrievalMethod[] getRetrievalMethodsForExport() {
		if(thePlugin != null) return thePlugin.getRetrievalMethodsForExport();
		return null;
	}

	public RetrievalMethod[] getRetrievalMethodsForPlot() {
		if(thePlugin != null) return thePlugin.getRetrievalMethodsForPlot();
		return null;
	}

	public RetrievalMethod[] getRetrievalMethodsForCalculation() {
		if(thePlugin != null) return thePlugin.getRetrievalMethodsForCalculation();
		return null;
	}

	public RetrievalMethod getRetrievalMethod(String methodName) {
		if(thePlugin != null) return thePlugin.getRetrievalMethod(methodName);
		return null;
	}

	public String getServerInfoText() throws Exception {
		if(thePlugin != null) return thePlugin.getServerInfoText();
		return null;
	}

	public ValuesContainer[] retrieveData(AVEntry[] archiveEntries,
			RequestObject requestObject, ProgressTask progressInfo)
			throws Exception {
		if(thePlugin != null) return thePlugin.retrieveData(archiveEntries, requestObject,
				progressInfo);
		return null;
	}

	public int getMaxNrValuesPerPVPerRequest(int nrPVs) {
		if(thePlugin != null) return thePlugin.getMaxNrValuesPerPVPerRequest(nrPVs);
		return Integer.MAX_VALUE;
	}

	public AVEntry[] search(ArchiveDirectory ad, String pattern,
			ProgressTask progressInfo) throws Exception {
		if(thePlugin != null) return thePlugin.search(ad, pattern, progressInfo);
		return null;
	}

	public AVEntryInfo getAVEInfo(AVEntry ave) {
		if(thePlugin != null) return thePlugin.getAVEInfo(ave);
		return null;
	}
}
