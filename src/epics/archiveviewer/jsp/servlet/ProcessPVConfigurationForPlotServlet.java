/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.DrawType;
import epics.archiveviewer.ImagePersistenceBean;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.UseCases;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.base.util.ColorUtilities;
import epics.archiveviewer.jsp.JSPConstants;
import epics.archiveviewer.jsp.Utilities;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProcessPVConfigurationForPlotServlet extends HttpServlet
{
	private String getImageFilePath(HttpServletRequest request)
	{
		return getServletContext().getRealPath("/images/" + request.getSession().getId() + ".png");
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			Utilities.checkParametersForBadCharactets(request);
		}
		catch(Exception e1)
		{
			try
			{
				response.getWriter().println("Forbidden characters in query!");
			}
			catch(Exception e2)
			{
				e2.printStackTrace();
			}
		}
		try
		{
			//update the plot model
			AVBase avBase = (AVBase) request.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
			PlotModel plotModel = avBase.getPlotModel();
			
			int i = 0;
			{
				//graphs
				AVEntry[] aves = plotModel.getAVEntries();			
				String[] timeAxisNames = request.getParameterValues(JSPConstants.TIME_AXIS_REQUEST_NAME);
				String[] rangeAxisNames = request.getParameterValues(JSPConstants.RANGE_AXIS_REQUEST_NAME);
				String[] htmlColors = request.getParameterValues(JSPConstants.COLOR_REQUEST_NAME);
				String[] drawTypes = request.getParameterValues(JSPConstants.DRAW_TYPE_REQUEST_NAME);
				
				String tAName = null;
				String rAName = null;
				for(i=0; i<aves.length; i++)
				{
				    try
				    {
				        tAName = timeAxisNames[i];
				        if(tAName == null || tAName.trim().equals(""))
				            throw new NullPointerException();
				    }
				    catch(Exception e)
				    {
				        tAName = plotModel.getTimeAxisName(0);
				    }
				    try
				    {
				        rAName = rangeAxisNames[i];
				        if(rAName == null || rAName.trim().equals(""))
				            throw new NullPointerException();
				    }
				    catch(Exception e)
				    {
				        rAName = plotModel.getRangeAxisName(0);
				    }
					plotModel.addGraph(
							new PVGraph(
									aves[i],
									tAName,
									rAName,
									ColorUtilities.getColorFromHTMLName(htmlColors[i]),
									DrawType.getDrawType(drawTypes[i]),
									1.0f,
									true
									)
							);
				}
			}
			{
				//time axes
				String[] timeAxisNames = plotModel.getTimeAxesNames();
				String[] startTimes = request.getParameterValues(JSPConstants.START_TIME_REQUEST_NAME);
				String[] endTimes = request.getParameterValues(JSPConstants.END_TIME_REQUEST_NAME);
				String[] locations = request.getParameterValues(JSPConstants.TIME_AXIS_LOCATION_REQUEST_NAME);
				
				for(i=0; i<timeAxisNames.length; i++)
				{
					plotModel.addTimeAxis(
							new TimeAxis(
									timeAxisNames[i],
									startTimes[i],
									endTimes[i],
									TimeAxisLocation.getAxisLocation(locations[i])
								)
						);
				}
			}
			{
				//range axes
				String[] rangeAxisNames = plotModel.getRangeAxesNames();
				String[] mins = request.getParameterValues(JSPConstants.MIN_REQUEST_NAME);
				String[] maxs = request.getParameterValues(JSPConstants.MAX_REQUEST_NAME);
				String[] axisTypes = request.getParameterValues(JSPConstants.AXIS_TYPE_REQUEST_NAME);
				String[] locations = request.getParameterValues(JSPConstants.RANGE_AXIS_LOCATION_REQUEST_NAME);
				
				Double min = null;
				Double max = null;
				for(i=0; i<rangeAxisNames.length; i++)
				{
					if(mins[i] != null && mins[i].trim().equals("") == false)
					{
						min = Double.valueOf(mins[i]);
					}
					
					if(maxs[i] != null && maxs[i].trim().equals("") == false)
					{
						max = Double.valueOf(maxs[i]);
					}
					
					plotModel.addRangeAxis(
							new RangeAxis(
									rangeAxisNames[i],
									min,
									max,
									RangeAxisType.getRangeAxisType(axisTypes[i]),
									RangeAxisLocation.getAxisLocation(locations[i])
								)
						);
				}
			}
			{
				//plot title
				String s = request.getParameter(JSPConstants.PLOT_TITLE_REQUEST_NAME);
				if(s == null)
					s = "";
				plotModel.setPlotTitle(s);
			}
			{
				//legend info
				LegendInfo li = new LegendInfo(false, false, false, false);
				
				String[] legendParams = request.getParameterValues(JSPConstants.LEGEND_REQUEST_NAME);
				
				if(legendParams != null)
				{
					for(i=0; i<legendParams.length; i++)
					{
						if(legendParams[i].equals("show_ave_name"))
							li.setShowAVEName(true);
						else if(legendParams[i].equals("show_archive_name"))
							li.setShowArchiveName(true);
						else if(legendParams[i].equals("show_range"))
							li.setShowRange(true);
						else if(legendParams[i].equals("show_units"))
							li.setShowUnits(true);
					}
				}
				
				plotModel.setLegendInfo(li);
			}
			//following possibilities:
			//1. new time axis name => show configuration
			//2. new range axis name => show configuration
			//3. none of them => plot image
			
			boolean callPlot = true;
			
			String s = null;
			s = request.getParameter("new_time_axis");
			if(s != null && s.trim().equals("") == false)
			{
				if(plotModel.getTimeAxis(s) == null)
				{
					plotModel.addTimeAxis(
							new TimeAxis(
									s,
									AVBaseConstants.DEFAULT_START_TIME,
									AVBaseConstants.DEFAULT_END_TIME,
									AVBaseConstants.DEFAULT_TIME_AXIS_LOCATION
								)
							);
				}
				callPlot = false;
			}
			
			s = request.getParameter("new_range_axis");
			if(s!=null && s.trim().equals("") == false)
			{
				if(plotModel.getRangeAxis(s) == null)
				{
					plotModel.addRangeAxis(
							new RangeAxis(
									s,
									AVBaseConstants.DEFAULT_RANGE_MIN,
									AVBaseConstants.DEFAULT_RANGE_MAX,
									AVBaseConstants.DEFAULT_RANGE_AXIS_TYPE,
									AVBaseConstants.DEFAULT_RANGE_AXIS_LOCATION
								)
							);
				}
				callPlot = false;
			}
			
			if(callPlot)
			{
				String height = request.getParameter(JSPConstants.HEIGHT_REQUEST_NAME);
				String width = request.getParameter(JSPConstants.WIDTH_REQUEST_NAME);
				
				if(	height == null || height.trim().equals("") || 
					width == null || width.trim().equals(""))
				{
					height = "600";
					width  = "800";
				}
				
				PlotPlugin plotPlugin = 
					AVBaseUtilities.loadPlotPlugin(
							avBase, 
							null, 
							"epics.archiveviewer.plotplugins.JFreeChartForTimePlots",
							new ImagePersistenceBean
									(
										new BufferedImage(Integer.parseInt(width), Integer.parseInt(height), BufferedImage.TYPE_INT_RGB),
										epics.archiveviewer.ImageFileFormat.PNG,
										new File(
												getImageFilePath(request)
												)
									)
					);
				plotPlugin.setAvailableRetrievalMethods(avBase.getClient().getRetrievalMethodsForPlot());
				
				AVBaseUtilities.copyAxesRangesFromPlotModelIntoAxesManager(avBase);
				
				ValuesContainer[] vcs = UseCases.retrieveNecessaryDataForPlot(
						avBase,
						plotPlugin.getChosenRetrievalMethod(),
						1000,
						null);
			
				plotPlugin.displayGraphs(vcs);
				request.getRequestDispatcher("plot_image.jsp").forward(request, response);
			}
			else
			{
				request.getRequestDispatcher("plot_configurator.jsp").forward(request, response);
			}
		}
		catch(Exception e1)
		{
			try
			{
				response.getWriter().println("Processing plot settings failed");
				e1.printStackTrace(response.getWriter());
			}
			catch(IOException e2)
			{
				e2.printStackTrace();
			}
		}
	}

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		processRequest(arg0, arg1);
	}
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		processRequest(arg0, arg1);
	}
}
