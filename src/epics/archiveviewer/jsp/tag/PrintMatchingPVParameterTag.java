/*
 * Created on Mar 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.AVEntry;
import epics.archiveviewer.AVEntryInfo;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintMatchingPVParameterTag extends TagSupport
{
	protected final String NAME_PARAMETER = "name";
	protected final String AD_PARAMETER = "directory";
	protected final String START_TIME_PARAMETER = "starttime";
	protected final String END_TIME_PARAMETER = "endtime";
	
	protected AVBase avBase;
	protected String parameter;
	protected int index;
	
	protected AVEntry getMatchingPV() throws Exception
	{
		return this.avBase.getMatchingAVEsRepository().getMatchingAVE(this.index);
	}
	
	public void setIndex(int _index)
	{
		this.index = _index;
	}
	
	public void setParameter(String param)
	{
		this.parameter = param;
	}
	
	public int doStartTag() throws JspException {
		try
		{
			this.avBase = (AVBase) pageContext.getSession().getAttribute(JSPConstants.AVBASE_SESSION_NAME);
			
			AVEntry ave = getMatchingPV();
			
			if(parameter.equalsIgnoreCase(NAME_PARAMETER))
			{
				pageContext.getOut().print(ave.getName());
			}
			else if(parameter.equalsIgnoreCase(AD_PARAMETER))
			{
				pageContext.getOut().print(ave.getArchiveDirectory().getName());
			}
			else
			{
				AVEntryInfo info = this.avBase.getClient().getAVEInfo(ave);
				String s = "";
				if(parameter.equalsIgnoreCase(START_TIME_PARAMETER))
				{
					s = AVBaseConstants.MAIN_DATE_FORMAT.format(
										new Date((long)info.getArchivingStartTime())
											);
				}
				else if(parameter.equalsIgnoreCase(END_TIME_PARAMETER))
				{
					s = AVBaseConstants.MAIN_DATE_FORMAT.format(
							new Date((long)info.getArchivingEndTime())
								);
				}
				pageContext.getOut().print(s);
			}

		}
		catch(Exception e)
		{
			throw new JspException(e.getMessage());
		}	
		return SKIP_BODY;
	}
}
