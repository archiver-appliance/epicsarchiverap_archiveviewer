/*
 * Created on 07.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.preferences;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LegendConfiguratorPanel extends AVAbstractPanel
{
    private JCheckBox showAVENameBox;
	
	private JCheckBox showArchiveNameBox;
	
	private JCheckBox showRangeBox;
	
	private JCheckBox showUnitsBox;

	LegendConfiguratorPanel()
	{
		init();
	}
	
	protected void addComponents() 
	{		
		JPanel nameBoxesPanel = new JPanel(new GridLayout(0,1));
		nameBoxesPanel.add(this.showAVENameBox);
		nameBoxesPanel.add(this.showArchiveNameBox);
		
		JPanel valueBoxesPanel = new JPanel(new GridLayout(0,1));
		valueBoxesPanel.add(this.showRangeBox);
		valueBoxesPanel.add(this.showUnitsBox);
		
		JPanel p = new JPanel(new BorderLayout(20, 0));
		p.add(nameBoxesPanel, BorderLayout.WEST);
		p.add(valueBoxesPanel, BorderLayout.EAST);
		
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		add(p, gbc);
	}
	
	protected void createComponents() {
		this.showAVENameBox = new JCheckBox("Show AVE Name");
		this.showArchiveNameBox = new JCheckBox("Show Archive Name");
		this.showRangeBox = new JCheckBox("Show Range");
		this.showUnitsBox = new JCheckBox("Show Units");
	}
	
	public JCheckBox getAVENameBox()
	{
	   return this.showAVENameBox;
	}
	
	public JCheckBox getArchiveBox()
	{
	   return this.showArchiveNameBox;
	}
	
	public JCheckBox getRangeBox()
	{
	   return this.showRangeBox;
	}
	
	public JCheckBox getUnitsBox()
	{
	   return this.showUnitsBox;
	}
}
