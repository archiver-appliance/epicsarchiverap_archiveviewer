/*
 * Created on Feb 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.fundamental;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FormulaParameter {
	private String argName;
	private String aveName;
	private boolean isAVEFormula;
	
	public FormulaParameter(String arg, String ave, boolean _isFormula)
	{
		this.argName = arg;
		this.aveName = ave;
		this.isAVEFormula = _isFormula;
	}
	
	public String getArg() {
		return this.argName;
	}	
	
	public String getAVEName() {
		return this.aveName;
	}
	
	public boolean isAVEFormula() {
		return this.isAVEFormula;
	}
	
	public void setArg(String arg) {
		this.argName = arg;
	}
	
	public void setAVEName(String _aveName, boolean _isFormula) 
	{
		this.aveName = _aveName;
		this.isAVEFormula = _isFormula;
	}	
}
