package epics.archiveviewer.clients.appliancearchiver;

import java.util.Vector;

import org.epics.archiverappliance.EventStreamDesc;
import org.epics.archiverappliance.data.DBRTimeEvent;
import org.epics.archiverappliance.retrieval.RemotableEventStreamDesc;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;

/**
 * @author mshankar
 * A values container backed by a event stream
 *
 */
public class EventStreamValuesContainer implements ValuesContainer {
	private AVEntry avEntry;
	private Vector<DBRTimeEvent> events = new Vector<DBRTimeEvent>();
	private double minValue = Double.MAX_VALUE;
	private double maxValue = Double.MIN_VALUE;
	private double minPosValue = Double.MAX_VALUE;
	
	long minTimeMs = Long.MAX_VALUE;
	long maxTimeMs = 0L;
	
	private RemotableEventStreamDesc remoteDesc;
	
	public EventStreamValuesContainer(AVEntry av, RemotableEventStreamDesc desc) {
		this.avEntry = av;
		this.remoteDesc = desc;
	}
	
	public void add(DBRTimeEvent dbrevent) {
		double val = dbrevent.getSampleValue().getValue().doubleValue();
		if(val < minValue) minValue = val;
		if(val > 0 && val < minPosValue) minPosValue = val;
		if(val > maxValue) maxValue = val;
		long currenttsms = dbrevent.getEpochSeconds()*1000;
		if(currenttsms < minTimeMs) minTimeMs = currenttsms;
		if(currenttsms > maxTimeMs) maxTimeMs = currenttsms;
		events.add(dbrevent);
	}

	@Override
	public AVEntry getAVEntry() {
		return avEntry;
	}

	@Override
	public int getNumberOfValues() throws Exception {
		return events.size();
	}

	@Override
	public double getTimestampInMsec(int index) throws Exception {
		return events.get(index).getEpochSeconds()*1000;
	}

	@Override
	public String getUnits() throws Exception {
		return remoteDesc.getUnits();
	}

	@Override
	public int getDimension() throws Exception {
		return 1;
	}

	@Override
	public String getDisplayLabel(int index) throws Exception {
		return remoteDesc.getPvName();
	}

	@Override
	public String valueToString(int index, int item) throws Exception {
		return events.get(index).getSampleValue().toString();
	}

	@Override
	public String getStatus(int index) {
		return "Unknown Status";
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}

	@Override
	public boolean isValid(int index) throws Exception {
		return index < events.size();
	}

	@Override
	public Vector getValue(int index) throws Exception {
		Number val = events.get(index).getSampleValue().getValue(index);
		Vector<Number> ret = new Vector<Number>();
		ret.add(val);
		return ret;
	}

	@Override
	public boolean isWaveform() {
		return remoteDesc.getArchDBRType().isWaveForm();
	}

	@Override
	public Class getDataType() {
		return Double.class;
	}

	@Override
	public int getPrecision() {
		if(remoteDesc.getPrecision() != null) {
			return (int) remoteDesc.getPrecision().doubleValue();
		} else { 
			return 0;
		}
	}

	@Override
	public void clear() {
		events.clear();
	}

	@Override
	public double getMinValidValue() {
		return minValue;
	}

	@Override
	public double getMinPosValidValue() {
		return minPosValue;
	}

	@Override
	public double getMaxValidValue() {
		return maxValue;
	}

	@Override
	public String getRangeLabel(String separator) {
		return String.valueOf(minValue) + ":" + String.valueOf(maxValue);
	}

}
