/*
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintFullErrorTag extends TagSupport
{
	public int doEndTag() throws JspException
	{
		try
		{
			String s = pageContext.getException().getMessage();
			pageContext.getOut().println(s);
			pageContext.getOut().println();
			StackTraceElement[] stackTraces = pageContext.getException().getStackTrace();
			for(int i=0; i<stackTraces.length; i++)
			{
				pageContext.getOut().println(stackTraces[i]);
			}
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return EVAL_PAGE;
	}
}
