/*
 * Created on Feb 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.base.fundamental.Formula;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.FormulaParameter;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportModel {
	
	private String directoryName;
	private String[] pvNames;
	private int numberOfValues;
	private String startTime;
	private String endTime;
	private String methodName;
	private String timeStampFormat;
	private boolean showStatus;
	private Writer writer;
	private String connectionParameter;
	private final ArrayList formulas;
	
    // Added at SLAC (along with the supporting "getter" and "setter" methods)
    // for obtaining the specified output Matlab filename (along with its full
    // directory path).
    private String writerFilename;
    private int countPerAvePerRetrival;
    
	public ExportModel( 		String connParam,
								String dirName, 
								String[] _pvnames, 
								int number_values,
								String start, 
								String end,
			                    String method, 
			                    String time_stamp,
			                    boolean status, 
			                    Writer w )
	{	this.connectionParameter = connParam;
		this.directoryName = dirName;
		this.pvNames = _pvnames;
		this.numberOfValues = number_values;
		this.startTime = start;
		this.endTime = end;
		this.methodName = method;
		this.timeStampFormat = time_stamp;
		this.showStatus = status;
		this.writer = w;
		this.formulas = new ArrayList();
		writerFilename = "";
	}
	
	public void addFormula(AVEntry ave, String term, FormulaParameter[] arguments)
	{
		this.formulas.add(new Formula(ave, term, arguments, true));
	}
	
	public void addFormula(FormulaGraph fg)
	{
		this.formulas.add(fg);
	}
	
	public String getConnectionParameter(){
		return connectionParameter;
	}
	
	public String getDirectoryName() {
		return directoryName;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public int getNumberOfValues() {
		return numberOfValues;
	}
	
	public String[] getPvNames() {
		return pvNames;
	}
	
	public boolean isShowStatus() {
		return showStatus;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getTimeStampFormat() {
		return timeStampFormat;
	}
	
	public Formula[] getFormulas()
	{
		return (Formula[]) this.formulas.toArray(new Formula[this.formulas.size()]);
	}
    public String getWriterFilename() {
        return this.writerFilename;
    }

	public void setWriterFilename(String writerFilename) {
		this.writerFilename = writerFilename;
	}
	public int getCountPerAvePerRetrival() {		
		return countPerAvePerRetrival ==  0 ? 3000 : countPerAvePerRetrival;
	}

	public void setCountPerAvePerRetrival(int countPerAvePerRetrival) {
		this.countPerAvePerRetrival = countPerAvePerRetrival;
	}

	public Writer getWriter() {
		return writer;
	}

}
