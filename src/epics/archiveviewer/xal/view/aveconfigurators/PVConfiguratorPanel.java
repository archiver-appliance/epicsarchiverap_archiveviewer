/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.aveconfigurators;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PVConfiguratorPanel extends AVAbstractPanel
{
	private AVEConfiguratorHeaderPanel headerPanel;
	private CommonGraphConfiguratorPanel commonConfiguratorPanel;
	
	public PVConfiguratorPanel()
	{
		init();
	}

	protected void addComponents() {
		JPanel p = new JPanel(new BorderLayout(0, 10));
		p.add(this.headerPanel, BorderLayout.NORTH);
		p.add(this.commonConfiguratorPanel, BorderLayout.CENTER);
		
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p2.add(p);

		setLayout(new BorderLayout());
		add(p2, BorderLayout.NORTH);
	}

	protected void createComponents() {
		this.headerPanel = new AVEConfiguratorHeaderPanel();
		this.commonConfiguratorPanel = new CommonGraphConfiguratorPanel();
	}
	
	public AVEConfiguratorHeaderPanel getHeaderPanel()
	{
		return this.headerPanel;
	}
	
	public CommonGraphConfiguratorPanel getCommonConfiguratorPanel()
	{
		return this.commonConfiguratorPanel;
	}
}
