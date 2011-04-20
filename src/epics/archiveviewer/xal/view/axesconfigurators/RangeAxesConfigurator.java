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
public class RangeAxesConfigurator extends AVAbstractPanel
{
	private static final int TEXT_FIELD_SIZE = 12;
	
	private JComboBox rangeAxesLabelsBox;
	private JButton moreButton;
	
	private JLabel rangeMaxLabel;
	private JTextField rangeMaxField;
	
	private JLabel rangeMinLabel;
	private JTextField rangeMinField;
	
	private JLabel rangeAxisTypeLabel;
	private JComboBox rangeAxisTypesBox;
	
	private JComboBox rangeAxisLocationsBox;
	
	public RangeAxesConfigurator()
	{
		init();
	}
	
	/**
	 * @see epics.archiveviewer.xal.view.components.AVAbstractPanel#addComponents()
	 */
	protected void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(0, 1));
		labelsPanel.add(this.rangeMaxLabel);
		labelsPanel.add(this.rangeMinLabel);
		labelsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

		JPanel fieldsPanel = new JPanel(new GridLayout(0, 1));
		fieldsPanel.add(this.rangeMaxField);
		fieldsPanel.add(this.rangeMinField);
		
		JPanel labelsAndFieldsPanel = new JPanel(new BorderLayout());
		labelsAndFieldsPanel.add(labelsPanel, BorderLayout.WEST);
		labelsAndFieldsPanel.add(fieldsPanel, BorderLayout.CENTER);
		
		labelsAndFieldsPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		
		JPanel rangeAxisTypePanel = new JPanel(new BorderLayout(4,0));
		rangeAxisTypePanel.add(this.rangeAxisTypeLabel, BorderLayout.WEST);
		rangeAxisTypePanel.add(this.rangeAxisTypesBox, BorderLayout.EAST);
		
		JPanel typeAndLocationPanel = new JPanel(new BorderLayout(10,0));
		typeAndLocationPanel.add(rangeAxisTypePanel, BorderLayout.WEST);
		typeAndLocationPanel.add(this.rangeAxisLocationsBox, BorderLayout.EAST);
		typeAndLocationPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		JPanel rangeAxisConfigPanel = new JPanel(new BorderLayout());
		rangeAxisConfigPanel.add(labelsAndFieldsPanel, BorderLayout.CENTER);
		rangeAxisConfigPanel.add(typeAndLocationPanel, BorderLayout.SOUTH);
		
		JPanel moreButtonPanel = new JPanel(new BorderLayout());
		moreButtonPanel.add(this.moreButton, BorderLayout.EAST);

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(this.rangeAxesLabelsBox, BorderLayout.WEST);
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
		this.rangeAxesLabelsBox = new JComboBox();
		this.moreButton = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
		this.rangeMaxLabel = new JLabel("Max");
		this.rangeMaxField = new JTextField(TEXT_FIELD_SIZE);
		this.rangeMinLabel = new JLabel("Min");
		this.rangeMinField = new JTextField(TEXT_FIELD_SIZE);
		this.rangeAxisTypeLabel = new JLabel("Type");
		this.rangeAxisTypesBox = new JComboBox();
		this.rangeAxisLocationsBox = new JComboBox();
		
		this.rangeMaxLabel.setPreferredSize(
				new Dimension(
						this.rangeMaxLabel.getPreferredSize().width,
						this.rangeAxesLabelsBox.getPreferredSize().height
					)
				);
	}
	
	public JComboBox getRangeAxisLabelsBox()
	{
		return this.rangeAxesLabelsBox;
	}
	
	public JTextField getMinField()
	{
		return this.rangeMinField;
	}
	
	public JTextField getMaxField()
	{
		return this.rangeMaxField;
	}
	
	public JComboBox getRangeAxisLocationBox()
	{
		return this.rangeAxisLocationsBox;
	}
	
	public JComboBox getRangeAxisTypeBox()
	{
		return this.rangeAxisTypesBox;
	}
	
	public JButton getMoreButton()
	{
		return this.moreButton;
	}
}
