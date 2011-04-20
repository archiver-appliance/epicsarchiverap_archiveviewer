/*
 * Created on Dec 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.axes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.ui.RectangleEdge;

import epics.archiveviewer.AVBaseFacade;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVOwnNumberAxis extends NumberAxis
{
	//label is not displayed
	public AVOwnNumberAxis(String label, Color c)
	{
		super(label);
		setAxisLinePaint(c);
		setTickLabelPaint(c);
		setTickMarkPaint(c);
	}
	
	//don't draw the axis label
    public AxisState draw(Graphics2D g2, 
            double cursor,
            Rectangle2D plotArea, 
            Rectangle2D dataArea, 
            RectangleEdge edge,
            PlotRenderingInfo plotState) 
    {

		AxisState state = null;
		// if the axis is not visible, don't draw it...
		if (!isVisible()) {
		state = new AxisState(cursor);
		// even though the axis is not visible, we need ticks for the gridlines...
		List ticks = refreshTicks(g2, state, plotArea, dataArea, edge); 
		state.setTicks(ticks);
		return state;
		}
		
		// draw the tick marks and labels...
		state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
		
		return state;	
	}
}
