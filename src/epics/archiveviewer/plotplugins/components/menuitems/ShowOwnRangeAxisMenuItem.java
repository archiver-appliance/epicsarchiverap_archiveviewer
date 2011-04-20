/*
 * Created on Dec 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.components.menuitems;

import java.awt.event.ActionEvent;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.plotplugins.jfreechart.JFreeChartWrapper;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVOwnNumberAxis;
import epics.archiveviewer.plotplugins.jfreechart.datasets.AVAbstractDataset;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ShowOwnRangeAxisMenuItem extends AVMenuItem{
	
	private String ownRangeAxisLabel;
	
	public ShowOwnRangeAxisMenuItem(JFreeChartWrapper jfcw)
	{
		super("Show own axis", jfcw);
	}		
	
	public void setEntity(ChartEntity _e)
	{
		if(_e instanceof XYItemEntity)
			this.entity = (XYItemEntity)_e;
		else
			this.entity = null;
		try
		{
			int datasetIndex = super.jfreeChartWrapper.getDatasetIndex(this.entity.getDataset());
			
			ValueAxis yAxis = super.jfreeChartWrapper.getDataPlot().getRangeAxisForDataset(datasetIndex);
			if(yAxis instanceof AVOwnNumberAxis)
			{
				this.ownRangeAxisLabel = yAxis.getLabel();
				setEnabled(true);
				return;
			}
		}
		catch(Exception e)
		{		
			//do nothing
		}
		setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		super.jfreeChartWrapper.setOwnRangeAxisVisible(this.ownRangeAxisLabel);
	}

}
