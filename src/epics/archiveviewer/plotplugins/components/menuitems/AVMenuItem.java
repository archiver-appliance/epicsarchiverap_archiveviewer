/*
 * Created on Dec 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.components.menuitems;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;

import epics.archiveviewer.plotplugins.jfreechart.JFreeChartWrapper;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AVMenuItem extends JMenuItem implements ActionListener 
{
	protected final JFreeChartWrapper jfreeChartWrapper;
	protected XYItemEntity entity;
	
	public AVMenuItem(String label, JFreeChartWrapper jfcw)
	{
		super(label);
		this.jfreeChartWrapper = jfcw;
		addActionListener(this);
	}	
	
	public void setEntity(ChartEntity _e)
	{
		if(_e instanceof XYItemEntity)
			this.entity = (XYItemEntity)_e;
		else
			this.entity = null;
		setEnabled(this.entity != null);
	}
}
