/*
 * Created on Nov 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.components.menuitems;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;

import epics.archiveviewer.plotplugins.JFreeChartForTimePlots;
import epics.archiveviewer.plotplugins.jfreechart.JFreeChartWrapper;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IgnoreValueMenuItem extends AVMenuItem
{
	public IgnoreValueMenuItem(JFreeChartWrapper jfcw)
	{
		super("Ignore item", jfcw);
	}		
	
	public void actionPerformed(ActionEvent e) {
		super.jfreeChartWrapper.ignoreItemAt(super.entity);
	}
}
