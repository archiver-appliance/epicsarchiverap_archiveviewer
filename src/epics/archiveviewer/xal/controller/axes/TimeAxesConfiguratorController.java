/*
 * Created on Feb 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.axes;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.listeners.PlotModelAdapter;
import epics.archiveviewer.base.model.listeners.PlotModelListener;
import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.axesconfigurators.TimeAxesConfigurator;
import epics.archiveviewer.xal.view.components.AVDialog;
import epics.archiveviewer.xal.view.timeinput.TimeInputPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TimeAxesConfiguratorController extends AxesConfiguratorController
{
	public static void storeVisibleTimeAxisParametersInPlotModel(AVController avController)
	{
		AVXALUtilities.storeTimeAxisParametersInPlotModel(
				avController, 
				(String)
				avController.getMainAVPanel().getAxesSettingsPanel().
				getTimeAxesConfigurator().getTimeAxisLabelsBox().getSelectedItem());
	}
	
	private final AVController avController;
	private final TimeAxesConfigurator tac;
	private final ItemListener timeAxesNamesBoxListener;
	
	/*
	 * Added SliderTimePanel
	 * @param tip
	 * @param textField
	 * @return
	 * modified on: 8/1/06
	 * John Lee
	 */
	private ActionListener createTimePanelOKListener(
			final TimeInputPanel tip, final JTextField textField)
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				if(tip.getAbsoluteTP().isVisible())
				{
					textField.setText(tip.getAbsoluteTP().getSelectedTime());
				}
				else if (tip.getRelativeTP().isVisible())
					textField.setText(tip.getRelativeTP().getSelectedTime());
				
				else if (tip.getSliderTP().isVisible())
					textField.setText(tip.getSliderTP().getSelectedTime());
			}
			
		};
	}
	
	private ActionListener createTimePanelButtonListener(final boolean forStartTime)
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
			 	JTextField timeField = null;
			 	JButton locationButton = null;
			 	if(forStartTime)
			 	{
			 		timeField = tac.getStartTimeField();
			 		locationButton = tac.getStartTimePanelDisplayButton();
			 	}
			 	else
			 	{
			 		timeField = tac.getEndTimeField();
			 		locationButton = tac.getEndTimePanelDisplayButton();
			 	}
			 	
			 	
			 	TimeInputPanel tip = 
			 		new TimeInputPanel(tac.getStartTimeField().getText(), tac.getEndTimeField().getText(), forStartTime);
			 	
                new AVDialog(
                        tip,
                        avController.getMainWindow(),
                        "Time Input Dialog",
                        false,
                        false,
                        locationButton,
                        createTimePanelOKListener(tip, timeField), 
                        FlowLayout.RIGHT
                        );
			}
			
		};
	}
	
	private ItemListener createTimeAxisLabelsListener()
	{
		return new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.DESELECTED)
				{
					AVXALUtilities.storeTimeAxisParametersInPlotModel(avController, (String) e.getItem());
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
            	storeVisibleTimeAxisParametersInPlotModel(avController);
                new AxesFeaturesMenuController(avController, TimeAxesConfiguratorController.this);
            }
        };
	}
	
	private PlotModelListener createTimeAxesModelListener()
	{
		return new PlotModelAdapter()
		{

			public void timeAxesUpdated() {

				PlotModel pm = avController.getAVBase().getPlotModel();
				String[] tANames = pm.getTimeAxesNames();
				
				loadTimeAxesNamesIntoBox(tANames);
			}			
		};
	}
	
	private void loadTimeAxesNamesIntoBox(String[] names)
	{
		JComboBox tANamesBox = this.tac.getTimeAxisLabelsBox();
		//MUST be done
		tANamesBox.removeItemListener(this.timeAxesNamesBoxListener);
		
		loadAxesNamesIntoBox(names, tANamesBox);
		displaySelectedAxisParameters();
		tANamesBox.addItemListener(this.timeAxesNamesBoxListener);
	}
	
	protected void displaySelectedAxisParameters()
	{
		try
		{
			TimeAxis selectedTA = 
				this.avController.getAVBase().getPlotModel().getTimeAxis(
						(String) this.tac.getTimeAxisLabelsBox().getSelectedItem());
			this.tac.getStartTimeField().setText(selectedTA.getStartTime());
			this.tac.getEndTimeField().setText(selectedTA.getEndTime());
			this.tac.getTimeAxisLocationBox().setSelectedItem(selectedTA.getLocation().toString());
		}
		catch(Exception e)
		{
			this.avController.getAVBase().displayError("Can't display selected axis", e);
		}
	}

	public TimeAxesConfiguratorController(AVController avc, TimeAxesConfigurator _tac)
	{
		this.avController = avc;
		this.tac = _tac;
		this.timeAxesNamesBoxListener = createTimeAxisLabelsListener();
		
		
		PlotModel pm = this.avController.getAVBase().getPlotModel();

		JComboBox tALocationBox = this.tac.getTimeAxisLocationBox();
		tALocationBox.addItem(TimeAxisLocation.BOTTOM.toString());
		tALocationBox.addItem(TimeAxisLocation.TOP.toString());
		tALocationBox.addItem(TimeAxisLocation.NOT_VISIBLE.toString());
		
		this.tac.getStartTimePanelDisplayButton().addActionListener(
				createTimePanelButtonListener(true));
		this.tac.getEndTimePanelDisplayButton().addActionListener(
				createTimePanelButtonListener(false));
		this.tac.getMoreButton().addActionListener(createAxesFeaturesMenuListener());
		
		this.tac.getStartTimeField().setToolTipText(AVXALConstants.TIME_INPUT_TOOLTIP);
		this.tac.getEndTimeField().setToolTipText(AVXALConstants.TIME_INPUT_TOOLTIP);
		
		pm.addPlotModelListener(
				createTimeAxesModelListener());
		
		String[] timeAxesNames = pm.getTimeAxesNames();
		loadTimeAxesNamesIntoBox(timeAxesNames);
	}
	
	public String getSelectedAxisName()
	{
		return (String) this.tac.getTimeAxisLabelsBox().getSelectedItem();
	}
	
	public JButton getMoreButton()
	{
		return this.tac.getMoreButton();
	}
}
