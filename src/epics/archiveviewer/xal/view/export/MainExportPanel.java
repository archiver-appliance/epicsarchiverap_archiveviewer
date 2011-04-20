/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.export;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MainExportPanel extends AVAbstractPanel
{
	private JCheckBox selectedAVEsOnlyBox;
	private JLabel startLabel;
	private JLabel endLabel;
	private JTextField startTimeField;
	private JTextField endTimeField;
	private JLabel fileLabel;
	private JTextField filePathField;
	private JButton featuresButton;
	private JButton fileChooserButton;
	private JComboBox exporterIdsBox;
	/*
	 * added start and end time panel buttons
	 * last modified: John Lee
	 */
	private JButton startTimePanelDisplayButton;
	private JButton endTimePanelDisplayButton;
	public MainExportPanel()
	{
		init();
	}
	
	protected void createComponents() {
		this.selectedAVEsOnlyBox = new JCheckBox("Selected AVEs Only");
		this.startLabel = new JLabel("Start");
		this.endLabel = new JLabel("End");
		this.startTimeField = new JTextField(30);
		this.endTimeField = new JTextField(30);
		this.fileLabel = new JLabel("File");
		this.filePathField = new JTextField(30);
		this.featuresButton = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
		this.startTimePanelDisplayButton = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
		this.endTimePanelDisplayButton = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
		this.fileChooserButton = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
		this.exporterIdsBox = new JComboBox();
	}

	protected void addComponents() {
		JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 1));
		checkBoxPanel.add(this.selectedAVEsOnlyBox);
		
		JPanel labelsPanel = new JPanel(new GridLayout(0,1));
		labelsPanel.add(new JLabel());
		labelsPanel.add(this.startLabel);
		labelsPanel.add(this.endLabel);
		labelsPanel.add(this.fileLabel);
		labelsPanel.add(new JLabel());
		
		JPanel inputPanel = new JPanel(new GridLayout(0,1));
		inputPanel.add(checkBoxPanel);
		inputPanel.add(this.startTimeField);
		inputPanel.add(this.endTimeField);
		inputPanel.add(this.filePathField);
		inputPanel.add(this.exporterIdsBox);
		
		JPanel buttonsPanel = new JPanel(new GridLayout(0,1));
		buttonsPanel.add(this.featuresButton);
		buttonsPanel.add(this.startTimePanelDisplayButton);
		buttonsPanel.add(this.endTimePanelDisplayButton);
		buttonsPanel.add(this.fileChooserButton);
		buttonsPanel.add(new JLabel());
		
		inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(labelsPanel, BorderLayout.WEST);
		p.add(inputPanel, BorderLayout.CENTER);
		p.add(buttonsPanel, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
	}
	
	public JCheckBox getSelectedAVEsOnlyBox()
	{
		return this.selectedAVEsOnlyBox;
	}
	
	public JTextField getStartTimeField()
	{
		return this.startTimeField;
	}
	
	public JTextField getEndTimeField()
	{
		return this.endTimeField;
	}
	
	public JTextField getFilePathField()
	{
		return this.filePathField;
	}
	
	public JButton getFeaturesButton()
	{
		return this.featuresButton;
	}
	
	public JButton getFileChooserButton()
	{
		return this.fileChooserButton;
	}

	public JComboBox getExporterIdsBox()
	{
		return this.exporterIdsBox;
	}
	public JButton getStartTimePanelDisplayButton()
	{
		return this.startTimePanelDisplayButton;
	}
	public JButton getEndTimePanelDisplayButton()
	{
		return this.endTimePanelDisplayButton;
	}
}
