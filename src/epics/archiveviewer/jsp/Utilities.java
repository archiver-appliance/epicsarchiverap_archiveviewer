/*
 * Created on Apr 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import epics.archiveviewer.base.AVBase;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Utilities {
	static
	{
		System.setProperty("java.awt.headless", Boolean.toString(true));
	}
	
	private static void checkStringForBadCharacters(String s) throws Exception
	{
		if(s.indexOf('>') > -1 || s.indexOf('<') > -1)
			throw new Exception("Forbidden character in query string");	
	}
	
	public static void connect(HttpServletRequest request) throws Exception
	{
		String connectionParameter = request.getParameter(JSPConstants.CONNECTION_REQUEST_NAME);
		
		AVBase avBase = new AVBase();
		request.getSession().setAttribute(JSPConstants.AVBASE_SESSION_NAME, avBase);
		
		avBase.getClient().connect(connectionParameter, null);
		avBase.getArchiveDirectoriesRepository().setArchiveDirectories(avBase.getClient().getAvailableArchiveDirectories());
	}
	
	public static void checkParametersForBadCharactets(HttpServletRequest request) throws Exception
	{
		Enumeration parameters = request.getParameterNames();
		String s = null;
		String[] vals = null;
		while(parameters.hasMoreElements())
		{
			s = (String) parameters.nextElement();
			if(s!=null)
			{
				checkStringForBadCharacters(s);
				
				vals = request.getParameterValues(s);
				for(int i=0; i<vals.length; i++)
				{
					checkStringForBadCharacters(vals[i]);
				}
			}
		}
	}
}
