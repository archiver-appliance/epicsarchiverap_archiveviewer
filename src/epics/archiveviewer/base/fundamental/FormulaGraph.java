package epics.archiveviewer.base.fundamental;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.DrawType;
import epics.archiveviewer.ValuesContainer;

/**
 * This class represents a formula graph. In our application, the user can make
 * PVs a part of a formula, which is calculated and then plotted in the same
 * manner as a PV
 * 
 * @author Sergei Chevtsov
 */
public class FormulaGraph extends Formula implements Graph
{
	private String methodName;
	private int nrValues;
	private String timeAxisLabel;
	private String rangeAxisLabel;
	private Color color;
	private DrawType drawType;
	private float drawWidth;
	private boolean isVisible;
	
	public FormulaGraph(
			AVEntry ave, 
			String _term,
			FormulaParameter[] arguments,
			String method,
			int _nrValues,
			String tALabel,
			String rALabel,
			Color c,
			DrawType type,
			float width,
			boolean visibility)
	{
		super(ave, _term, arguments, true);
		this.methodName = method;
		this.nrValues = _nrValues;
		this.color = c;
		this.drawType = type;
		this.drawWidth = width;
		this.rangeAxisLabel = rALabel;
		this.timeAxisLabel = tALabel;
		this.isVisible = visibility;
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
		return this.methodName;
	}

 	public int getRequestedNumberOfValues()
 	{
 		return this.nrValues;
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
		this.methodName = m;
	}

	public void setRequestedNumberOfValues(int n) {
		this.nrValues = n;
	}
}