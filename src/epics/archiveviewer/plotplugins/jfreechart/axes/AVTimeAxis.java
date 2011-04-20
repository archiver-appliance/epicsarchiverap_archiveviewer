/*
 * Created on Dec 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.axes;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.TimeZone;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.RectangleEdge;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVTimeAxis extends DateAxis
{
	public AVTimeAxis(String name)
	{
		super(name);
	}
	
	//append TimeZone to the label
	protected AxisState drawLabel(String label, Graphics2D g2,
			Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge,
			AxisState state) {
		
		StringBuffer sb = new StringBuffer(label);
		sb.append(" (");
		sb.append(TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
		sb.append(")");
		// TODO Auto-generated method stub
		return super.drawLabel(sb.toString(), g2, plotArea, dataArea, edge, state);
	}
	
}
