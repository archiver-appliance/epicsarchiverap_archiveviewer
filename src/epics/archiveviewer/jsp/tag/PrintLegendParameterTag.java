/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintLegendParameterTag extends TagSupport{
	public static final String SHOW_AVE_NAME = "show_ave_name";
	public static final String SHOW_AD_NAME = "show_archive_name";
	public static final String SHOW_RANGE = "show_range";
	public static final String SHOW_UNITS = "show_units";
	
	
	private String parameter;
	
	public void setParameter(String s)
	{
		this.parameter = s;
	}
	
	public int doStartTag() throws JspException 
	{
		try
		{
			LegendInfo li = 
				((AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME)).
					getPlotModel().
						getLegendInfo();
			
			if(this.parameter.equals(SHOW_AVE_NAME))
			{
				if(li.getShowAVEName())
					pageContext.getOut().print("\"checked='checked'\"");
			}
			else if(this.parameter.equals(SHOW_AD_NAME))
			{
				if(li.getShowArchiveName())
					pageContext.getOut().print("\"checked='checked'\"");
			}
			if(this.parameter.equals(SHOW_RANGE))
			{
				if(li.getShowRange())
					pageContext.getOut().print("\"checked='checked'\"");
			}
			if(this.parameter.equals(SHOW_UNITS))
			{
				if(li.getShowUnits())
					pageContext.getOut().print("\"checked='checked'\"");
			}
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return SKIP_BODY;
	}

}
