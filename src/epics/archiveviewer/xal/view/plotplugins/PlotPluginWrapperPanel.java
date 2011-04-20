/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.plotplugins;

import java.awt.BorderLayout;
import java.awt.Component;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotPluginWrapperPanel extends AVAbstractPanel
{
	private final Component plotPluginComponent;
	private PlotManipulationButtonsPanel plotManipulationButtonsPanel;
	
	public PlotPluginWrapperPanel(Component plotComponent)
	{
		this.plotPluginComponent = plotComponent;
		init();
	}

	protected void createComponents() {
		this.plotManipulationButtonsPanel = new PlotManipulationButtonsPanel();		
	}

	protected void addComponents() {
		setLayout(new BorderLayout());
		add(this.plotPluginComponent, BorderLayout.CENTER);
		add(this.plotManipulationButtonsPanel, BorderLayout.SOUTH);
	}
	
	public Component getPlotPluginComponent()
	{
		return this.plotPluginComponent;
	}
	
	public PlotManipulationButtonsPanel getPlotManipulationButtonsPanel()
	{
		return this.plotManipulationButtonsPanel;
	}

}
