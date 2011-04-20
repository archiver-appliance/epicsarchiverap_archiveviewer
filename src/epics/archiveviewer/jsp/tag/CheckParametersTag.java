/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.jsp.Utilities;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CheckParametersTag extends TagSupport
{
	
	public int doEndTag() throws JspException
	{
		try
		{
			Utilities.checkParametersForBadCharactets((HttpServletRequest) pageContext.getRequest());
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return EVAL_PAGE;
	}
}
