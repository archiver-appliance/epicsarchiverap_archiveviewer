/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.export;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportOptionsPanel extends AVAbstractPanel{
	private JLabel methodLabel;
	private JComboBox methodBox;
	private JLabel maxCountPerAVELabel;
	private JTextField countField;
	private JLabel periodLabel;
	private JTextField periodField;
	private JLabel tsFormatLabel;
	private JTextField tsFormatField;
	private JCheckBox exportStatusBox;
	private JButton resetAndCloseButton;
	
	/*
	 * required slider fields. 
	 * last modified: John Lee
	 */
	private JSlider countPerRetrivalSlider;
	private JLabel countPerAVEPerRetrival;
	private final int MIN_RETRIVAL = 3000;
	private final int MAX_RETRIVAL = 9000;

	public ExportOptionsPanel(){
		init();
	}
	
	protected void createComponents() {	
		this.methodLabel = new JLabel("Method");
		this.methodBox = new JComboBox();
		this.maxCountPerAVELabel = new JLabel("Max Count Per AVE");
		this.countField = new JTextField(10);
		this.periodLabel = new JLabel("Period [secs]");
		this.periodField = new JTextField(10);
		this.tsFormatLabel = new JLabel("Timestamp Format");
		this.tsFormatField = new JTextField(15);
		this.exportStatusBox = new JCheckBox("Export Status");
		this.resetAndCloseButton = new JButton("Reset & Close");
		
		// initialize countPerRetrivalSlider
		// last modified: John Lee
		this.countPerRetrivalSlider = new JSlider (JSlider.HORIZONTAL, 0, 6, 0);
		Hashtable label = new Hashtable ();
		int inc = (MAX_RETRIVAL - MIN_RETRIVAL) / 3;
		label.put(new Integer (0), new JLabel(""+MIN_RETRIVAL));
		label.put(new Integer (2), new JLabel(""+(MIN_RETRIVAL+inc)));
		label.put(new Integer (4), new JLabel(""+(MIN_RETRIVAL+inc*2)));
		label.put(new Integer (6), new JLabel(""+MAX_RETRIVAL));
		this.countPerRetrivalSlider.setLabelTable(label);
		this.countPerRetrivalSlider.setPaintTicks(true);
		this.countPerRetrivalSlider.setPaintLabels(true);
		this.countPerRetrivalSlider.setMajorTickSpacing(2);
		this.countPerRetrivalSlider.setMinorTickSpacing(1);
		this.countPerRetrivalSlider.setValue(3);
		this.countPerAVEPerRetrival = new JLabel ("Max Count Per AVE Per Retrieval");
		this.countPerAVEPerRetrival.setAlignmentX(Component.LEFT_ALIGNMENT);
	}

	protected void addComponents() {
		JPanel labelsPanel = new JPanel(new GridLayout(0,1));
		labelsPanel.add(this.methodLabel);
		labelsPanel.add(this.maxCountPerAVELabel);
		labelsPanel.add(this.periodLabel);
		labelsPanel.add(this.tsFormatLabel);
		labelsPanel.add(new JLabel());
		
		JPanel methodBoxPanel = new JPanel(new BorderLayout());
		methodBoxPanel.add(this.methodBox, BorderLayout.WEST);
		
		JPanel checkBoxPanel = new JPanel(new BorderLayout());
		checkBoxPanel.add(this.exportStatusBox, BorderLayout.WEST);

		
		JPanel inputPanel = new JPanel(new GridLayout(0,1));
		inputPanel.add(methodBoxPanel);
		inputPanel.add(this.countField);
		inputPanel.add(this.periodField);
		inputPanel.add(this.tsFormatField);
		inputPanel.add(checkBoxPanel);
		
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout (0,1));
		sliderPanel.add(this.countPerAVEPerRetrival);
		sliderPanel.add(this.countPerRetrivalSlider);
		
		JPanel p = new JPanel(new BorderLayout(5,0));
		p.add(labelsPanel, BorderLayout.WEST);
		p.add(inputPanel, BorderLayout.CENTER);
		p.add(sliderPanel, BorderLayout.SOUTH);
		JPanel resetAndCloseButtonPanel = new JPanel(new FlowLayout());
		resetAndCloseButtonPanel.add(this.resetAndCloseButton);
		
		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
		add(resetAndCloseButtonPanel, BorderLayout.CENTER);
	}
	
	public JComboBox getMethodBox()
	{
		return this.methodBox;
	}
	public JTextField getCountField()
	{
		return this.countField;
	}
	
	public JTextField getPeriodField()
	{
		return this.periodField;
	}
	
	public JTextField getTSFormatField()
	{
		return this.tsFormatField;
	}
	public JCheckBox getExportStatusBox()
	{
		return this.exportStatusBox;
	}
	
	public JButton getResetAndCloseButton()
	{
		return this.resetAndCloseButton;
	}
	/**
	 * getter method of RetrivalSlider
	 * @return retrivalSlider
	 */
	
	public JSlider getCountPerRetrivalSlider (){
		return this.countPerRetrivalSlider;
	}
	/**
	 * get the value that the slider specified
	 * @return
	 */
	public int getCountPerRetrival (){
		int inc = (MAX_RETRIVAL - MIN_RETRIVAL) / (this.countPerRetrivalSlider.getMaximum()-this.countPerRetrivalSlider.getMinimum());
		return this.countPerRetrivalSlider.getValue()*inc+this.MIN_RETRIVAL;
	}
}
