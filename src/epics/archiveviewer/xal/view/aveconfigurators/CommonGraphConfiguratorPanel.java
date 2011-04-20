/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.aveconfigurators;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;
import epics.archiveviewer.xal.view.components.AVColorButton;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommonGraphConfiguratorPanel extends AVAbstractPanel
{
	private JLabel timeAxisLabel;
	private JComboBox timeAxisBox;
	private JLabel rangeAxisLabel;
	private JComboBox rangeAxisBox;
	private JCheckBox visibleCheckBox;
	private JLabel colorLabel;
	private AVColorButton colorButton;
	private JLabel drawTypeLabel;
	private JComboBox drawTypeBox;
	private JLabel widthLabel;
	private JSlider widthSlider;
	private JLabel widthImageLabel;
	
	public CommonGraphConfiguratorPanel()
	{
		init();
	}

	protected void createComponents() {
		this.timeAxisLabel = new JLabel("Time Axis");
		this.timeAxisBox = new JComboBox();
		this.rangeAxisLabel = new JLabel("Range Axis");
		this.rangeAxisBox = new JComboBox();
		this.visibleCheckBox = new JCheckBox("Visible");
		this.visibleCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		this.colorLabel = new JLabel("Color");
		this.colorButton = new AVColorButton();
		this.drawTypeLabel = new JLabel("Type");
		this.drawTypeBox = new JComboBox();
		this.widthLabel = new JLabel("Width");
		this.widthSlider = new JSlider();
		this.widthSlider.setPreferredSize(
				new Dimension(
						this.drawTypeBox.getPreferredSize().width, 
						this.widthSlider.getPreferredSize().height
					)
				);
		
		this.widthImageLabel = new JLabel();
		this.widthImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}

	protected void addComponents() {
		//left part
		JPanel axesLabelsPanel = new JPanel(new GridLayout(0,1));
		axesLabelsPanel.add(this.timeAxisLabel);
		axesLabelsPanel.add(this.rangeAxisLabel);
		
		JPanel axesBoxesPanel = new JPanel(new GridLayout(0,1));
		axesBoxesPanel.add(this.timeAxisBox);
		axesBoxesPanel.add(this.rangeAxisBox);
		
		JPanel axesPanel = new JPanel(new BorderLayout(5, 0));
		axesPanel.add(axesLabelsPanel, BorderLayout.WEST);
		axesPanel.add(axesBoxesPanel, BorderLayout.CENTER);
		
		JPanel visiblePanel = new JPanel(new GridLayout(0,1));
		visiblePanel.add(this.visibleCheckBox);
		visiblePanel.add(new JLabel());
		
		JPanel visiblePanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		visiblePanel2.add(visiblePanel);
		
		JPanel axesAndVisiblePanel = new JPanel(new GridLayout(0,1));
		axesAndVisiblePanel.add(axesPanel);
		axesAndVisiblePanel.add(visiblePanel2);
		
		//right part
		JPanel drawLabelsPanel = new JPanel(new GridLayout(0,1));
		drawLabelsPanel.add(this.colorLabel);
		drawLabelsPanel.add(this.drawTypeLabel);
		drawLabelsPanel.add(this.widthLabel);
		drawLabelsPanel.add(new JLabel());
		
		JPanel drawSettingsPanel = new JPanel(new GridLayout(0,1));
		drawSettingsPanel.add(this.colorButton);
		drawSettingsPanel.add(this.drawTypeBox);
		drawSettingsPanel.add(this.widthSlider);
		drawSettingsPanel.add(this.widthImageLabel);
		
		JPanel drawStuffPanel = new JPanel(new BorderLayout(5, 0));
		drawStuffPanel.add(drawLabelsPanel, BorderLayout.WEST);
		drawStuffPanel.add(drawSettingsPanel, BorderLayout.CENTER);
 		
		JPanel p = new JPanel(new BorderLayout(7, 0));
		p.add(axesAndVisiblePanel, BorderLayout.WEST);
		p.add(drawStuffPanel, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
	}
	
	public JComboBox getTimeAxisBox()
	{
		return this.timeAxisBox;
	}
	
	public JComboBox getRangeAxisBox()
	{
		return this.rangeAxisBox;
	}
	
	public JCheckBox getVisibilityCheckBox()
	{
		return this.visibleCheckBox;
	}
	
	public AVColorButton getAVEColorButton()
	{
		return this.colorButton;
	}
	
	public JComboBox getDrawTypeBox()
	{
		return this.drawTypeBox;
	}
	
	public JSlider getWidthSlider()
	{
		return this.widthSlider;
	}
	
	public JLabel getWidthLabel()
	{
	    return this.widthLabel;
	}
	
	public JLabel getWidthImageLabel()
	{
		return this.widthImageLabel;
	}
}
