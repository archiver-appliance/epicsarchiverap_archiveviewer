/*
 * Created on 17.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.axes;

import org.jfree.chart.axis.NumberAxis;

import epics.archiveviewer.plotplugins.jfreechart.LogTickUnit;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVCommonRangeAxis extends NumberAxis
{
    private boolean isLogarithmic;
    
    public AVCommonRangeAxis(String label)
    {
        super(label);
        this.isLogarithmic = false;
    }
    
    public void setLogarithmic()
    {
        setTickUnit(new LogTickUnit(getUpperBound() - getLowerBound()));
        this.isLogarithmic = true;
    }
    
    public boolean isLogarithmic()
    {
        return this.isLogarithmic;
    }
}
