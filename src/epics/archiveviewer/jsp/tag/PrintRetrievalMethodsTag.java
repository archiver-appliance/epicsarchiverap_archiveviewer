/*
 * Created on Mar 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintRetrievalMethodsTag extends TagSupport
{
	private final static String FOR_PLOT = "plot";
	private final static String FOR_EXPORT = "export";
	private final static String FOR_CALCULATION = "calculation";
	
	private String purpose;
	
	public void setPurpose(String s)
	{
		this.purpose = s;
	}
	public int doEndTag() throws JspException
	{
		try
		{
			AVBase avBase = (AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
			RetrievalMethod[] methods = null;
			
			if(this.purpose.equals(FOR_EXPORT))
				methods = avBase.getClient().getRetrievalMethodsForExport();
			else if(this.purpose.equals(FOR_PLOT))
				methods = avBase.getClient().getRetrievalMethodsForPlot();
			else if(this.purpose.equals(FOR_CALCULATION))
				methods = avBase.getClient().getRetrievalMethodsForCalculation();
			
			for(int i=0; i<methods.length; i++)
			{
				pageContext.getOut().print("<option>");
				pageContext.getOut().print(methods[i].getName());
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
