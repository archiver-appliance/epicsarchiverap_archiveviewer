/*
 * Created on Feb 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.axesconfigurators;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TimeAxesConfigurator extends AVAbstractPanel
{
	private static final String TIME_PANEL_BUTTON_LABEL = "...";
	private static final int TEXT_FIELD_SIZE = 12;
	
	private JComboBox timeAxesLabelsBox;
	private JButton moreButton;
	
	private JLabel startTimeLabel;
	private JTextField startTimeField;
	private JButton startTimePanelDisplayButton;
	
	private JLabel endTimeLabel;
	private JTextField endTimeField;
	private JButton endTimePanelDisplayButton;
	
	private JComboBox timeAxisLocationsBox;
	
	public TimeAxesConfigurator()
	{
		init();
	}
	
	/**
	 * @see epics.archiveviewer.xal.view.components.AVAbstractPanel#addComponents()
	 */
	protected void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(0, 1));
		labelsPanel.add(this.startTimeLabel);
		labelsPanel.add(this.endTimeLabel);
		labelsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

		JPanel fieldsPanel = new JPanel(new GridLayout(0, 1));
		fieldsPanel.add(this.startTimeField);
		fieldsPanel.add(this.endTimeField);

		JPanel inputTimeButtonsPanel = new JPanel(new GridLayout(0, 1));
		inputTimeButtonsPanel.add(this.startTimePanelDisplayButton);
		inputTimeButtonsPanel.add(this.endTimePanelDisplayButton);
		
		JPanel labelsFieldsInputTimeButtonsPanel = new JPanel(new BorderLayout());
		labelsFieldsInputTimeButtonsPanel.add(labelsPanel, BorderLayout.WEST);
		labelsFieldsInputTimeButtonsPanel.add(fieldsPanel, BorderLayout.CENTER);
		labelsFieldsInputTimeButtonsPanel.add(inputTimeButtonsPanel, BorderLayout.EAST);
		
		labelsFieldsInputTimeButtonsPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		
		BorderLayout b = new BorderLayout();
		b.setHgap(4);
		JPanel timeAxisLocationPanel = new JPanel(b);
		timeAxisLocationPanel.add(this.timeAxisLocationsBox, BorderLayout.EAST);
		timeAxisLocationPanel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
		
		JPanel rangeAxisConfigPanel = new JPanel(new BorderLayout());
		rangeAxisConfigPanel.add(labelsFieldsInputTimeButtonsPanel, BorderLayout.CENTER);
		rangeAxisConfigPanel.add(timeAxisLocationPanel, BorderLayout.SOUTH);
		
		JPanel moreButtonPanel = new JPanel(new BorderLayout());
		moreButtonPanel.add(this.moreButton, BorderLayout.EAST);
		
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(this.timeAxesLabelsBox, BorderLayout.WEST);
		headerPanel.add(moreButtonPanel, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);
		add(rangeAxisConfigPanel, BorderLayout.CENTER);
		setBorder(new BevelBorder(BevelBorder.RAISED)
		{
	    	public Insets getBorderInsets(Component c)       
	    	{
	    		return new Insets(1, 1, 1, 1);
	    	}
        });	
	}
	
	/**
	 * @see epics.archiveviewer.xal.view.components.AVAbstractPanel#createComponents()
	 */
	protected void createComponents() {
		this.timeAxesLabelsBox = new JComboBox();
		this.moreButton = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
		this.startTimeLabel = new JLabel("Start");
		this.startTimeField = new JTextField(TEXT_FIELD_SIZE);
		this.startTimePanelDisplayButton = new JButton(TIME_PANEL_BUTTON_LABEL);
		this.endTimeLabel = new JLabel("End");
		this.endTimeField = new JTextField(TEXT_FIELD_SIZE);
		this.endTimePanelDisplayButton = new JButton(TIME_PANEL_BUTTON_LABEL);
		this.timeAxisLocationsBox = new JComboBox();
		
		this.startTimeLabel.setPreferredSize(
				new Dimension(
						this.startTimeLabel.getPreferredSize().width,
						this.timeAxesLabelsBox.getPreferredSize().height
					)
				);
	}
	
	public JComboBox getTimeAxisLabelsBox()
	{
		return this.timeAxesLabelsBox;
	}
	
	public JTextField getStartTimeField()
	{
		return this.startTimeField;
	}
	
	public JButton getStartTimePanelDisplayButton()
	{
		return this.startTimePanelDisplayButton;
	}
	

	public JTextField getEndTimeField()
	{
		return this.endTimeField;
	}
	
	public JButton getEndTimePanelDisplayButton()
	{
		return this.endTimePanelDisplayButton;
	}
	
	public JComboBox getTimeAxisLocationBox()
	{
		return this.timeAxisLocationsBox;
	}
	
	public JButton getMoreButton()
	{
		return this.moreButton;
	}
}
