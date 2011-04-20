package epics.archiveviewer.plotplugins.jfreechart;

import java.awt.Shape;

import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.xy.XYDataset;

import epics.archiveviewer.plotplugins.jfreechart.datasets.AVAbstractDataset;

/**
 * A chart entity that represents one item within an {@link org.jfree.chart.plot.XYPlot}.
 */
public class AVItemEntity extends XYItemEntity {

    public AVItemEntity(Shape area, 
                        XYDataset dataset, int series, int item,
                        String toolTipText, String urlText) {
        super(area, dataset, series, item, toolTipText, urlText);
    }
    
    //get tooltip on demand
    public String getToolTipText()
    {
    	if(getDataset() instanceof AVAbstractDataset)
    	{
    		return ((AVAbstractDataset)getDataset()).getToolTip(getSeriesIndex(), getItem());
    	}
    	return super.getToolTipText();
    }
}
