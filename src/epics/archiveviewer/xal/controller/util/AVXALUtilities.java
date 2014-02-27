/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.Icons;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.PlotPluginRightClickMenuController;
import epics.archiveviewer.xal.controller.listeners.TimePlotZoomListener;
import epics.archiveviewer.xal.view.aveconfigurators.CommonGraphConfiguratorPanel;
import epics.archiveviewer.xal.view.axesconfigurators.RangeAxesConfigurator;
import epics.archiveviewer.xal.view.axesconfigurators.TimeAxesConfigurator;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVXALUtilities {
	
	public static String getToolTip(Graph g)
	{
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n");
    	sb.append(g.getAVEntry().getName());
    	sb.append("\n");
    	sb.append(g.getAVEntry().getArchiveDirectory().getName());
    	sb.append("\n");
    	
    	Map m = g.getAVEntry().getMetaData();

    	//time axis
    	sb.append("x :");
    	sb.append(g.getTimeAxisLabel());
    	if(m != null && !m.isEmpty() && m.containsKey("Sparsification")) { 
    		sb.append(" " + m.get("Sparsification"));
    	}
    	
    	sb.append("\n");
    	//range axis
    	sb.append("y: ");
    	String rangeAxisLabel = g.getRangeAxisLabel();
    	
    	if(rangeAxisLabel == null)
    		sb.append("normalized");
    	else
    		sb.append(rangeAxisLabel);
    	sb.append("\n");
    	if(m != null && m.isEmpty() == false)
    	{	
	    	Iterator metaIt = m.entrySet().iterator();
	    	Map.Entry entry = null;
	    	while(metaIt.hasNext())
	    	{
	    		entry = (Map.Entry) metaIt.next();
	    		sb.append(entry.getKey().toString());
	    		sb.append(": ");
	    		sb.append(entry.getValue().toString());
	    		sb.append("\n");			
	    	}
    	}
    	return sb.toString();
    }
	
	public static void storeTimeAxisParametersInPlotModel(AVController avController, String tAName)
	{
		try
		{
			PlotModel pm = avController.getAVBase().getPlotModel();
	
			TimeAxesConfigurator tac = avController.getMainAVPanel().getAxesSettingsPanel().getTimeAxesConfigurator();
			
			pm.addTimeAxis(
					new TimeAxis(
						tAName,
						tac.getStartTimeField().getText(),
						tac.getEndTimeField().getText(),
						TimeAxisLocation.getAxisLocation(
								(String)
								tac.getTimeAxisLocationBox().getSelectedItem())
						)
					);
		}
		catch(Exception e)
		{
			avController.getAVBase().displayError("Can't commit the axis parameters", e);
		}
	}
	
	public static void storeRangeAxisParametersInPlotModel(AVController avController, String rAName)
	{
		try
		{
			PlotModel pm = avController.getAVBase().getPlotModel();
	
			RangeAxesConfigurator rac = 
				avController.getMainAVPanel().getAxesSettingsPanel().getRangeAxesConfigurator();
			
			Double min = null;
			if(rac.getMinField().getText().trim().equals("") == false)
				min = Double.valueOf(rac.getMinField().getText());
			
			Double max = null;
			if(rac.getMaxField().getText().trim().equals("") == false)
				max = Double.valueOf(rac.getMaxField().getText());
			
			pm.addRangeAxis(
					new RangeAxis(
						rAName,
						min,
						max,
						RangeAxisType.getRangeAxisType((String)rac.getRangeAxisTypeBox().getSelectedItem()),
						RangeAxisLocation.getAxisLocation(
								(String)
								rac.getRangeAxisLocationBox().getSelectedItem())
						)
					);
		}
		catch(Exception e)
		{
			avController.getAVBase().displayError("Can't commit the axis parameters", e);
		}
	}
	
	public static void setupPlotPlugin(AVController avc, PlotPlugin plotPlugin) throws Exception
	{
		if(plotPlugin.isDomainTime())
		{
			Component c = plotPlugin.getComponent();
			TimePlotZoomListener zoomListener  = new TimePlotZoomListener(avc, plotPlugin);
			c.addMouseListener(zoomListener);
			c.addMouseMotionListener(zoomListener);
			
			new PlotPluginRightClickMenuController(avc, plotPlugin);
		}	
	}

	public static int drawWidthToPercent(float drawWidth)
	{
		float temp = 	(drawWidth - AVBaseConstants.MIN_DRAW_WIDTH)
						/
						(AVBaseConstants.MAX_DRAW_WIDTH - AVBaseConstants.MIN_DRAW_WIDTH);
		return (int) (temp * 100);
	}

	public static float percentToDrawWidth(int percent)
	{
		return 	(percent * (AVBaseConstants.MAX_DRAW_WIDTH - AVBaseConstants.MIN_DRAW_WIDTH))/100 +
				AVBaseConstants.MIN_DRAW_WIDTH;
		
	}

}
