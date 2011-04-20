/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintPlotTitleTag extends TagSupport
{
	public int doStartTag() throws JspException 
	{
		try
		{
			PlotModel pm = 
				((AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME)).
					getPlotModel();
			
			String s = pm.getPlotTitle();
			if(s == null)
				s="";
			pageContext.getOut().print(s);
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return SKIP_BODY;
	}

}
