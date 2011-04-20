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
public class PrintRequestParameterTag extends TagSupport
{
	private String parameter;
	
	public void setParameter(String s)
	{
		this.parameter = s;
	}
	
	public int doEndTag() throws JspException
	{
		try
		{
			String s  = pageContext.getRequest().getParameter(this.parameter);
			pageContext.getOut().print(s);
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return EVAL_PAGE;
	}
}
