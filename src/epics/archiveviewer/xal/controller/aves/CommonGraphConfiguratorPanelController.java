/*
 * Created on Feb 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import epics.archiveviewer.DrawType;
import epics.archiveviewer.base.Icons;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.listeners.AVEColorButtonListener;
import epics.archiveviewer.xal.controller.listeners.WidthSliderListener;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.aveconfigurators.CommonGraphConfiguratorPanel;
import epics.archiveviewer.xal.view.components.AVColorButton;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommonGraphConfiguratorPanelController {
	
	private final CommonGraphConfiguratorPanel cgcPanel;
	
	//g mustnot be NULL
	private void displayGraphParameters(Graph g)
	{
		this.cgcPanel.getTimeAxisBox().setSelectedItem(g.getTimeAxisLabel());
		this.cgcPanel.getRangeAxisBox().setSelectedItem(g.getRangeAxisLabel());
		this.cgcPanel.getVisibilityCheckBox().setSelected(g.isVisible());
    	this.cgcPanel.getAVEColorButton().setColor(g.getColor());
    	this.cgcPanel.getDrawTypeBox().setSelectedItem(g.getDrawType().toString());
    	this.cgcPanel.getWidthSlider().setValue(AVXALUtilities.drawWidthToPercent(g.getDrawWidth()));   
	}

	//g may be null
	public CommonGraphConfiguratorPanelController(AVController avController, CommonGraphConfiguratorPanel cgcp, Graph g)
	throws Exception
	{
    	//set up
    	this.cgcPanel = cgcp;
    	
    	JComboBox box = this.cgcPanel.getTimeAxisBox();
    	
    	String[] timeAxesNames = avController.getAVBase().getPlotModel().getTimeAxesNames();
    	
    	int i=0;
    	for(i=0; i<timeAxesNames.length; i++)
    	{
    		box.addItem(timeAxesNames[i]);
    	}
    	
    	box = this.cgcPanel.getRangeAxisBox();
	
    	String[] rangeAxesNames = avController.getAVBase().getPlotModel().getRangeAxesNames();
    	
    	for(i=0; i<rangeAxesNames.length; i++)
    	{
    		box.addItem(rangeAxesNames[i]);
    	}
    	//normalized
    	box.addItem(AVXALConstants.NORMALIZED_RANGE_AXIS_LABEL);
    	  	
      	AVColorButton colorButton = this.cgcPanel.getAVEColorButton();
      	
    	box = this.cgcPanel.getDrawTypeBox();
    	
    	box.addItem(DrawType.LINES.toString());
    	box.addItem(DrawType.SCATTER.toString());
    	box.addItem(DrawType.STEPS.toString());
    	
    	if(g!=null)
    		displayGraphParameters(g);
    	
    	//listeners
    	this.cgcPanel.getWidthSlider().addChangeListener(new WidthSliderListener(this.cgcPanel));
    	
    	colorButton.addActionListener
    	(
			new AVEColorButtonListener
			(
	    			new ActionListener()
	    			{
	
						public void actionPerformed(ActionEvent e)
						{
							JSlider slider = cgcPanel.getWidthSlider();
							int v = slider.getValue();
							slider.setValue(v > 0 ? v - 1 : v + 1);
						}
	    			}
			)
    	);
	}
	
	public void storeConfiguratorParameters(Graph g)
	{
    	g.setTimeAxisLabel(this.cgcPanel.getTimeAxisBox().getSelectedItem().toString());
    	g.setRangeAxisLabel(this.cgcPanel.getRangeAxisBox().getSelectedItem().toString());   	
    	g.setVisible(this.cgcPanel.getVisibilityCheckBox().isSelected());
      	g.setColor(this.cgcPanel.getAVEColorButton().getColor());
    	g.setDrawType(DrawType.getDrawType(this.cgcPanel.getDrawTypeBox().getSelectedItem().toString()));
    	g.setDrawWidth(AVXALUtilities.percentToDrawWidth(this.cgcPanel.getWidthSlider().getValue()));
	}

}
