/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.aveconfigurators;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AdditionalConfiguratorPanelForFormulas extends AVAbstractPanel
{
	private JLabel methodLabel;
	private JLabel maxCountLabel;
	private JLabel periodLabel;
	private JComboBox methodBox;
	private JTextField maxCountField;
	private JTextField periodField;
	
	public AdditionalConfiguratorPanelForFormulas()
	{
		init();
	}
	
	protected void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(0,1));
		labelsPanel.add(this.methodLabel);
		labelsPanel.add(this.maxCountLabel);
		labelsPanel.add(this.periodLabel);
		
		JPanel inputPanel = new JPanel(new GridLayout(0,1));
		inputPanel.add(this.methodBox);
		inputPanel.add(this.maxCountField);
		inputPanel.add(this.periodField);
		
		setLayout(new BorderLayout(5, 0));
		add(labelsPanel, BorderLayout.WEST);
		add(inputPanel, BorderLayout.CENTER);
	}

	protected void createComponents() {
		this.methodLabel = new JLabel("Retrieval Method");
		this.maxCountLabel = new JLabel("Max Count");
		this.periodLabel = new JLabel("Period [secs]");
		this.methodBox = new JComboBox();
		this.maxCountField = new JTextField(10);
		this.periodField = new JTextField(10);
	}
	

	public JComboBox getMethodBox()
	{
		return this.methodBox;
	}
	
	public JTextField getMaxCountField()
	{
		return this.maxCountField;
	}
	
	public JTextField getPeriodField()
	{
		return this.periodField;
	}
}
