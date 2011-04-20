package epics.archiveviewer.base.fundamental;

import java.awt.Color;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.DrawType;

/**
 * This class encapsulates the common attributes needed to plot a PV or a
 * formula
 * 
 * @author Sergei Chevtsov
 */
public interface Graph
{	
	public AVEntry getAVEntry();
	public Color getColor();
	public DrawType getDrawType();	
	public float getDrawWidth();	
	public boolean isVisible();	
	public String getRangeAxisLabel();	
	public String getTimeAxisLabel();
	//may be NULL => whatever default is
	public String getRetrievalMethodName();
	//if <=0 => whatever default is
 	public int getRequestedNumberOfValues();
 	
	public void setColor(Color c);
	public void setDrawType(DrawType type);	
	public void setDrawWidth(float width);	
	public void setVisible(boolean flag);	
	public void setRangeAxisLabel(String label);	
	public void setTimeAxisLabel(String label);
	public void setRetrievalMethodName(String m);
 	public void setRequestedNumberOfValues(int n);
}