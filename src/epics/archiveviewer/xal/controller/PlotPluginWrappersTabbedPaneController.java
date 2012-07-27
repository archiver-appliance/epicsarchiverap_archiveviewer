/*
 * Created on Feb 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.model.PlotPluginsRepository;
import epics.archiveviewer.base.model.listeners.PlotPluginsListener;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.plotplugins.JFreeChartForTimePlots;
import epics.archiveviewer.plotplugins.JFreeChartCorrelator;
import epics.archiveviewer.plotplugins.JFreeChartForWaveforms;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.components.UndockablePanel;
import epics.archiveviewer.xal.view.plotplugins.PlotPluginWrapperPanel;
import epics.archiveviewer.xal.view.plotplugins.PlotPluginWrappersTabbedPane;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotPluginWrappersTabbedPaneController {
	
    private final HashMap undockablePanelToPlotPlugin;
	private final PlotPluginsRepository ppsRepository;
	private final PlotPluginWrappersTabbedPane ppwTabbedPane;
	
	private void selectVisiblePluginInTabbedPane()
	{
	    PlotPlugin selectedPP =
		    (PlotPlugin)
		    undockablePanelToPlotPlugin.get(
		            ppwTabbedPane.getSelectedComponent());
		        
		ppsRepository.setSelectedPlotPlugin(selectedPP);
	}
	
	private void saveLoadedPlotPluginsClassNamesToPreferences(
			)
	{
		try
		{
			String[] values = new String[this.ppwTabbedPane.getTabCount()];
			for(int i=0; i<values.length; i++)
			{
				values[i] = this.ppsRepository.getPlotPlugin(i).getClass().getName();
			}
			AVBaseConstants.AV_PREFERENCES.put(
						AVBaseConstants.PLOT_PLUGINS_TO_LOAD_PREFS_KEY, 
						AVBaseUtilities.assemble(values, AVBaseConstants.PREFERENCES_VALUES_DELIM)
					);
		}
		catch(Exception e)
		{
			//do nothing
		}
	}
	
	private WindowListener createUndockedPlotPluginListener(
			final PlotPluginsRepository ppsRepository, 
			final PlotPlugin pp)
	{
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e) {
				ppsRepository.removePlotPlugin(pp);
			}
		};
	}
	
	private ChangeListener createPWPTabbedPaneListener()
	{
		return new ChangeListener()
		{

			public void stateChanged(ChangeEvent e) {
				selectVisiblePluginInTabbedPane();
			}
			
		};
	}
	
	private WindowFocusListener createUndockedPlotPluginSelectionListener(final PlotPlugin plotPlugin)
	{
	    return new WindowFocusListener()
	    {

            public void windowGainedFocus(WindowEvent arg0)
            {
                ppsRepository.setSelectedPlotPlugin(plotPlugin);                
            }

            public void windowLostFocus(WindowEvent arg0)
            {
               selectVisiblePluginInTabbedPane();
            }
	        
	    };
	}
	
	private PlotPluginsListener createPlotPluginsListener(
			final AVController avController)
	{
		return new PlotPluginsListener()
		{
		    private HashMap plotPluginsToUndockablePanels = new HashMap();
			public void plotPluginAdded(final PlotPlugin pp) {

				final PlotPluginWrapperPanel pwpPanel = new PlotPluginWrapperPanel(pp.getComponent());
				
				new PlotManipulationButtonsController(
						avController, 
						pwpPanel.getPlotManipulationButtonsPanel(),
						pp);
				
				
				UndockablePanel up =  new UndockablePanel(pwpPanel, avController.getMainWindow())
				{

					public void addToContainer() {
						ppwTabbedPane.add(pp.getName(), this);
					}

					public void removeFromContainer() {
						ppwTabbedPane.remove(this);
					}
					
				};
				this.plotPluginsToUndockablePanels.put(pp, up);
				undockablePanelToPlotPlugin.put(up, pp);
				
				up.addToContainer();
				up.getWrapperDialog().addWindowFocusListener(
				        createUndockedPlotPluginSelectionListener(pp));
				up.getWrapperDialog().addWindowListener(
						createUndockedPlotPluginListener(ppsRepository, pp));
				
				saveLoadedPlotPluginsClassNamesToPreferences();
			}

			public void plotPluginRemoved(PlotPlugin pp) {
				saveLoadedPlotPluginsClassNamesToPreferences();
				undockablePanelToPlotPlugin.remove(this.plotPluginsToUndockablePanels.get(pp));
			}			
		};
	}

	public PlotPluginWrappersTabbedPaneController(AVController avController, PlotPluginWrappersTabbedPane ppwtp)
	{
	    this.undockablePanelToPlotPlugin = new HashMap();
		this.ppwTabbedPane = ppwtp;		
		this.ppsRepository = avController.getAVBase().getPlotPluginsRepository();
		
		this.ppsRepository.addPlotPluginsListener(createPlotPluginsListener(avController));

                /***DISABLING RETRIEVAL FROM USER PREFERENCES AND LOADING ALL 3 PLOT PLUGINS
		String[] ppClassNamesToLoad = 
			AVBaseUtilities.tokenize(
					AVBaseConstants.AV_PREFERENCES.get(AVBaseConstants.PLOT_PLUGINS_TO_LOAD_PREFS_KEY, ""),
					AVBaseConstants.PREFERENCES_VALUES_DELIM
					);
                */
                String[] ppClassNamesToLoad = {JFreeChartForTimePlots.class.getName(),
                                               JFreeChartCorrelator.class.getName(),
                                               JFreeChartForWaveforms.class.getName()};


		//if no plot plugins are to be loaded, load the first of the available ones anyway
		if(ppClassNamesToLoad == null || ppClassNamesToLoad.length == 0)
		{
			String[] availablePlotPlugins = 
				this.ppsRepository.getAvailablePlotPluginClassNames();
			if(availablePlotPlugins != null && availablePlotPlugins.length > 0)
			{
				ppClassNamesToLoad = new String[] {JFreeChartForTimePlots.class.getName()};
			}
		}
		if(ppClassNamesToLoad != null)
		{
			for(int i=0; i<ppClassNamesToLoad.length; i++)
			{
				try
				{
					PlotPlugin pp = 
						AVBaseUtilities.loadPlotPlugin(
							avController.getAVBase(), 
							avController.getMainWindow(), 
							ppClassNamesToLoad[i],
							null);
					AVXALUtilities.setupPlotPlugin(avController, pp);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					avController.getAVBase().displayError("Can't load plot plugin " + ppClassNamesToLoad[i], ex);
				}
			}
		}
		this.ppwTabbedPane.addChangeListener(
				createPWPTabbedPaneListener());
	}
}
