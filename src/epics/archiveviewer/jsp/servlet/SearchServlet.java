/*
 * Created on Mar 16, 2005
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

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.UseCases;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.jsp.JSPConstants;
import epics.archiveviewer.jsp.Utilities;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchServlet extends HttpServlet
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
		AVBase avBase = (AVBase) request.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
		avBase.getMatchingAVEsRepository().clear();
		String regEx = AVBaseUtilities.convertGlobToRegular(
				request.getParameter(JSPConstants.GLOB_EXPRESSION_REQUEST_NAME), false);
		
		String[] arDirNames = request.getParameterValues(JSPConstants.ARCHIVE_DIRS_REQUEST_NAME);
		try
		{
			for(int i=0; i<arDirNames.length; i++)
			{
				try
				{
					UseCases.search(avBase, arDirNames[i], regEx, null);
				}
				catch(Exception e1)
				{
					try
					{
						e1.printStackTrace(response.getWriter());
					}
					catch(IOException e2)
					{
						e2.printStackTrace();
					}
				}
			}
			request.getRequestDispatcher("matching_pvs.jsp").forward(request, response);
		}
		catch(Exception e1)
		{
			try
			{
				response.getWriter().println("Search for " + regEx + " failed");
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
