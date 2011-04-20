/*
 * Created on Apr 13, 2005
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
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.model.ArchiveDirectoriesRepository;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.jsp.JSPConstants;
import epics.archiveviewer.jsp.Utilities;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotDirectlyServlet extends HttpServlet
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
		try
		{
			Utilities.connect(request);
			
			String[] pvNames = request.getParameterValues(JSPConstants.PV_NAME_REQUEST_NAME);
			String[] arDirNames = request.getParameterValues(JSPConstants.PV_DIRECTORY_REQUEST_NAME);
			
			AVBase avBase = (AVBase) request.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
			
			ArchiveDirectoriesRepository adsRepository = avBase.getArchiveDirectoriesRepository();
			PlotModel plotModel = avBase.getPlotModel();			
			
			ArchiveDirectory ad = null;
			AVEntry ave = null;
			PVGraph pvg = null;
			
			for(int i=0; i<pvNames.length; i++)
			{
				ad = adsRepository.getArchiveDirectory(arDirNames[i]);
				ave = new AVEntry(pvNames[i], ad);
				pvg = plotModel.createNewPVGraph(ave);
				plotModel.addGraph(pvg);
			}
			
			request.getRequestDispatcher("/processPVConfigurationForPlot").forward(request, response);
		}
		catch(Exception e1)
		{
			try
			{
				response.getWriter().println("Processing plot configuration failed");
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
