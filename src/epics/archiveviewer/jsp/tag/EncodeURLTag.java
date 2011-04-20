/*
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EncodeURLTag extends TagSupport
{
	private String url;
	
	public void setUrl(String s)
	{
		this.url = s;
	}
	
	public int doEndTag() throws JspException
	{
		try
		{
			String s = ((HttpServletResponse)pageContext.getResponse()).encodeURL(this.url);
			pageContext.getOut().print(s);
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return EVAL_PAGE;
	}
}
