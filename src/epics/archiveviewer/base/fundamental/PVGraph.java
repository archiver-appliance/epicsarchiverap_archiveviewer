package epics.archiveviewer.base.fundamental;

import java.awt.Color;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.DrawType;

/**
 * This class represents a PV graph
 * 
 * @author Sergei Chevtsov
 */
public class PVGraph implements Graph
{
	private final AVEntry avEntry;
	private String timeAxisLabel;
	private String rangeAxisLabel;
	private Color color;
	private DrawType drawType;
	private float drawWidth;
	private boolean isVisible;

	public PVGraph(
			AVEntry ave, 
			String tALabel,
			String rALabel,
			Color c,
			DrawType type,
			float width,
			boolean visibility)
	{
		this.avEntry = ave;
		this.timeAxisLabel = tALabel;
		this.rangeAxisLabel = rALabel;
		this.color = c;
		this.drawType = type;
		this.drawWidth = width;
		this.isVisible = visibility;
	}
	
	public AVEntry getAVEntry() {
		return this.avEntry;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public DrawType getDrawType() {
		return this.drawType;
	}
	
	public float getDrawWidth() {
		return this.drawWidth;
	}
	
	public boolean isVisible() {
		return this.isVisible;
	}
	
	public String getRangeAxisLabel() {
		return this.rangeAxisLabel;
	}
	
	public String getTimeAxisLabel() {
		return this.timeAxisLabel;
	}
	
	//may be NULL => whatever default is
	public String getRetrievalMethodName()
	{
		return null;
	}

 	public int getRequestedNumberOfValues()
 	{
 		return -1;
 	}
 	
	public void setColor(Color c) {
		this.color = c;
	}

	public void setDrawType(DrawType type) {
		this.drawType = type;
	}

	public void setDrawWidth(float width) {
		this.drawWidth = width;
	}

	public void setVisible(boolean flag) {
		this.isVisible = flag;
	}

	public void setRangeAxisLabel(String label) {
		this.rangeAxisLabel = label;
	}

	public void setTimeAxisLabel(String label) {
		this.timeAxisLabel = label;
	}

	public void setRetrievalMethodName(String m) {
		//do nothing
	}

	public void setRequestedNumberOfValues(int n) {
		//do nothing
	}
}