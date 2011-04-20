/*
 * Created on Feb 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.axes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JComboBox;

import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.listeners.PlotModelAdapter;
import epics.archiveviewer.base.model.listeners.PlotModelListener;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.axesconfigurators.RangeAxesConfigurator;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RangeAxesConfiguratorController extends AxesConfiguratorController
{
	public static void storeVisibleRangeAxisParametersInPlotModel(AVController avController)
	{
		AVXALUtilities.storeRangeAxisParametersInPlotModel(
				avController, 
				(String)
				avController.getMainAVPanel().getAxesSettingsPanel().
				getRangeAxesConfigurator().getRangeAxisLabelsBox().getSelectedItem());
	}
	
	private final AVController avController;
	private final RangeAxesConfigurator rac;
	private final ItemListener rangeAxesNamesBoxListener;
	
	private ItemListener createRangeAxisLabelsListener()
	{
		return new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.DESELECTED)
				{
					storeRangeAxesParametersInPlotModel(e.getItem().toString());
				}
				else if(e.getStateChange() == ItemEvent.SELECTED)
				{
					displaySelectedAxisParameters();
				}
			}			
		};
	}
	
	private ActionListener createAxesFeaturesMenuListener()
	{
		return new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	storeVisibleRangeAxisParametersInPlotModel(avController);
                new AxesFeaturesMenuController(avController, RangeAxesConfiguratorController.this);
            }
        };
	}
	
	private PlotModelListener createRangeAxesModelListener()
	{
		return new PlotModelAdapter()
		{

			public void rangeAxesUpdated() {
				PlotModel pm = avController.getAVBase().getPlotModel();
				String[] rANames = pm.getRangeAxesNames();
				
				loadRangeAxesNamesIntoBox(rANames);
			}			
		};
	}
	
	private void storeRangeAxesParametersInPlotModel(String rAName)
	{
		try
		{
			PlotModel pm = this.avController.getAVBase().getPlotModel();
			
			Double min = null;
			if(this.rac.getMinField().getText().equals("") == false)
				min = Double.valueOf(this.rac.getMinField().getText());

			Double max = null;
			if(this.rac.getMaxField().getText().equals("") == false)
				max = Double.valueOf(this.rac.getMaxField().getText());
			
			pm.addRangeAxis(
					new RangeAxis(
						rAName,
						min,
						max,
						RangeAxisType.getRangeAxisType(
								this.rac.getRangeAxisTypeBox().getSelectedItem().toString()),
						RangeAxisLocation.getAxisLocation(
								this.rac.getRangeAxisLocationBox().getSelectedItem().toString())
						)
				);
		}
		catch(Exception e)
		{
			this.avController.getAVBase().displayError("Can't commit the axis parameters", e);
		}
	}
	
	private void loadRangeAxesNamesIntoBox(String[] names)
	{
		JComboBox rANamesBox = this.rac.getRangeAxisLabelsBox();
		
		rANamesBox.removeItemListener(this.rangeAxesNamesBoxListener);
		
		loadAxesNamesIntoBox(names, rANamesBox);
		
		displaySelectedAxisParameters();
		rANamesBox.addItemListener(this.rangeAxesNamesBoxListener);
	}
	
	protected void displaySelectedAxisParameters()
	{
		try
		{
			RangeAxis selectedRA = 
				this.avController.getAVBase().getPlotModel().getRangeAxis(
						(String) this.rac.getRangeAxisLabelsBox().getSelectedItem());
			
			String min = null;
			if(selectedRA.getMin() == null)
				min = "";
			else
				min = selectedRA.getMin().toString();
			
			String max = null;
			if(selectedRA.getMax() == null)
				max = "";
			else
				max = selectedRA.getMax().toString();
				
			this.rac.getMinField().setText(min);
			this.rac.getMaxField().setText(max);
			this.rac.getRangeAxisTypeBox().setSelectedItem(selectedRA.getType().toString());
			this.rac.getRangeAxisLocationBox().setSelectedItem(selectedRA.getLocation().toString());
		}
		catch(Exception e)
		{
			avController.getAVBase().displayError("Can't display axis parameters", e);
		}
	}

	public RangeAxesConfiguratorController(AVController avc, RangeAxesConfigurator _rac)
	{
		this.avController = avc;
		this.rac = _rac;
		this.rangeAxesNamesBoxListener = createRangeAxisLabelsListener();
		
		PlotModel pm = this.avController.getAVBase().getPlotModel();
		
		JComboBox raTypeBox = this.rac.getRangeAxisTypeBox();
		raTypeBox.addItem(RangeAxisType.NORMAL.toString());
		raTypeBox.addItem(RangeAxisType.LOG.toString());

		JComboBox rALocationBox = this.rac.getRangeAxisLocationBox();
		rALocationBox.addItem(RangeAxisLocation.LEFT.toString());
		rALocationBox.addItem(RangeAxisLocation.RIGHT.toString());
		rALocationBox.addItem(RangeAxisLocation.NOT_VISIBLE.toString());
		
		this.rac.getMoreButton().addActionListener(createAxesFeaturesMenuListener());
		pm.addPlotModelListener(
				createRangeAxesModelListener());
		
		loadRangeAxesNamesIntoBox(pm.getRangeAxesNames());
	}
	
	public String getSelectedAxisName()
	{
		return (String) this.rac.getRangeAxisLabelsBox().getSelectedItem();
	}
	
	public JButton getMoreButton()
	{
		return this.rac.getMoreButton();
	}
}
