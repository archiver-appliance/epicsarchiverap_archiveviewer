package epics.archiveviewer.clients.appliancearchiver;

import java.text.DecimalFormat;
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
	private DecimalFormat formatter = new DecimalFormat();
	
	long minTimeMs = Long.MAX_VALUE;
	long maxTimeMs = 0L;
	
	private RemotableEventStreamDesc remoteDesc;
	
	public EventStreamValuesContainer(AVEntry av, RemotableEventStreamDesc desc) {
		this.avEntry = av;
		this.remoteDesc = desc;
		if(desc.getPrecision() != null) {
			formatter.setMaximumFractionDigits(desc.getPrecision().intValue());
		}
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
		return remoteDesc.getElementCount();
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
		return events.get(index).getSeverity() + "\t" + events.get(index).getStatus();
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
			return remoteDesc.getPrecision().intValue();
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
		return formatter.format(minValue) + separator + formatter.format(maxValue);
	}

}
