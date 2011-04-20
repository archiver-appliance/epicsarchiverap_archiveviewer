/*
 * Created on Mar 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.model.MatchingAVEsRepository;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LoopTag extends BodyTagSupport
{
	public static final String COUNTER_VARIABLE = "counter";
	
	public static final String MATCHING_PVS = "matching_pvs";
	public static final String SELECTED_PVS = "selected_pvs";
	public static final String GRAPHS = "graphs";
	public static final String TIME_AXES = "time_axes";
	public static final String RANGE_AXES = "range_axes";
	
	private String set;
	private int limit;
	
	public void setSet(String s)
	{
		this.set = s;
	}
	
	public int doAfterBody() throws JspException {		
		int currentIndex = ((Integer)pageContext.getAttribute(COUNTER_VARIABLE)).intValue();

		currentIndex++;
		if(currentIndex == this.limit)
			return SKIP_BODY;	
		
		pageContext.setAttribute(COUNTER_VARIABLE, new Integer(currentIndex));
		return EVAL_BODY_AGAIN;
	}
	
	public int doStartTag() throws JspException {
		AVBase avBase = (AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
		if(this.set.equals(MATCHING_PVS))
		{
			limit = 
				avBase.
					getMatchingAVEsRepository().
						getNrOfMatchingAVEs();
		}
		else if(this.set.equals(SELECTED_PVS))
		{
			limit = 
				((String[]) pageContext.getRequest().getParameterValues(JSPConstants.PV_INDEX_REQUEST_NAME)).
					length;
		}
		else if(this.set.equals(GRAPHS))
		{
			limit = 
				avBase.
					getPlotModel().
						getAVEntries().
							length;
		}
		else if(this.set.equals(TIME_AXES))
		{
			limit = 
				avBase.
					getPlotModel().
						getTimeAxesNames().
							length;
		}
		else if(this.set.equals(RANGE_AXES))
		{
			limit = 
				avBase.
					getPlotModel().
						getRangeAxesNames().
							length;
		}
		pageContext.setAttribute(COUNTER_VARIABLE, new Integer(0));
		return EVAL_BODY_BUFFERED;
	}
	
	public int doEndTag() throws JspException {
		try
		{
			//necessary
			String s = bodyContent.getString();
			
			bodyContent.clear();
			StringBuffer result = new StringBuffer();
			StringTokenizer st = new StringTokenizer(s);
			while(st.hasMoreElements())
			{
				result.append(st.nextToken() + "\n");
			}
			
			bodyContent.print(result.toString());
			bodyContent.writeOut(getPreviousOut());
		}
		catch(IOException e)
		{
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}
}
