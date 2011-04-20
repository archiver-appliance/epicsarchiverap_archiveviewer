/*
 * Created on Mar 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintArchiveDirectoriesTag extends BodyTagSupport
{
	private String body;
	
	public int doAfterBody() throws JspException
	{
		try
		{
			bodyContent.writeOut(getPreviousOut());
			body = getBodyContent().getString();
		}
		catch(Exception e)
		{
			//do nothing
		}
		return SKIP_BODY;
	}
	
	public int doEndTag() throws JspException
	{
		try
		{
			AVBase avBase = (AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
			String[] archiveDirectoryNames = avBase.getArchiveDirectoriesRepository().getSortedArchiveDirectoryNames();
			
			boolean bodyContainsInformation = (body != null && body.trim().equals("") == false);
			
			for(int i=0; i<archiveDirectoryNames.length; i++)
			{
				pageContext.getOut().print("<option");
				if(	bodyContainsInformation && 
					archiveDirectoryNames[i].trim().equals(body.trim())
				)
						pageContext.getOut().print(" selected=\"selected\"");
				pageContext.getOut().print(">");
				pageContext.getOut().print(archiveDirectoryNames[i]);
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
