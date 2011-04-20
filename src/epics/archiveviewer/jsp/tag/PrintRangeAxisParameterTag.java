/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintRangeAxisParameterTag extends TagSupport{
	public static final String NAME = "name";
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String TYPE = "type";
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
			
			RangeAxis rA = pm.getRangeAxis(pm.getRangeAxisName(this.index));
			
			int i = 0;
			
			if(this.parameter.equals(NAME))
			{
				pageContext.getOut().print(rA.getName());
			}
			else if(this.parameter.equals(MIN))
			{
				String min = "";
				if(rA.getMin() != null)
					min = rA.getMin().toString();
				pageContext.getOut().print(min);
			}
			else if(this.parameter.equals(MAX))
			{
				String max = "";
				if(rA.getMax() != null)
					max = rA.getMax().toString();
				pageContext.getOut().print(max);
			}
			else if(this.parameter.equals(TYPE))
			{
				//a list of axis type names with the selected being the one of this axis
				pageContext.getOut().print("<option");
				if(RangeAxisType.NORMAL == rA.getType())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(RangeAxisType.NORMAL.toString());
				pageContext.getOut().print("</option>");
				
				pageContext.getOut().print("<option");
				if(RangeAxisType.LOG == rA.getType())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(RangeAxisType.LOG);
				pageContext.getOut().print("</option>");
			}
			else if(this.parameter.equals(LOCATION))
			{
				//a list of axis location names with the selected being the one of this axis
				pageContext.getOut().print("<option");
				if(RangeAxisLocation.LEFT == rA.getLocation())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(RangeAxisLocation.LEFT.toString());
				pageContext.getOut().print("</option>");
				
				pageContext.getOut().print("<option");
				if(RangeAxisLocation.RIGHT == rA.getLocation())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(RangeAxisLocation.RIGHT);
				pageContext.getOut().print("</option>");
				
				pageContext.getOut().print("<option");
				if(RangeAxisLocation.NOT_VISIBLE == rA.getLocation())
					pageContext.getOut().print(" selected='selected'");
				pageContext.getOut().print(">");
				pageContext.getOut().print(RangeAxisLocation.NOT_VISIBLE);
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
