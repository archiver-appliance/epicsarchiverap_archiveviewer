package epics.archiveviewer.clients.appliancearchiver;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.InfoChangeHandler;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.FieldValue;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;
import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ValuesContainer;

/**
 * @author mshankar
 * A values container backed by a event stream
 *
 */
public class EventStreamValuesContainer implements ValuesContainer, InfoChangeHandler {
	private AVEntry avEntry;
	private Vector<EpicsMessage> events = new Vector<EpicsMessage>();
	private double minValue = Double.MAX_VALUE;
	private double maxValue = Double.MIN_VALUE;
	private double minPosValue = Double.MAX_VALUE;
	private DecimalFormat formatter = new DecimalFormat();
	
	long minTimeMs = Long.MAX_VALUE;
	long maxTimeMs = 0L;
	
	private EPICSEvent.PayloadInfo payloadInfo;
	
	public EventStreamValuesContainer(AVEntry av, EPICSEvent.PayloadInfo desc) {
		this.avEntry = av;
		this.payloadInfo = desc;
		HashMap<String, String> headers = new HashMap<String, String>();
		if(desc.getHeadersCount() > 0) { 
			for(FieldValue f : desc.getHeadersList()) { 
				headers.put(f.getName(), f.getVal());
			}
		}
		if(headers.containsKey("PREC")) {
			formatter.setMaximumFractionDigits(Integer.parseInt(headers.get("PREC")));
		}
		handleInfoChange(desc);
	}
	
	public void add(EpicsMessage dbrevent) throws IOException {
		double val = dbrevent.getNumberValue().doubleValue();
		if(val < minValue) minValue = val;
		if(val > 0 && val < minPosValue) minPosValue = val;
		if(val > maxValue) maxValue = val;
		long currenttsms = dbrevent.getTimestamp().getTime();
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
		return events.get(index).getTimestamp().getTime();
	}

	@Override
	public String getUnits() throws Exception {
		if(this.avEntry.getMetaData().containsKey("EGU")) {
			return (String) this.avEntry.getMetaData().get("EGU");
		} else { 
			return "Cannot determine units";
		}
	}

	@Override
	public int getDimension() throws Exception {
		return payloadInfo.getElementCount();
	}

	@Override
	public String getDisplayLabel(int index) throws Exception {
		if(payloadInfo.getType().getNumber() >= 7) {
			return "Waveform";
		}
	
		if (this.isValid(index))
		{
			return valueToString(index, 0);
		}

		return computeSeverityLabel(events.get(index).getSeverity());
	}

	@Override
	public String valueToString(int index, int item) throws Exception {
		return events.get(index).getNumberValue().toString();
	}

	@Override
	public String getStatus(int index) {
		return computeSeverityLabel(events.get(index).getSeverity()) + "\t" + events.get(index).getStatus();
	}

	@Override
	public boolean isDiscrete() {
		return payloadInfo.getType() == PayloadType.SCALAR_ENUM;
	}

	@Override
	public boolean isValid(int index) throws Exception {
		return index < events.size() & events.get(index).getSeverity() != 3;
	}

	@Override
	public Vector getValue(int index) throws Exception {
		Number val = events.get(index).getNumberAt(index);
		Vector<Number> ret = new Vector<Number>();
		ret.add(val);
		return ret;
	}

	@Override
	public boolean isWaveform() {
		return payloadInfo.getType().getNumber() >= 7;
	}

	@Override
	public Class getDataType() {
		// TODO what should we return here?
		return Double.class;
	}

	@Override
	public int getPrecision() {
		if(this.avEntry.getMetaData().containsKey("PREC")) {
			return Integer.parseInt((String) this.avEntry.getMetaData().get("PREC"));
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
	
	private static String computeSeverityLabel(int severity) {
		switch(severity) {
		case 0:
			return "NO ALARM";
		case 1:
			return "MINOR";
		case 2: 
			return "MAJOR";
		case 3:
			return "INVALID";
//		3968 Est Repeat true false
//		3856 Repeat true false
//		3904 Disconnected false true
//		3872 Archive Off false true
//		3848 Archive Disabled false true
		default:
			return "UnknownSeverity";
		}
	}

	@Override
	public void handleInfoChange(PayloadInfo desc) {
		Map<String, Object> m = this.avEntry.getMetaData();
		if(m == null) { 
			m = new LinkedHashMap<String, Object>();
			this.avEntry.setMetaData(m);
		}
		if (desc.getElementCount() > 1)
			m.put("type", "waveform");
		else
			m.put("type", "double");

		if(desc.getHeadersCount() > 0 ) {
			for(FieldValue f : desc.getHeadersList()) { 
				m.put(mapFieldName(f.getName()), f.getVal());
			}
		}
	}
	
	private static HashMap<String, String> fieldNameMappings = new HashMap<String, String>();
	static { 
		fieldNameMappings.put("LOPR", "disp_low");
		fieldNameMappings.put("HOPR", "disp_high");
		fieldNameMappings.put("LOLO", "alarm_low");
		fieldNameMappings.put("HIHI", "alarm_high");
		fieldNameMappings.put("LOW", "warn_low");
		fieldNameMappings.put("HIGH", "warn_high");
		fieldNameMappings.put("PREC", "precision");
		fieldNameMappings.put("EGU", "units");
		fieldNameMappings.put("DESC", "desc");
	}	
	
	private static String mapFieldName(String headerName) { 
		String expectedMetadataFieldName = fieldNameMappings.get(headerName);
		if(expectedMetadataFieldName == null) {
			return headerName;
		} else { 
			return expectedMetadataFieldName;
		}
		
	}
}
