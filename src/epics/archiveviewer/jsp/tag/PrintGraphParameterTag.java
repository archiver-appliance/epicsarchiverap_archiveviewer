/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import java.awt.Color;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.DrawType;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.util.ColorUtilities;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintGraphParameterTag extends TagSupport
{
	public static final String NAME = "name";
	public static final String AD_NAME = "directory";
	public static final String TIME_AXIS_PARAMETER = "time_axis";
	public static final String RANGE_AXIS_PARAMETER = "range_axis";
	public static final String COLOR_PARAMETER = "color";
	public static final String DRAW_TYPE_PARAMETER = "draw_type";
	
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
			
			Graph g = pm.getGraph(pm.getAVEntry(this.index));
			
			int i = 0;
			
			if(this.parameter.equals(NAME))
			{
				pageContext.getOut().print(g.getAVEntry().getName());
			}
			else if(this.parameter.equals(AD_NAME))
			{
				pageContext.getOut().print(g.getAVEntry().getArchiveDirectory().getName());
			}
			else if(this.parameter.equals(TIME_AXIS_PARAMETER))
			{
				//a list of time axis names with the selected being the one of this graph
				String[] timeAxisNames = pm.getTimeAxesNames();
				for(i = 0; i<timeAxisNames.length; i++)
				{
					pageContext.getOut().print("<option");
					if(timeAxisNames[i].equals(g.getTimeAxisLabel()))
						pageContext.getOut().print(" selected='selected'");
					pageContext.getOut().print(">");
					pageContext.getOut().print(timeAxisNames[i]);
					pageContext.getOut().print("</option>");
				}
			}
			else if(this.parameter.equals(RANGE_AXIS_PARAMETER))
			{
				//a list of range axis names with the selected being the one of this graph
				String[] rangeAxisNames = pm.getRangeAxesNames();
				for(i = 0; i<rangeAxisNames.length; i++)
				{
					pageContext.getOut().print("<option");
					if(rangeAxisNames[i].equals(g.getRangeAxisLabel()))
						pageContext.getOut().print(" selected='selected'");
					pageContext.getOut().print(">");
					pageContext.getOut().print(rangeAxisNames[i]);
					pageContext.getOut().print("</option>");
				}
			}
			else if(this.parameter.equals(COLOR_PARAMETER))
			{
				//html color code
				pageContext.getOut().print(ColorUtilities.getHTMLName(g.getColor()));
			}
			else if(this.parameter.equals(DRAW_TYPE_PARAMETER))
			{
				//a list of draw type names with the selected being the one of this graph
				pageContext.getOut().print("<option");
				if(DrawType.STEPS == g.getDrawType())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(DrawType.STEPS.toString());
				pageContext.getOut().print("</option>");
				
				pageContext.getOut().print("<option");
				if(DrawType.SCATTER == g.getDrawType())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(DrawType.SCATTER.toString());
				pageContext.getOut().print("</option>");
				
				pageContext.getOut().print("<option");
				if(DrawType.LINES == g.getDrawType())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(DrawType.LINES.toString());				pageContext.getOut().print("</option>");
			}
		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return SKIP_BODY;
	}

}
