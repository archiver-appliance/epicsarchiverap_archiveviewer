/*
 * Created on Mar 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal;

import java.awt.Color;
import java.awt.Dimension;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface AVXALConstants {

	public static final String DEFAULT_TERM = "";
	public static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 600);
	public static final String NORMALIZED_RANGE_AXIS_LABEL = "";
	public static final String NO_CHANGE_LABEL = "---no change---";
	//tooltips
	public static final String TIME_INPUT_TOOLTIP = "e.g. 02/03/2005 18:00:00 or -1y +2M -3d +6H -7m +10s";
	public static final String SEARCH_INPUT_TOOLTIP = "enter a search string in glob format";
	public static final String PERIOD_TOOLTIP = 
		"enter a period in seconds for which one interpolated value will be retrieved";
	
	public static final String ENABLE_VISIBILITY_BOX_TOOLTIP = "Enables/disables visibility check box";
	public static final String ENABLE_WIDTH_SLIDER_TOOLTIP = "Enables/disables width slider";
}
