package epics.archiveviewer.clients.appliancearchiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.epics.archiverappliance.retrieval.client.EpicsMessage;

public class SpreadSheetExporter {
	private static Logger logger = Logger.getLogger(SpreadSheetExporter.class.getName());

	/**
	 * Do a ChannelArchiver style spreadsheet interpolation on the client side.
	 * This should generate the same number of elements in all the value containers.
	 * @param srcValueContainers
	 * @return
	 */
	public static EventStreamValuesContainer[] spreadSheetInterpolate(EventStreamValuesContainer[] srcValueContainers) throws IOException {
		EventStreamValuesContainer[] returnValueContainers = new EventStreamValuesContainer[srcValueContainers.length];
		// indexes represents the current event that is used to generate the data
		int[] indexes = new int[srcValueContainers.length];
		for(int i = 0; i < srcValueContainers.length; i++) { 
			returnValueContainers[i] = new EventStreamValuesContainer(srcValueContainers[i].getAVEntry(), srcValueContainers[i].getPayloadInfo(), srcValueContainers[i].getSparsificationOperator());
			indexes[i] = -1;
		}
	
		int currentMinIndex = findMinimumTimestampAndAdvance(indexes, srcValueContainers);
		while(currentMinIndex != -1) {
			generateData(indexes, currentMinIndex, srcValueContainers, returnValueContainers);
			currentMinIndex = findMinimumTimestampAndAdvance(indexes, srcValueContainers);
		}
		
		checkReturnValueSizes(returnValueContainers);
		
		return returnValueContainers;
	}

	/**
	 * Use two years into the future as an initial value
	 */
	private static long FUTURE_TIMESTAMP = System.currentTimeMillis() + 2*365*24*60*60*100;
	
	/**
	 * Find the EventStreamValuesContainer that has data and whose data has the least timestamp.
	 * If we find such an data element, we advance that index.
	 * @param indexes
	 * @param srcValueContainers
	 * @return -1 if we no longer have data.
	 */
	static int findMinimumTimestampAndAdvance(int[] indexes, EventStreamValuesContainer[] srcValueContainers) {
		Timestamp currentMin = new Timestamp(FUTURE_TIMESTAMP);
		int currentMinIndex = -1;
		for(int i = 0; i < indexes.length; i++) { 
			int nextElemIndex = indexes[i]+1;
			if(srcValueContainers[i].getEvents() != null && nextElemIndex < srcValueContainers[i].getEvents().size()) {
				Timestamp eventTs = srcValueContainers[i].getEvents().get(nextElemIndex).getTimestamp();
				if(eventTs.before(currentMin)) { 
					currentMin = eventTs;
					currentMinIndex = i;
				}
			}
		}
		if(currentMinIndex != -1) { 
			indexes[currentMinIndex] = indexes[currentMinIndex]+1;
		}

		return currentMinIndex;
	}
	
	/**
	 * Generate spreadsheet interpolated data and advance pointer.
	 * We use the timestamp at srcValueContainers[currentMinIndex].getEvents().get(indexes[currentMinIndex])
	 * And we use the values at various indexes[locations] to generate data
	 * @param indexes
	 * @param currentMinIndex
	 * @param srcValueContainers
	 * @param destValueContainers
	 */
	static void generateData(int[] indexes, int currentMinIndex, EventStreamValuesContainer[] srcValueContainers, EventStreamValuesContainer[] destValueContainers) throws IOException { 
		Timestamp currentMin = srcValueContainers[currentMinIndex].getEvents().get(indexes[currentMinIndex]).getTimestamp();
		for(int i = 0; i < indexes.length; i++) { 
			Vector<EpicsMessage> srcEvents = srcValueContainers[i].getEvents();
			if(srcEvents != null && !srcEvents.isEmpty()) {
				int srcEventIndex = indexes[i];
				if(srcEventIndex >= srcEvents.size()) { 
					// Pick the last event if we are past the size of the array
					srcEventIndex = srcEvents.size() - 1;
				} else if (srcEventIndex == -1) {
					// If we have not started yet, pick the first event.
					// This differs a little from the ChannelArchiver.
					srcEventIndex = 0;
				}
				EpicsMessage msg = srcEvents.get(srcEventIndex);
				EpicsMessage newMsg = new EpicsMessage(msg);
				newMsg.setTimestamp(currentMin);
				destValueContainers[i].add(newMsg, false);
			}
		}
	}

