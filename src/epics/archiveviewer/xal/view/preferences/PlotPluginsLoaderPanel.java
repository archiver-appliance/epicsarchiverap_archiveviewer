/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.preferences;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotPluginsLoaderPanel extends AVAbstractPanel
{
	private JComboBox plotPluginsBox;
	
	private JTextArea descriptionArea;
	private JButton loadButton;
	
	PlotPluginsLoaderPanel()
	{
	    init();
	}

	protected void createComponents() {
		this.plotPluginsBox = new JComboBox();
		
		this.descriptionArea = new JTextArea(5, 40);
		this.descriptionArea.setEditable(false);
		this.descriptionArea.setLineWrap(true);
		this.descriptionArea.setWrapStyleWord(true);
		
		this.loadButton = new JButton("Load");
	}

	protected void addComponents() {
		JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
		inputPanel.add(this.plotPluginsBox, BorderLayout.CENTER);
		inputPanel.add(this.loadButton, BorderLayout.EAST);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 0, 10, 0);
		JPanel inputPanel2 = new JPanel(new GridBagLayout());
		inputPanel2.add(inputPanel, gbc);
		
		GridBagConstraints gbc2 = new GridBagConstraints();
		JPanel descriptionAreaPanel = new JPanel(new GridBagLayout());
		descriptionAreaPanel.add(this.descriptionArea, gbc2);
		
		setLayout(new BorderLayout());
		add(inputPanel2, BorderLayout.NORTH);
		add(descriptionAreaPanel, BorderLayout.CENTER);
		
	}
	
	public JTextArea getDescriptionArea()
	{
	    return this.descriptionArea;
	}

	public JButton getLoadButton()
	{
	    return this.loadButton;
	}
	
	public JComboBox getPlotPluginsBox()
	{
		return this.plotPluginsBox;
	}
}
