/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.DrawType;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.util.ColorUtilities;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintTimeAxisParameterTag extends TagSupport
{
	public static final String NAME = "name";
	public static final String START_TIME = "starttime";
	public static final String END_TIME = "endtime";
	public static final String LOCATION = "location";
	
	private int index;
	private String parameter;
	
	public void setIndex(int i)
	{
		this.index = i;
	}
	public void setParameter(String s)
	{
		this.parameter = s;
	}
	
	public int doStartTag() throws JspException 
	{
		try
		{
			PlotModel pm = 
				((AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME)).
					getPlotModel();
			
			TimeAxis tA = pm.getTimeAxis(pm.getTimeAxisName(this.index));
			
			int i = 0;
			
			if(this.parameter.equals(NAME))
			{
				pageContext.getOut().print(tA.getName());
			}
			else if(this.parameter.equals(START_TIME))
			{
				pageContext.getOut().print(tA.getStartTime());
			}
			else if(this.parameter.equals(END_TIME))
			{
				pageContext.getOut().print(tA.getEndTime());
			}
			else if(this.parameter.equals(LOCATION))
			{
				//a list of axis location names with the selected being the one of this axis
				pageContext.getOut().print("<option");
				if(TimeAxisLocation.BOTTOM == tA.getLocation())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(TimeAxisLocation.BOTTOM.toString());
				pageContext.getOut().print("</option>");
				
				pageContext.getOut().print("<option");
				if(TimeAxisLocation.TOP == tA.getLocation())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(TimeAxisLocation.TOP);
				pageContext.getOut().print("</option>");
				
				pageContext.getOut().print("<option");
				if(TimeAxisLocation.NOT_VISIBLE == tA.getLocation())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(TimeAxisLocation.NOT_VISIBLE);
				pageContext.getOut().print("</option>");
			}
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return SKIP_BODY;
	}

}
