/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import epics.archiveviewer.xal.view.axesconfigurators.AxesSettingsPanel;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;
import epics.archiveviewer.xal.view.plotplugins.PlotPluginWrappersTabbedPane;


/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MainAVPanel extends AVAbstractPanel
{
	private AVEsPanel avesPanel;
	private AxesSettingsPanel axesSettingsPanel;
	private StatusPanel statusPanel;
	private PlotPluginWrappersTabbedPane plotPluginsWrapperPane;
	
	public MainAVPanel()
	{
		init();
	}
	
	protected void createComponents() {
		this.avesPanel = new AVEsPanel();
		this.axesSettingsPanel = new AxesSettingsPanel();
		this.statusPanel = new StatusPanel();
		this.plotPluginsWrapperPane = new PlotPluginWrappersTabbedPane();
	}

	protected void addComponents() {
		JPanel inputPanel = new JPanel(new BorderLayout(5,5));
		inputPanel.add(this.avesPanel, BorderLayout.CENTER);
		inputPanel.add(this.axesSettingsPanel, BorderLayout.EAST);
		inputPanel.add(this.statusPanel, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.NORTH);
		add(this.plotPluginsWrapperPane, BorderLayout.CENTER);
	}
	
	public AVEsPanel getAVEsPanel()
	{
		return this.avesPanel;
	}
	
	public AxesSettingsPanel getAxesSettingsPanel()
	{
		return this.axesSettingsPanel;
	}
	
	public PlotPluginWrappersTabbedPane getPlotPluginsWrapperPane()
	{
		return this.plotPluginsWrapperPane;
	}
	
	public StatusPanel getStatusPanel()
	{
		return this.statusPanel;
	}

}
