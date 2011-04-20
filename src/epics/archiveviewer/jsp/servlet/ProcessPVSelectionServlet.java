/*
 * Created on Mar 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.jsp.JSPConstants;
import epics.archiveviewer.jsp.Utilities;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProcessPVSelectionServlet extends HttpServlet
{
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
		String configMode = request.getParameter(JSPConstants.CONFIGURATION_MODE_REQUEST_NAME);
		try
		{
			if(configMode.equals(JSPConstants.PLOT_CONFIGURATION_MODE))
			{
				AVBase avBase = (AVBase) request.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
				PlotModel plotModel = avBase.getPlotModel();
				
				plotModel.clear();
				plotModel.loadInitialAxesSettings();
				
				String[] selectedPVIndices = request.getParameterValues(JSPConstants.PV_INDEX_REQUEST_NAME);
				
				AVEntry selectedPV = null;
				Graph g = null;
				for(int i = 0; i<selectedPVIndices.length; i++)
				{
					selectedPV = avBase.getMatchingAVEsRepository().getMatchingAVE(Integer.parseInt(selectedPVIndices[i]));
					g = plotModel.createNewPVGraph(selectedPV);
					plotModel.addGraph(g);			
				}
				
				request.getRequestDispatcher("plot_configurator.jsp?width=800&height=600").forward(request, response);
			}
			else if(configMode.equals(JSPConstants.EXPORT_CONFIGURATION_MODE))
				request.getRequestDispatcher("export_configurator.jsp").forward(request, response);
			else
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		catch(Exception e1)
		{
			try
			{
				response.getWriter().println("Selection of PVs failed");
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
