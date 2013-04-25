package epics.archiveviewer.base.export;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.jmatio.common.MatDataTypes;
import com.jmatio.io.MatFileWriter;
import com.jmatio.io.MatlabIOException;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.Exporter;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.util.AVProgressTask;
import epics.archiveviewer.clients.appliancearchiver.EventStreamValuesContainer;
import epics.archiveviewer.clients.channelarchiver.DiscreteValuesContainer;
import epics.archiveviewer.clients.channelarchiver.NumericValuesContainer;

/**
 * A Exporter plugin to Matlab file format. it requires filename from ExportModel
 * to generate a matlab formatted data file. if the filename wasn't set before
 * calling the fucntions export or genMatlabFile, it will return basically  
 * <P>
 * 
 * it requires to write bytes, so writer will not work with this plugin. Hence, 
 * This does not work with invoking on dos command nor on servlet 
 *  
 * this plugin also uses modified JMatIO package that was originally written by Wojciech Gradkowsk <P>
 * 
 * this plugin uses MatDataStorageInByte which appends time,data into local temporary files 
 * while the retrieval of all data from server is not yet finished. Once done, user might invoke the 
 * genMatlabFile method which will use MatlabFileMatrixAppender to read time,data from teomporary files
 * and output to the specified matlab file. <P>
 * 
 * last modified on 7-17-06
 * @author John Lee
 * 
 */
public class MatlabExporter extends Exporter{
	//variable prefix
	private final String MATLAB_VAR_PREFIX = "archive_";
	//used for limiting the name length of a state
	private final int CHAR_BUF_LENGTH = 128;
	//not important, just counting number of rows for debuggin purpose
	private int [] num_row;
	//containers to hold information of each channel which will later writing to matlab files
	private ArrayList contents;
	//variable name consists of variable prefix + number of this variable
	private String [] matlab_var_name;
	//output file name, required before calling export
	private String outputfilenameandpath = "";
	private String outputfilename = "";
	private String outputdir = "";
	//data storage to store time
	private MatDataStorageInBytes [] mdsib_time;
	//data storage to store data
	private MatDataStorageInBytes [] mdsib_data;
    //the time from the first data retrieved from server
	private double []userStartTimeInMsecs;
	//channel name. used to detect if the values goes to right container
	private Vector pv_name;
	//a monitor for handling export data and time to multiple files
	private TimeAndDataMonitor tadm;
	/** the default date format for timestamps */
	private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss");
	//nanoseconds are 10-9 seconds
	private static final DecimalFormat NSECS_FORMAT = new DecimalFormat("#########");
	
