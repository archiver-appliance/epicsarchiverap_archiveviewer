/*
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintExporterIdsTag extends TagSupport
{
	public int doEndTag() throws JspException
	{
		try
		{
			AVBase avBase = (AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
			String[] exporterIds = avBase.getExportersRepository().getRegisteredIds();
			
			for(int i=0; i<exporterIds.length; i++)
			{
				pageContext.getOut().print("<option>");
				pageContext.getOut().print(exporterIds[i]);
				pageContext.getOut().print("</option>");
			}
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return EVAL_PAGE;
	}
}
