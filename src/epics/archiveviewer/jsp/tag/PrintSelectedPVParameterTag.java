/*
 * Created on Mar 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.jsp.tag;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.jsp.JSPConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintSelectedPVParameterTag extends PrintMatchingPVParameterTag
{
	
	protected AVEntry getMatchingPV() throws Exception 
	{
		String[] selectedPVIndices = 
			(String[]) pageContext.getRequest().getParameterValues(JSPConstants.PV_INDEX_REQUEST_NAME);
		return 
				avBase.getMatchingAVEsRepository().getMatchingAVE(
						Integer.parseInt(selectedPVIndices[index])
						);
				
	}
}