	/**
	 * create components of this class
	 * @param length
	 * @throws Exception
	 */
	private void createComponent (int length) throws Exception{
		try {
			// initialize fields
			contents = new ArrayList ();
			matlab_var_name = new String [length];
			mdsib_time = new MatDataStorageInBytes [length];
			mdsib_data = new MatDataStorageInBytes [length];
			pv_name = new Vector();
			num_row = new int [length];
			tadm = new TimeAndDataMonitor ();
			userStartTimeInMsecs = new double [length];
		} catch (Exception e){
			throw new Exception ("MatlabExporter -- createComponent problem\n"+e.toString());
		}
	}
	/**
	 * initialize components used by this class
	 * @param vc
	 * @param cur_pv
	 * @param seq_num
	 * @param firstIndex
	 * @return
	 * @throws Exception
	 */
	private int initComponent (ValuesContainer vc, int cur_pv, int seq_num, int firstIndex) throws Exception {
		//initalize matlab_var_name
		try {
			matlab_var_name[cur_pv] = genMatVarName (++seq_num);
			//initalize storage for time
			mdsib_time[cur_pv] = new MatDataStorageInBytes (outputdir, outputfilename, matlab_var_name[cur_pv]+"_time", 
									1, MLArray.mxDOUBLE_CLASS);
			//initalize storage for data
			mdsib_data[cur_pv] = new MatDataStorageInBytes (outputdir, outputfilename, matlab_var_name[cur_pv]+"_value", 
									vc.getDimension(), MLArray.mxDOUBLE_CLASS);
			userStartTimeInMsecs[cur_pv] = vc.getTimestampInMsec(firstIndex);
			//initialize thread monitor
			tadm.addPV(vc.getAVEntry().getName());
		} catch (Exception e){
			throw new Exception ("MatlabExporter -- initComponent problem\n"+e.toString());
		}
		return seq_num;
	}
	/**
	 * Exports the specified <CODE>ValuesContainer</CODE> s to the specified
	 * file, using the specified details level. Only <CODE>ValuesContainer
	 * </CODE> s that have data are exported; and in the case that a <CODE>
	 * ValuesContainer</CODE> contains waveform data, there must not be any
	 * other <CODE>ValuesContainer</CODE> s.
	 * 
	 * <p>This build doesn't support details level
	 * 
	 * @param vcs
	 *            a <CODE>Vector</CODE> of <CODE>ValuesContainer</CODE> s
	 * @param file
	 *            the file the data is written to
	 * @param append
	 *            the flag indicating if the specified file is to be appended to
	 *            or overwritten
	 * @param detailsLevel
	 *            see {@link ClientPlugin ClientFacade}
	 * @param tsFormat
	 *            a <CODE>String</CODE> containing the date format for
	 *            timestamps; all standard Java tokens (y, M, H, h, m, S, s) may
	 *            be used; if NULL or an empty <CODE>String</CODE>, or
	 *            parsing errors occur,
	 *            {@link #DEFAULT_DATE_FORMAT the standard date format}is used
	 * @throws Exception
	 */
	public void export(ValuesContainer[] vcs, int firstIndex, int lastIndex, Writer w,
			boolean append, int detailsLevel, String tsFormat)	throws Exception{
		// filename must be set before going further
		try {
			if (outputfilenameandpath == null || outputfilenameandpath.equals("") || outputfilenameandpath.equals(this.getExt()))
				return;
			int seq_num = 0;
			if (!append){
				createComponent(vcs.length);
			}
			for (int i = 0; i < vcs.length; i++){
				if (!append){
					//initialize temporary storage and thread monitor 
					seq_num = initComponent(vcs[i], i, seq_num, firstIndex);
					//output each channel's info
					//write current channel name
					save_name(vcs[i], i);
					//write header
					save_header_info (vcs[i], i, tsFormat);
					//write control info
					save_control_info (vcs[i], i);
				}
				int index = pv_name.indexOf(vcs[i].getAVEntry().getName());
				if (index == -1)
					throw new Exception ("MatlabExporter -- missing PV name");
				//append data from server to storage
				Thread t = new Thread (new TimeAndDataRunner(vcs[i], mdsib_time[index], 
							mdsib_data[index], index, firstIndex, lastIndex));
				tadm.addThread(vcs[i].getAVEntry().getName(), t);
			}
		} catch (Exception e){
			if (tadm != null)
				tadm.waitAllDead();
			this.clear();
			throw new Exception ("MatlabExporter -- saveExportData problem\n"+e.toString(), e);
		}
	}
	/**
	 * A simple monitor class keeping track of threads that was used to 
	 * export data to temporary files
	 * @author John C Lee
	 *
	 */
	private class TimeAndDataMonitor {
		private Hashtable monitor;
		public TimeAndDataMonitor (){
			monitor = new Hashtable ();
		}
		public void addPV (String pv_name){
			monitor.put(pv_name, new Thread ());
		}
		public void waitAllDead (){
			Enumeration en = monitor.elements();
			try{
			while (en.hasMoreElements()){
				Thread t = ((Thread)en.nextElement());
				if (t.isAlive())
					t.join();
			}
			}catch (Exception e){
				e.printStackTrace();
			}	
		}
		public void addThread (String name, Thread t){
			if (monitor.get(name) != null){
				Thread ct = (Thread)monitor.get(name);
				try{
					if (ct.isAlive()){
//						System.err.println ("waiting for thread: "+name+" to terminate");
						ct.join();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			monitor.put(name, t);
			t.start();
		}
	}
	/**
	 * A class was to export data to temporary files
	 * @author John C Lee
	 *
	 */
	private class TimeAndDataRunner implements Runnable{
		private ValuesContainer vc;
		private int cur_pv;
		private int startindex;
		private int endindex;
		private MatDataStorageInBytes mdsib_time;
		private MatDataStorageInBytes mdsib_data;
		public TimeAndDataRunner (ValuesContainer vc, MatDataStorageInBytes mdsib_time, 
						MatDataStorageInBytes mdsib_data, int cur_pv, int startindex, int endindex){
			this.vc = vc;
			this.mdsib_time = mdsib_time;
			this.mdsib_data = mdsib_data;
			this.cur_pv = cur_pv;
			this.startindex = startindex;
			this.endindex = endindex;
		}

		/**
		 * a function that append time, data to the underlying data structure 
		 * @param em	Export model that contains ValuesContainer[] that could provides the necessary info, time and data
		 * @param cur_pv	The current PV channel data that this method will be working on 
		 * @param userStartTimeInMsec	was used to filter out extra time, data that starts before this param 
		 * @param userEndTimeInMsec		was used to filter out extra time, data that ends after this param
		 * @throws Exception	any exceptions that occurs while saving data to underlying data structures	 
		 */

		private void appendTimeAndData () throws Exception{
			try{
//				System.err.println("Begin append-->"+Calendar.getInstance().getTime().toString());
				ArrayList timesec = new ArrayList ();
				ArrayList tempValue = new ArrayList ();
				// filter and record time and data

				for (int j = startindex; j < endindex; j++){
					if (vc.isValid(j)){
						double timestamp = vc.getTimestampInMsec(j); 
						Vector value = vc.getValue(j);
						num_row[cur_pv] ++;
						if (vc.getDimension() != value.size())
							throw new Exception ("MatlabFileGenerator -- missing data from VC");
						tempValue.addAll(value.subList(0, value.size()));
						timesec.add(new Double((timestamp-userStartTimeInMsecs[cur_pv])/1000));
					}
				}	
				// append time, data into their own storage
//				System.err.println("Begin write-->"+Calendar.getInstance().getTime().toString());
				mdsib_time.writeData(timesec.toArray());
				mdsib_data.writeData(tempValue.toArray());
//				System.err.println("End write-->"+Calendar.getInstance().getTime().toString());
//				System.err.println(""+cur_pv+"..."+num_row[cur_pv]+"..."+tempValue.size()+"..."+timesec.size());
			}catch (Exception e){
				throw new Exception ("MatlabFileGenerator-- appendTimeAndData problem\n"+e.toString());
			}
		}
		public void run() {
			// TODO Auto-generated method stub
			try {
//				System.err.println("Start running~~~~~~~~~~~~~~~~~~~~~");
				appendTimeAndData();
//				System.err.println("End running~~~~~~~~~~~~~~~~~~~~");
			}catch (Exception e){
				System.err.println ("MatlabFileGenerator-- appendTimeAndData problem");
			}
		}
	}
	/**
	 * Generate the matlab file based on the infomation, data 
	 * saved through the method call of Export()
	 * @param em	the export model that contains the matlab file name
	 * @throws Exception	any exceptions that occurs while generating matlab file
	 */
	public void genMatlabFile (AVProgressTask avp) throws Exception{
		try {
			if (outputfilenameandpath == null || outputfilenameandpath.equals("") || outputfilenameandpath.equals(this.getExt()))
				return;
			if (tadm == null || mdsib_time == null || mdsib_data == null)
				throw new Exception ("MatlabExporter -- No data for Export");
			if (avp != null)
				avp.setProgressParameters(0, "Start Generating Matlab file");
//			System.err.println("Waiting for all process done");
			tadm.waitAllDead();
			if (avp != null)
				avp.setProgressParameters(50, "Generating Matlab file");
			
//			System.err.println("Generating MatlabFile: "+Calendar.getInstance().getTime().toString());
			//write the collection of header information into matlab file
			MatlabFileMatrixAppender mfma = new MatlabFileMatrixAppender(contents);
			//append the time, data matrices into matlab file, and remove their data storage
			for (int i = 0; i < mdsib_time.length; i++){
				if (avp != null){
					int progress = (50+50/mdsib_time.length*i);
					avp.setProgressParameters( progress > 99 ? 99 : progress , "Generating Matlab file");
				}
				if (mdsib_time[i] == null)
					continue;
				mdsib_time[i].finish();
				mdsib_data[i].finish();
				mfma.StreamMatrixAppender(mdsib_time[i]);
				mfma.StreamMatrixAppender(mdsib_data[i]);
				mdsib_time[i].removeStorage();
				mdsib_data[i].removeStorage();
			}
			if (avp != null)
				avp.setProgressParameters(99, "Generated Successfully");
			this.clear();
//			System.err.println("GenMatlabFile successfully: "+Calendar.getInstance().getTime().toString());
		} catch (Exception e){
			if (tadm != null)
				tadm.waitAllDead();
			this.clear();
			throw new Exception ("MatlabExporter --genMatlabFile problem\n"+e.toString());
		}
	}
	/**
	 * clean out everything this class created
	 * @throws Exception
	 */
	private void clear () throws Exception{
		outputfilenameandpath = null;
		contents = null;
		matlab_var_name = null;
		pv_name = null;
		num_row = null;
		tadm = null;
		userStartTimeInMsecs = null;
		if (mdsib_time != null)
			for (int i = 0; i < mdsib_time.length; i++)
				if(mdsib_time[i] != null) mdsib_time[i].removeStorage();
		if (mdsib_data != null)
			for (int i = 0; i < mdsib_data.length; i++)
				if(mdsib_data[i] != null) mdsib_data[i].removeStorage();
		mdsib_time = null;
		mdsib_data = null;
	}
	/**
	 * set output file name, temp files directory, required before calling export
	 * @param filename
	 */
	public void setOutputFileName (String filename){
		//System.err.println(filename);
		if (filename.endsWith(this.getExt()))
			this.outputfilenameandpath = filename;
		else
			this.outputfilenameandpath = filename+this.getExt();
		if (filename.lastIndexOf("/") != -1){
			outputdir = filename.substring(0,filename.lastIndexOf("/")+1);
			outputfilename = filename.substring(filename.lastIndexOf("/")+1, filename.length());
		}
		else if (filename.lastIndexOf("\\") != -1){
			outputdir = filename.substring(0,filename.lastIndexOf("\\")+1);
			outputfilename = filename.substring(filename.lastIndexOf("\\")+1, filename.length());
		}
		if (new File (outputdir).isDirectory())
			return ;
		outputdir = "./";
		outputfilename = "";
	}
	/**
	 * matlab export plugin id
	 */
	public String getId(){
		return "matlab";
	}
	/**
	 * matlab export Extension, .mat
	 */
	public String getExt(){
		return ".mat";
	}
	/**
	 * generate prefix name of a variable
	 * @param seq_num
	 * @return
	 */
	private String genMatVarName (int seq_num){
		String seq_num_str = ""+seq_num;
		while (seq_num_str.length()<6)
			seq_num_str = "0"+seq_num_str;
		return MATLAB_VAR_PREFIX + seq_num_str;
	}
	/**
	 * Saves the following information of a PV Channel: PV channels name
	 * @param em	Export model that contains ValuesContainer[] that could provides the PV name
	 * @param cur_pv	The current PV channel data that this method will be working on 
	 * @throws Exception	any exceptions that occurs while saving a name to underlying data structures
	 */
	private  void save_name (ValuesContainer vc, int cur_pv) throws Exception{
		pv_name.add(vc.getAVEntry().getName());
		contents.add(new MLChar (matlab_var_name[cur_pv]+"_name", vc.getAVEntry().getName()));
	}
	/**
	 * Saves the following infomation of a PV channel: Dimensikon of data, type of data, starttime of data
	 * @param em	Export model that contains ValuesContainer[] that could provides the necessary info
	 * @param cur_pv	The current PV channel data that this method will be working on 
	 * @throws Exception	any exceptions that occurs while saving header_info to underlying data structures
	 */
	private  void save_header_info (ValuesContainer vc, int cur_pv, String tsFormat) throws Exception{
		contents.add(new MLDouble (matlab_var_name[cur_pv]+"_count", makeDArrCol1(vc.getDimension()), 1));
		String type = vc.getDataType().getName();
		if (type.lastIndexOf(".") == -1)
			contents.add(new MLChar (matlab_var_name[cur_pv]+"_type", type.toUpperCase()));
		else
			contents.add(new MLChar (matlab_var_name[cur_pv]+"_type", type.substring(type.lastIndexOf(".")+1).toUpperCase()));
		
		SimpleDateFormat dateFormat;
		boolean appendNsecs = true;
		if (tsFormat == null || tsFormat.equals(""))
			dateFormat = DEFAULT_DATE_FORMAT;
		else{
			try{
				int index = tsFormat.indexOf("n");
				if (index >= 0)
					dateFormat = new SimpleDateFormat(tsFormat.substring(0, index-1));
				else{
					appendNsecs = false;
					dateFormat = new SimpleDateFormat(tsFormat);
				}
			}catch (Exception e){
				dateFormat = DEFAULT_DATE_FORMAT;
			}
		} 
        long timestampInSecs = (long) (userStartTimeInMsecs[cur_pv] / 1000);
        
        String startTime_well_formatted = dateFormat.format(new Date(timestampInSecs * 1000));
        if (appendNsecs){
        	double nsecs = (userStartTimeInMsecs[cur_pv] / 1000 - timestampInSecs) * 1000000000;
        	startTime_well_formatted += "." + NSECS_FORMAT.format(nsecs);
        }
		contents.add(new MLChar (matlab_var_name[cur_pv]+"_starttime", startTime_well_formatted));
	}
	/**
	 * Saves the following infomation of a PV channel: data type (Numeric, Enumerated) <P>
	 * if data_type is Numeric then it saves the following information: precision, units, display_high, display_low,
	 * high_alarm, low_alarm, high_warning, low_warning <P>
	 * if data_type is Enumerated then it saves the following information: num_states, and names of the states 
	 * @param em	Export model that contains ValuesContainer[] that could provides the necessary info
	 * @param cur_pv	The current PV channel data that this method will be working on 
	 * @throws Exception	any exceptions that occurs while saving control_info to underlying data structures
	 */
	private  void save_control_info (ValuesContainer vc, int cur_pv) throws Exception{
		Map meta = vc.getAVEntry().getMetaData();
		// save necessary data when data type is numeric
		if (vc instanceof NumericValuesContainer){
			contents.add(new MLChar (matlab_var_name[cur_pv]+"_data_type", "Numeric"));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_precision",makeDArrCol1(vc.getPrecision()), 1));
			contents.add(new MLChar (matlab_var_name[cur_pv]+"_units", vc.getUnits()));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_display_high",makeDArrCol1(((Double)meta.get("disp_high")).doubleValue()), 1));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_display_low",makeDArrCol1(((Double)meta.get("disp_low")).doubleValue()), 1));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_alarm_high",makeDArrCol1(((Double)meta.get("alarm_high")).doubleValue()), 1));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_alarm_low",makeDArrCol1(((Double)meta.get("alarm_low")).doubleValue()), 1));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_warning_high",makeDArrCol1(((Double)meta.get("warn_high")).doubleValue()), 1));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_warning_low",makeDArrCol1(((Double)meta.get("warn_low")).doubleValue()), 1));
		}
		// save necessary state information when data type is enumerataed
		else if (vc instanceof DiscreteValuesContainer){
			contents.add(new MLChar (matlab_var_name[cur_pv]+"_data_type", "Enumerated"));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_num_states",makeDArrCol1(meta.size()-1), 1));
			//	record states
			ArrayList states = new ArrayList ();
			int i = 0;
			while (meta.containsKey(new Integer(i)))
				states.add(meta.get(new Integer (i++)));
			MLChar temp = new MLChar (matlab_var_name[cur_pv]+"_state", new int [] {states.size(), CHAR_BUF_LENGTH},  MLArray.mxCHAR_CLASS, 0);			
			for (int j = 0; j < states.size(); j++){
				char [] temp2 = ((String)states.get(j)).toCharArray();
				for (int k = 0; k < CHAR_BUF_LENGTH; k++ ){
					if (k < temp2.length)
						temp.setChar(temp2[k], j+k*states.size());
					else
						temp.setChar(' ', j+k*states.size());
				}
			}
			contents.add(temp);
		}
		else if (vc instanceof EventStreamValuesContainer) { 
			contents.add(new MLChar (matlab_var_name[cur_pv]+"_data_type", "Numeric"));
			contents.add(new MLDouble (matlab_var_name[cur_pv]+"_precision",makeDArrCol1(vc.getPrecision()), 1));
			contents.add(new MLChar (matlab_var_name[cur_pv]+"_units", vc.getUnits()));
			if(meta.containsKey("disp_high")) contents.add(new MLDouble (matlab_var_name[cur_pv]+"_display_high",makeDArrCol1(Double.parseDouble((String)meta.get("disp_high"))), 1));
			if(meta.containsKey("disp_low")) contents.add(new MLDouble (matlab_var_name[cur_pv]+"_display_low",makeDArrCol1(Double.parseDouble((String)meta.get("disp_low"))), 1));
			if(meta.containsKey("alarm_high")) contents.add(new MLDouble (matlab_var_name[cur_pv]+"_alarm_high",makeDArrCol1(Double.parseDouble((String)meta.get("alarm_high"))), 1));
			if(meta.containsKey("alarm_low")) contents.add(new MLDouble (matlab_var_name[cur_pv]+"_alarm_low",makeDArrCol1(Double.parseDouble((String)meta.get("alarm_low"))), 1));
			if(meta.containsKey("warn_high")) contents.add(new MLDouble (matlab_var_name[cur_pv]+"_warning_high",makeDArrCol1(Double.parseDouble((String)meta.get("warn_high"))), 1));
			if(meta.containsKey("warn_low")) contents.add(new MLDouble (matlab_var_name[cur_pv]+"_warning_low",makeDArrCol1((Double.parseDouble((String)meta.get("warn_low")))), 1));
		}
		else
			throw new Exception ("MatlabExporter -- save_control_info: unknown ValueContainer");
	}
	/**
	 * a helper function to create Double array which contains only 1 column
	 * @param value 	the values that will be in the Double[]
	 * @return a new Double array that contains the value parameter
	 */
	private Double[] makeDArrCol1 (double value){
		return new Double [] {new Double (value)};
	}

	/**
	 * A class that will create necessary temporary files for storing Double[] or Character[] in its'
	 * byte format. It will also store necessary information to assist generating matlab files. 
	 * By invoking finish() method will convert temporary columns files into one file that is in Matlab matrix format<P>
	 * 
	 * Current build only supports Java types: Double[] and Character[]. <P>
	 * Current build only exports to Matlab types: Double, UTF8 <P>
	 * 
	 * this plugin also uses modified JMatIO package that was originally written by Wojciech Gradkowsk <P>
	 * 
	 * last modified on 7-17-06
	 * @author John Lee
	 * 
	 */
	private class MatDataStorageInBytes {
		//transfer at most number of data at a time (from storage to storage)
		private final int MAX_DATA_ONE_TIME = 6000;
		//temp files name prefix
		private final String prefix = "DataInBytes_";
		//temp file name of the first column
		private String fullpathname;
		//the type of data stored in Matlab matrix
		private int type;
		//total number of bytes stored in the storage
		private int total_bytes_of_data_wrote;
		//the attribute of this Matlab matrix
		private int attributes;
		//the variable name that the data is corresponding to
		private String var_name;
		//the number of data wrote in this storage
		private int total_data_wrote;
		//number of columns of this Matlab matrix
		private int col;
		//used to initialize a file name 
		private final DateFormat DATE_FORMAT = new SimpleDateFormat("MM.dd.yyyy_HH.mm.ss");
		//the column names
		private String [] colfiles_name;
		
		/**
		 * get a flag that combines the type and the attributes used in generating matlab files
		 * @return a flag
		 */
		public int getFlag (){
			return type & 0xff | attributes & 0xffffff00;
		}
		/**
		 * get the filename this storage creates for data
		 * @return the file name
		 */
		public String getFile (){
			return fullpathname;
		}
		/**
		 * get the columns filename this storage creates for data
		 * @param index
		 * @return a file name
		 */
		public String getColFiles (int index){
			return colfiles_name[index];
		}
		/**
		 * get the Matlab variable name that the data is representing for 
		 * @return variable name
		 */
		public String getName (){
			return var_name;
		}
		/**
		 * get the Dimension of this data array
		 * @return the dimension
		 */
		public int[] getDimension (){
			return new int[] {total_data_wrote/col, col};
		}
		/**
		 * get the Matlab type of the data 
		 * @return a matlab array type
		 */
		public int getType (){
			return type;
		}
		/**
		 * get the type of required padding for a particular matlab type
		 * supports only Character Array, Double Array
		 * @return the type required for padding 
		 */
		public int getPaddingType (){
			if (type == MLArray.mxCHAR_CLASS)
				return MatDataTypes.miUTF8;
			else if (type == MLArray.mxDOUBLE_CLASS)
				return MatDataTypes.miDOUBLE;
			else 
				return -1;
		}
		/**
		 * get the total bytes wrote in files
		 * @return total bytes of data wrote
		 */
	    public int getTotalBytesOfDataInStorage (){
	    	return total_bytes_of_data_wrote;
	    }
	    /**
	     * get the total number of data wrote in files
	     * @return total number of data wrote
	     */
	    public int getTotalDataWrote (){
	    	return total_data_wrote;
	    }
	    /**
	     * a Constructor that assumes attributes to be 0
	     * @param name
	     * @param col
	     * @param mat_type
	     */
	    public MatDataStorageInBytes (String dir, String filename, String name, int col, int mat_type)  throws Exception{
	    	this(dir, filename, name, col, mat_type, 0);
	    }
	    /**
	     * creates a empty storage that uses local directory  
	     * @param name	a variable name of the data
	     * @param col	number of columns per row
	     * @param mat_type	the matlab data type
	     * @param attributes	matlab attributes
	     */
	    public MatDataStorageInBytes (String dir, String filename, String name, int col, int mat_type, int attributes) throws Exception{
	        if ( name != null && !name.equals("") )
	        	var_name = name;
	        else
	            this.var_name = "@"; //default name
	        this.total_data_wrote = 0;
	        this.col = col;
	    	this.type = mat_type;
	    	fullpathname = dir+prefix+filename+(DATE_FORMAT.format(Calendar.getInstance().getTime()))+name;
	    	this.attributes = attributes;
	    	total_bytes_of_data_wrote = 0;
	    	colfiles_name = new String [col];
	    	try{
	    		for (int i = 0; i < col; i++){
	    			colfiles_name[i] = (fullpathname+((i == 0)? "" :"_col"+i));
	    			FileOutputStream fos = new FileOutputStream(new File (colfiles_name[i]));
	    			fos.close();
	    		}
	    	}catch (Exception e){throw new Exception ("MatDataStorageInBytes -- Constructor\n"+e.toString());}
	    }
	    /**
	     * append data into storage specified with offset and length of the data
	     * @param data	the data array that will be used to write to storage
	     * @param offset	the starting element that will be used to write to storage
	     * @param len	the number of element that will be writing to storage
	     */
	    public void writeData (Object [] data, int offset, int len) throws Exception{
	    	if (data == null || data.length == 0)
	    		return;
			try {
				DataOutputStream [] dos = new DataOutputStream [col];
				for (int i = 0; i < col; i++)
			    	dos[i] = new DataOutputStream (new FileOutputStream (colfiles_name[i], true));
		        if (data[0] instanceof Double){
			        for ( int i = offset, j = offset%col; i < offset+len; i++ )
			        	dos[j == col ? j = 0 : j++].writeDouble(((Double)data[i]).doubleValue());
		        }
		        else if (data[0] instanceof Integer){
			        for ( int i = offset, j = offset%col; i < offset+len; i++ )
			        	dos[j == col ? j = 0 : j++].writeDouble(((Integer)data[i]).intValue());
		        }
		        else if (data[0] instanceof String){
		        	for ( int i = offset, j = offset%col; i < offset+len; i++ )
		        		dos[j == col ? j = 0 : j++].writeBytes((String)data[i]);
		        }
		        else if (data[0] instanceof Character){
	                for ( int i = offset, j = offset%col; i < offset+len; i++ ){
	                	dos[j == col ? j = 0 : j++].writeByte((byte)((Character)data[i]).charValue());
	                }
		        }
		        else{
		        	throw new Exception ("support only Double [], Character []");
		        }
		        total_data_wrote += len;
		        for (int i = 0; i < col; i++){
		        	total_bytes_of_data_wrote += dos[i].size();
		        	dos[i].close();
		        }
			}catch (Exception e){
				new Exception ("MatDataStorageInBytes -- writeData()\n"+e.toString());
			}
			
	    }
	    /**
	     * it is a function to combine all temporary column files into a file that is in matlab matrix format 
	     * it is required to invoke before generating matlab files when having more than 1 column
	     * 
	     */
	    public void finish () throws Exception{
	    	try{
	    		if (col <= 1)
	    			return;
		    	int read = 0;
	            byte [] b = new byte [MAX_DATA_ONE_TIME];
	            DataInputStream bufferDos;
	            DataOutputStream dos = new DataOutputStream (new FileOutputStream (colfiles_name[0], true));
		    	for (int i = 1; i < col; i ++){
		    		bufferDos = new DataInputStream (new FileInputStream (colfiles_name[i]));
		    		while ((read = bufferDos.read(b)) > 0)
		    			dos.write(b, 0, read);
		    		dos.flush();
		            bufferDos.close();
	            }
		    	dos.close();
	    	}catch (Exception e) {
	    		new Exception ("MatDataStorageInBytes -- finish()\n"+e.toString());
	    	}   		
	    }
	    /**
	     * append the whole array of data into storage
	     * @param data	the data array that will be used to write to storage
	     */
		public void writeData (Object [] data) throws Exception{
			writeData (data, 0, data.length);
		}

		/**
		 * to remove every temporary storage that this class creates
		 * @throws Exception
		 */
		public void removeStorage () throws Exception{
			try{
				File f;
				for (int i = 0; i < colfiles_name.length; i++){
					f = new File (colfiles_name[i]);
					if (f.exists() && !f.delete())
							throw new Exception ("MatDataSotrageInBytes -- unable to remove storage: "+colfiles_name[i]);
				}
			}catch (Exception e){
				throw new Exception ("MatDataStorageInBytes -- removeStorage()\n"+e.toString());
			}
		}
	}
	
	/**
	 * An extension to MatFileWriter of JMatIO package that allows user to append matrices to the current
	 * matlab file. it invokes MatFileWriter to write a collection of data in its constructor. it then
	 * allows one to append extra matrices to this matlab file. <P>
	 * 
	 * Current build of appending matrics only supports Matlab types: Double, UTF8 <P>
	 * 
	 * this plugin also uses modified JMatIO package that was originally written by Wojciech Gradkowsk <P>
	 * 
	 * last modified on 7-17-06
	 * @author John Lee
	 * 
	 */
	private class MatlabFileMatrixAppender extends MatFileWriter{
		//transfer at most number of data at a time (from stream to storage) 
		private final int MAX_DATA_ONE_TIME = 3000;
		
		public MatlabFileMatrixAppender (Collection data) throws IOException {
			this (new DataOutputStream (new FileOutputStream (outputfilenameandpath)), data);
		}
		/**
		 * invokes the constuctor of MatFileWriter which writes collection of data to the outputstream
		 * @param dos	the outputstream that the collection of data will be writing to
		 * @param data	the collection of data that is going to be writing to the output stream
		 * @throws IOException
		 */
		public MatlabFileMatrixAppender(DataOutputStream dos, Collection data) throws IOException {
			super(dos, data);
		}
		/**
		 * Writes Matrix into OutputStream
		 * @param mdsib
		 * @throws IOException
		 */
		public void StreamMatrixAppender(MatDataStorageInBytes mdsib) throws IOException{
			DataOutputStream dos;
			StreamMatrixAppender (dos = new DataOutputStream(new FileOutputStream (outputfilenameandpath, true)), mdsib);
			dos.close();
		}
		/**
		 * Writes Matrix into OutputStream that user specify
		 * @param output	the output data stream 
		 * @param mdsib 	the data storage contains the matlab array format of data
		 * @throws IOException
		 */
	    private void StreamMatrixAppender(DataOutputStream output, MatDataStorageInBytes mdsib) throws IOException {
	    	
	    	byte [] b;
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(baos);
	        DataInputStream bufferDos;
	        output.writeInt(MatDataTypes.miMATRIX); //matrix tag
	        
	        //flags        
	        writeFlags(dos, mdsib.getFlag());
	        //dimensions
	        writeDimensions(dos, mdsib.getDimension());
	        //array name
	        writeName(dos, mdsib.getName());
	        //write total size of this matrix
	        output.writeInt(baos.size()+mdsib.getTotalBytesOfDataInStorage()+
	        					getPadding(mdsib.getTotalBytesOfDataInStorage(), mdsib.getPaddingType())+4*2);
	        //write flags, dimensions, array name
	        output.write( baos.toByteArray() ); 
	        
	        int total_read = 0;
	        int read;
	        switch ( mdsib.getType() ) {
	            case MLArray.mxCHAR_CLASS:
	            	//appending data from input stream to output stream
	            	output.writeInt (MatDataTypes.miUTF8);
	            	output.writeInt (mdsib.getTotalBytesOfDataInStorage());
	                b = new byte [MAX_DATA_ONE_TIME];
	                bufferDos = new DataInputStream (new FileInputStream (mdsib.getFile()));

	                while ((read = bufferDos.read(b)) > 0){
	                	total_read += read;
	                	output.write(b, 0, read);
	                }
	                bufferDos.close();
	                output.write(new byte [getPadding(mdsib.getTotalBytesOfDataInStorage(), mdsib.getPaddingType())]);
	                break;
	            case MLArray.mxDOUBLE_CLASS:
	            	//appending data from input stream to output stream
	            	output.writeInt (MatDataTypes.miDOUBLE);
	            	output.writeInt (mdsib.getTotalBytesOfDataInStorage());
	                b = new byte [MAX_DATA_ONE_TIME];
	                bufferDos = new DataInputStream (new FileInputStream (mdsib.getFile()));
	                while ((read = bufferDos.read(b)) > 0){
	                	total_read += read;
	                	output.write(b, 0, read);
	                }
	                bufferDos.close();
	                output.write(new byte [getPadding(mdsib.getTotalBytesOfDataInStorage(), mdsib.getPaddingType())]);
	                break;
	            default:
	                throw new MatlabIOException("Cannot write matrix of type: "+mdsib.getType());	                
	        }	        
	    }
	    
	    /**
	     * Writes MATRIX flags into <code>OutputStream</code>.
	     * 
	     * @param os - <code>OutputStream</code>
	     * @param array - a <code>MLArray</code>
	     * @throws IOException
	     */
	    private void writeFlags(DataOutputStream os, int flags) throws IOException
	    {
	        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        DataOutputStream bufferDOS = new DataOutputStream(buffer);

	        bufferDOS.writeInt( flags);
	        bufferDOS.writeInt( 0 );
	        
	        int size = buffer.toByteArray().length;
	        os.writeInt (MatDataTypes.miUINT32);
	        os.writeInt(size);
	        os.write(buffer.toByteArray());
	        if (getPadding (size, MatDataTypes.miUINT32) > 0)
	        	os.write(new byte [getPadding(size, MatDataTypes.miUINT32)]);
	        
	    }

	    /**
	     * Writes MATRIX dimensions into <code>OutputStream</code>.
	     * 
	     * @param os - <code>OutputStream</code>
	     * @param array - a <code>MLArray</code>
	     * @throws IOException
	     */
	    private void writeDimensions(DataOutputStream os, int[]dims) throws IOException
	    {
	        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        DataOutputStream bufferDOS = new DataOutputStream(buffer);
	        
	        for ( int i = 0; i < dims.length; i++ )
	            bufferDOS.writeInt(dims[i]);
	        
	        int size = buffer.toByteArray().length;
	        os.writeInt (MatDataTypes.miUINT32);
	        os.writeInt(size);
	        os.write(buffer.toByteArray());
	        if (getPadding (size, MatDataTypes.miUINT32) > 0)
	        	os.write(new byte [getPadding(size, MatDataTypes.miUINT32)]);
	    }
	    
	    /**
	     * Writes MATRIX name into <code>OutputStream</code>.
	     * 
	     * @param os - <code>OutputStream</code>
	     * @param array - a <code>MLArray</code>
	     * @throws IOException
	     */
	    private void writeName(DataOutputStream os, String name) throws IOException
	    {
	        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        DataOutputStream bufferDOS = new DataOutputStream(buffer);

	        byte[] nameByteArray = name.getBytes();
	        buffer = new ByteArrayOutputStream();
	        bufferDOS = new DataOutputStream(buffer);
	        bufferDOS.write( nameByteArray );

	        
	        int size = buffer.toByteArray().length;
	        os.writeInt (16);
	        os.writeInt(size);
	        os.write(buffer.toByteArray());
	        if (getPadding (size, 16) > 0)
	        	os.write(new byte [getPadding(size, 16)]);
	        
	    }
	    /**
	     * Calcuates padding by specifing the size and the type of the data
	     * @param size the size of data for calculating require padding
	     * @param type the type of data for calculating require padding 
	     * @return number of required padding
	     */
	    private int getPadding(int size, int type)
	    {
	        int padding;
	        //data not packed in the tag  
	        int b;
	        int sizeofType = MatDataTypes.sizeOf(type);
	        padding = ( b = ( ((size/sizeofType)%(8/sizeofType))*sizeofType ) ) !=0   ? 8-b : 0;
	        return padding;
	    }
	}
}