	/** 
	 * Make sure that all the returnValueContainers are the same length...
	 * @param returnValueContainers
	 */
	static void checkReturnValueSizes(EventStreamValuesContainer[] returnValueContainers) {
		int returnLength = -1;
		for(int i = 0; i < returnValueContainers.length; i++) { 
			if(returnLength == -1) { 
				returnLength = returnValueContainers[i].getEvents().size();
			} else { 
				if(returnLength != returnValueContainers[i].getEvents().size()) { 
					logger.warning("returnLength " + returnLength + " and container " + i + " has " + returnValueContainers[i].getEvents().size() + " and they differ in length");
				}
			}
		}
	}

	
	/**
	 * Validate a spreadsheet export. 
	 * To validate, export each of the PVs from within AV (which should export raw data) and also one file with all the PVs (which should trigged the spreadsheet interpolation).
	 * We then parse the files and make sure that each sample in the raw data is present in the interpolated data.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if(args.length < 2) { 
			System.err.println("Usage: java epics.archiveviewer.clients.appliancearchiver.SpreadSheetExporter <FileWithCombinedPVs> <FileWithSinglePV1> <FileWithSinglePV2> ...");
			return;
		}

		String combinedPVFileName = args[0];
		List<String> indivPVFileNames = new LinkedList<String>();
		Collections.addAll(indivPVFileNames, args);
		indivPVFileNames.remove(0);
		
		HashMap<String, HashMap<String, String>> combinedPVData = loadTabFile(combinedPVFileName);
		List<HashMap<String, HashMap<String, String>>> indivPVDatas = new ArrayList<HashMap<String, HashMap<String, String>>>();
		for(String indivPVFileName : indivPVFileNames) { 
			indivPVDatas.add(loadTabFile(indivPVFileName));
		}
		
		for(HashMap<String, HashMap<String, String>> indivPVData : indivPVDatas) {
			for(String pvName : indivPVData.keySet()) {
				System.err.println(pvName + " has " + indivPVData.get(pvName).size() + " individual values");
			}
		}
		
		for(HashMap<String, HashMap<String, String>> indivPVData : indivPVDatas) { 
			for(String pvName : indivPVData.keySet()) { 
				HashMap<String, String> indivPVSamples = indivPVData.get(pvName);
				HashMap<String, String> combinedPVSamples = combinedPVData.get(pvName);
				for(String ts : indivPVSamples.keySet()) { 
					String indivVal = indivPVSamples.get(ts);
					String combinedVal = combinedPVSamples.get(ts);
					if(combinedVal == null) { 
						System.err.println("For pv " + pvName + ", data at timestamp " + ts + " exists but not in the combined export");
					} else if (!combinedVal.equals(indivVal)) { 
						System.err.println("For pv " + pvName + ", data at timestamp " + ts + " has indiv val " + indivVal + " and combined val " + combinedVal);
					} else { 
						// System.err.println("For pv " + pvName + ", data at timestamp " + ts + " has indiv val " + indivVal + " and combined val " + combinedVal);
					}
				}
			}
		}
		
	}
	
	/**
	 * We load a tab separated file as generated by archive viewer export.
	 * The file has the following format
	 * #Timestamp      mshankar:arch:static1   Status  mshankar:arch:static2   Status  
	 * 05/14/2013 17:50:36.785000086   1.0     MINOR   4       2.0     MINOR   4        
	 * @param fileName
	 * @return
	 */
	private static HashMap<String, HashMap<String, String>> loadTabFile(String fileName) throws IOException { 
		HashMap<String, HashMap<String, String>> retVal = new HashMap<String, HashMap<String, String>>();
		HashMap<String, Integer> pvName2Column = new HashMap<String, Integer>();
		try(LineNumberReader is = new LineNumberReader(new InputStreamReader(new FileInputStream(new File(fileName))))) { 
			String line = is.readLine();
			boolean postTimestamp = false;
			while(line != null) { 
				if(line.startsWith("#Timestamp")) {
					postTimestamp = true;
					String[] headers = line.split("[\t]");
					int pvNumber = 0;
					for(String header : headers) { 
						if(!header.isEmpty() && !header.contains("Timestamp") && !header.contains("Status")) {
							String pvName = header;
							// 1 is timestamp and we have 3 columns for each PV
							pvName2Column.put(pvName, new Integer(1 + pvNumber*3));
							retVal.put(pvName, new HashMap<String, String>());
							pvNumber++;
						}
					}
					for(String pvName : pvName2Column.keySet()) { 
						logger.info("Found pvName " + pvName + " in file " + fileName);
					}
					
				} else if(postTimestamp) { 
					String[] elements = line.split("[\t]");
					String ts = elements[0];
					for(String pvName : pvName2Column.keySet()) { 
						int columnNumber = pvName2Column.get(pvName);
						String value = elements[columnNumber];
						retVal.get(pvName).put(ts, value);
					}
				}
				line = is.readLine();
			}
		}
		
		return retVal;
	}
}
