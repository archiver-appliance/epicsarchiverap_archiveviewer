/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.preferences;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;
import epics.archiveviewer.xal.view.components.AVColorButton;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OtherPlotSettingsPanel extends AVAbstractPanel
{
	private JLabel plotTitleLabel;
	private JTextField plotTitleField;
	
	private JLabel plotBGColorLabel;
	private AVColorButton plotBGColorButton;
	
	OtherPlotSettingsPanel()
	{
		init();
	}
	
	protected void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(0, 1));
		labelsPanel.add(this.plotTitleLabel);
		labelsPanel.add(this.plotBGColorLabel);
		
		JPanel plotBGColorButtonPanel = new JPanel(new BorderLayout());
		plotBGColorButtonPanel.add(this.plotBGColorButton, BorderLayout.WEST);
		
		JPanel inputPanel = new JPanel(new GridLayout(0, 1));
		inputPanel.add(this.plotTitleField);
		inputPanel.add(plotBGColorButtonPanel);
		
		JPanel p = new JPanel(new BorderLayout(10, 0));
		p.add(labelsPanel, BorderLayout.WEST);
		p.add(inputPanel, BorderLayout.EAST);
		
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		add(p, gbc);
	}

	protected void createComponents() {
		this.plotTitleLabel = new JLabel("Title");
		this.plotTitleField = new JTextField(15);
		this.plotBGColorLabel = new JLabel("Background");
		this.plotBGColorButton = new AVColorButton();

	}
	
	public JTextField getPlotTitleField()
	{
	    return this.plotTitleField;
	}
	
	public AVColorButton getPlotBGColorButton()
	{
		return this.plotBGColorButton;
	}
}
