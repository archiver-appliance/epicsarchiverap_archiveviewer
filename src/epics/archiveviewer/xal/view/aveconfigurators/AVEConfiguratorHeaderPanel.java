/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.aveconfigurators;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEConfiguratorHeaderPanel extends AVAbstractPanel{
	private JLabel aveNameLabel;
	private JLabel directoryNameLabel;	
	
	public AVEConfiguratorHeaderPanel(){
		init();
	}
	
	protected void createComponents() {
		this.aveNameLabel = new JLabel();
		this.aveNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.directoryNameLabel = new JLabel();
		this.directoryNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}

	
	protected void addComponents() {
		JPanel nameAndDirectoryPanel = new JPanel(new BorderLayout());
		nameAndDirectoryPanel.add(this.aveNameLabel, BorderLayout.NORTH);
		nameAndDirectoryPanel.add(this.directoryNameLabel,BorderLayout.CENTER);

		this.setLayout(new BorderLayout(0,10));
		this.add(nameAndDirectoryPanel, BorderLayout.NORTH);
	}
	
	public JLabel getAVENameLabel()
	{
		return this.aveNameLabel;
	}
	
	public JLabel getDirectoryLabel()
	{
		return this.directoryNameLabel;
	}

}
