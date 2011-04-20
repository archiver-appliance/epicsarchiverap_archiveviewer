/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Shape;
import java.util.Date;

import javax.swing.JFrame;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.AVEntry;
import epics.archiveviewer.DrawType;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.base.fundamental.Range;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.fundamental.TimeAxis;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVBaseFacadeImpl implements AVBaseFacade
{
	private final AVBase avBase;
	//may be null
	private final JFrame mainFrame;
	
	public AVBaseFacadeImpl(AVBase avb, JFrame appFrame)
	{
		this.avBase = avb;
		this.mainFrame = appFrame;
	}

	public Frame getMainFrame() {
		return this.mainFrame;
	}
	
	public DrawType getDrawType(AVEntry ave) {
		try
		{
			return this.avBase.getPlotModel().getGraph(ave).getDrawType();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the draw type of AV Entry " +
					ave.getName(), e);
			return null;
		}
	}

	public float getDrawWidth(AVEntry ave) {
		try
		{
			return this.avBase.getPlotModel().getGraph(ave).getDrawWidth();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the draw width of AV Entry " +
					ave.getName(), e);
			return -1;
		}
	}

	public Shape getShape(AVEntry ave) {
		return AVBaseConstants.DEFAULT_SHAPE;
	}

	public Color getColor(AVEntry ave) {
		try
		{
			return this.avBase.getPlotModel().getGraph(ave).getColor();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the color of AV Entry " +
					ave.getName(), e);
			return null;
		}
	}

	public String getTimeAxisLabel(AVEntry ave)
	{
		try
		{
			return this.avBase.getPlotModel().getGraph(ave).getTimeAxisLabel();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the time axis of AV Entry " +
					ave.getName(), e);
			return null;
		}
	}

	public Date[] getTimeAxisBounds(String xAxisLabel) throws Exception {
		//use axes intervals manager
		try
		{
			Range r = this.avBase.getAxesIntervalsManager().getCurrentTimeInterval(
					this.avBase.getPlotPluginsRepository().getSelectedPlotPlugin(), xAxisLabel);
			return new Date[]
						  {
							new Date((long) r.min.doubleValue()),
							new Date((long) r.max.doubleValue())
						  };
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the bounds of time axis " +
					xAxisLabel +
					" (did you change the plotplugin?)", e);
			return null;
		}
	}

	public TimeAxisLocation getTimeAxisLocation(String xAxisLabel) throws Exception {
		try
		{
			TimeAxis tA = this.avBase.getPlotModel().getTimeAxis(xAxisLabel);
			return tA.getLocation();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine location of time axis " +
					xAxisLabel, e);
			return null;
		}
	}

	public String getRangeAxisLabel(AVEntry ave) throws Exception {
		try
		{
			String s = this.avBase.getPlotModel().getGraph(ave).getRangeAxisLabel();
			if(s.trim().equals(""))
			    return null;
			return s;
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the range axis of AV Entry " +
					ave.getName(), e);
			return null;
		}
	}

	public Double[] getRangeAxisBounds(String yAxisLabel) throws Exception {
		//use axes intervals manager
		try
		{
			Range r = this.avBase.getAxesIntervalsManager().getCurrentRangeInterval(
					this.avBase.getPlotPluginsRepository().getSelectedPlotPlugin(), yAxisLabel);
			return new Double[]
						  {
							r.min,
							r.max
						  };
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the bounds of range axis " +
					yAxisLabel, e);
			return null;
		}
	}

	public RangeAxisType getRangeAxisType(String yAxisLabel) throws Exception {
		try
		{
			RangeAxis rA = this.avBase.getPlotModel().getRangeAxis(yAxisLabel);
			return rA.getType();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine the type of range axis " +
					yAxisLabel, e);
			return null;
		}
	}

	public RangeAxisLocation getRangeAxisLocation(String yAxisLabel) throws Exception {
		try
		{
			RangeAxis rA = this.avBase.getPlotModel().getRangeAxis(yAxisLabel);
			return rA.getLocation();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine location of range axis " +
					yAxisLabel, e);
			return null;
		}
	}

	public String[] getRangeAxesLabels() {
		try
		{
			return this.avBase.getPlotModel().getRangeAxesNames();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to retrieve range axis labels", e);
			return null;
		}
	}

	public String[] getTimeAxesLabels() {
		try
		{
			return this.avBase.getPlotModel().getTimeAxesNames();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to retrieve time axis labels", e);
			return null;
		}
	}

	public String getSelectedTimeAxisLabel() throws Exception {
		try
		{
			return this.avBase.getPlotModel().getTimeAxesNames()[0];
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to determine selected time axis", e);
			return null;
		}
	}

	public LegendInfo getLegendInfo() {
		try
		{
			return this.avBase.getPlotModel().getLegendInfo();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to retrieve the legend info", e);
			return null;
		}
	}

	public String getPlotTitle() {
		try
		{
			return this.avBase.getPlotModel().getPlotTitle();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to retrieve the plot title", e);
			return null;
		}
	}
	
	public Color getPlotBackgroundColor()
	{
		try
		{
			return this.avBase.getPlotModel().getPlotBGColor();
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Failed to retrieve the plot background", e);
			return AVBaseConstants.DEFAULT_PLOT_BACKGROUND;
		}
	}

	public void displayError(String s, Exception e) {
		this.avBase.displayError(s, e);
	}

	public void displayWarning(String s, Exception e) {
		this.avBase.displayWarning(s, e);
	}

	public void displayInformation(String s) {
		this.avBase.displayInformation(s);
	}
}
