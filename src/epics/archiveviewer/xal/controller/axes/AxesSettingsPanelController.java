/*
 * Created on Feb 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.axes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.view.axesconfigurators.AxesSettingsPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AxesSettingsPanelController {
	private final AVController avController;
	private final AxesSettingsPanel axesSettingsPanel;
	private final TimeAxesConfiguratorController tacController;
	private final RangeAxesConfiguratorController racController;
	
	private ActionListener createPlotButtonListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				if(axesSettingsPanel.getKeepAxesRangesBox().isSelected() == false)
				{
					TimeAxesConfiguratorController.storeVisibleTimeAxisParametersInPlotModel(avController);
					RangeAxesConfiguratorController.storeVisibleRangeAxisParametersInPlotModel(avController);
					
					AVBase avBase = avController.getAVBase();
					avBase.getAxesIntervalsManager().clear();
					
					try
					{
						AVBaseUtilities.copyAxesRangesFromPlotModelIntoAxesManager(avBase);
						
					}
					catch(Exception ex)
					{
						avBase.displayError("Can't process axes ranges", ex);
						return;
					}
				}
				
				avController.plot();
				
			}			
		};
	}
	
	public AxesSettingsPanelController(AVController avc, AxesSettingsPanel asp)
	{
		this.avController = avc;
		this.axesSettingsPanel = asp;
		this.tacController = new TimeAxesConfiguratorController(avController, asp.getTimeAxesConfigurator());
		this.racController = new RangeAxesConfiguratorController(avController, asp.getRangeAxesConfigurator());
		
		this.axesSettingsPanel.getPlotButton().addActionListener(
				createPlotButtonListener());
	}
}
