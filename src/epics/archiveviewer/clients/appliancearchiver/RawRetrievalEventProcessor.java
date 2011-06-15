package epics.archiveviewer.clients.appliancearchiver;

import java.util.logging.Logger;

import org.epics.archiverappliance.retrieval.EventStreamDesc;
import org.epics.archiverappliance.retrieval.client.RetrievalEventProcessor;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;

public class RawRetrievalEventProcessor implements RetrievalEventProcessor {
	private static Logger logger = Logger.getLogger(RawRetrievalEventProcessor.class.getName());
	final AVEntry[] avEntries;
	ValuesContainer[] valueContainers;
	EventStreamValuesContainer currentVals;
	int currentPV = 0;
	
	public RawRetrievalEventProcessor(final AVEntry[] archiveEntries) {
		this.avEntries = archiveEntries;
		valueContainers = new ValuesContainer[this.avEntries.length];
	}

	@Override
	public void newPVOnStream(EventStreamDesc desc) {
		logger.info("Getting data for PV " + desc.getPvName());
		currentVals = new EventStreamValuesContainer(avEntries[currentPV]);
		valueContainers[currentPV] = currentVals;
		currentPV++;
	}

}
