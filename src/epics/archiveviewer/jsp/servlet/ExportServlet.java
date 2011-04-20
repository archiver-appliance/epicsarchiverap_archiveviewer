/*
 * Created on Mar 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.model.ExportModel;
import epics.archiveviewer.jsp.JSPConstants;
import epics.archiveviewer.jsp.Utilities;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportServlet extends HttpServlet
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
		AVBase avBase = 
			(AVBase) request.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
		
		try
		{
			String s = request.getParameter(JSPConstants.PV_NAMES_FOR_EXPORT_REQUEST_NAME);
			
			ArrayList al = new ArrayList();
				
			StringTokenizer st = new StringTokenizer(s);
			String temp = "";
			
			while(st.hasMoreTokens())
			{
				temp = st.nextToken();
				if(temp!=null && !temp.equals(""))
					al.add(temp) ;
			
			}

			String[] pvNames = (String[]) al.toArray(new String[al.size()]);
			
			String statusParameter = request.getParameter(JSPConstants.EXPORT_STATUS_REQUEST_NAME);
			String nrValuesParameter = request.getParameter(JSPConstants.NR_VALUES_REQUEST_NAME);
			if(nrValuesParameter == null || nrValuesParameter.trim().equals(""))
				nrValuesParameter = Integer.toString(Integer.MAX_VALUE);
			
			ExportModel exportModel = 
			new ExportModel
			(
			    avBase.getClient().getConnectionParameter(),
				request.getParameter(JSPConstants.PV_DIRECTORY_REQUEST_NAME),
				pvNames,
				Integer.parseInt(nrValuesParameter),
				request.getParameter(JSPConstants.START_TIME_REQUEST_NAME),
				request.getParameter(JSPConstants.END_TIME_REQUEST_NAME),
				request.getParameter(JSPConstants.RETRIEVAL_METHOD_REQUEST_NAME),
				request.getParameter(JSPConstants.TIMESTAMP_FORMAT_REQUEST_NAME),
				statusParameter != null ? true : false,
				response.getWriter()
			);
			avBase.setExportModel(exportModel);
			
			epics.archiveviewer.base.UseCases.retrieveAndExportData(
					avBase, 
					request.getParameter(JSPConstants.EXPORTER_ID_REQUEST_NAME),
					null);
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

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		processRequest(arg0, arg1);
	}
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		processRequest(arg0, arg1);
	}
}
