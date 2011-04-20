/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.xal.controller.listeners.AVEColorButtonListener;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.components.AVColorButton;
import epics.archiveviewer.xal.view.components.AVDialog;
import epics.archiveviewer.xal.view.preferences.LegendConfiguratorPanel;
import epics.archiveviewer.xal.view.preferences.PreferencesPanel;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PreferencesPanelController
{
	public static final String PLOT_PLUGINS_CATEGORY = "Plot Plugins";
	public static final String PLOT_LEGEND_CATEGORY = "Plot Legend";
	public static final String OTHER_PLOT_SETTINGS_CATEGORY = "Other Plot Settings";
	
	private final PreferencesPanel pp;
	private final AVController avController;
	
	private String getSelectedPlotPluginClassName()
	{
		return pp.getPlotPluginsLoaderPanel().getPlotPluginsBox().getSelectedItem().toString();
	}
	
	private ActionListener createShowPlotPluginInfoListener()
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				String className = getSelectedPlotPluginClassName();
				try
				{
					pp.getPlotPluginsLoaderPanel().getDescriptionArea().setText(
							Class.forName(className).getField("DESCRIPTION").get(null).toString());
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Couldn't load description of this plot plugin", ex);
				}
			}
			
		};
	}
    
    private ActionListener createLoadPlotPluginListener()
    {
    	return new ActionListener()
    	{

			public void actionPerformed(ActionEvent e) {
				String className = getSelectedPlotPluginClassName();
				
				try
				{
					PlotPlugin pp =
						AVBaseUtilities.loadPlotPlugin(
							avController.getAVBase(),
							avController.getMainWindow(),
							className,
							null);
					AVXALUtilities.setupPlotPlugin(avController, pp);
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't load plot plugin " + className, ex);
				}
					
				
			}
    		
    	};
    }
    
    private ActionListener createCategoriesListener()
    {
        return new ActionListener()
        {
        	public void actionPerformed(ActionEvent e)
        	{
        		pp.getContentPanel().removeAll();
        		
        		Object selectedItem = pp.getCategoriesBox().getSelectedItem();
        		
        		JPanel contentPanel = null;
        		if(selectedItem == PLOT_PLUGINS_CATEGORY)
        		{
        			contentPanel = pp.getPlotPluginsLoaderPanel();
        		}
        		else if(selectedItem == PLOT_LEGEND_CATEGORY)
        		{
        			contentPanel = pp.getLegendConfiguratorPanel();
        		}
        		else
        		{
        			contentPanel = pp.getOtherPlotSettingsPanel();
        		}
        		
        		pp.getContentPanel().add(contentPanel);
        		pp.validate();
        		pp.repaint();
        	}
        };
    }
    
    private ActionListener createCommitListener()
    {
    	return new ActionListener()
    	{

			public void actionPerformed(ActionEvent e) {
				//commit plot title, plot background, and new legend settings
				
				PlotModel pm = avController.getAVBase().getPlotModel();
		    	pm.setPlotTitle(pp.getOtherPlotSettingsPanel().getPlotTitleField().getText());
		    	pm.setPlotBGColor(pp.getOtherPlotSettingsPanel().getPlotBGColorButton().getBackground());
		    	
		    	LegendConfiguratorPanel lcp = pp.getLegendConfiguratorPanel();
		    	LegendInfo li = new LegendInfo(
		    			lcp.getAVENameBox().isSelected(),
		    			lcp.getArchiveBox().isSelected(),
		    			lcp.getRangeBox().isSelected(),
		    			lcp.getUnitsBox().isSelected()
		    			);
		    	pm.setLegendInfo(li);
			}
    		
    	};
    }
        
    public PreferencesPanelController(final AVController avc) throws Exception
    {
    	this.avController = avc;
        this.pp = new PreferencesPanel();
        
        PlotModel pm = avController.getAVBase().getPlotModel();
        LegendInfo li = pm.getLegendInfo();
        
        pp.getOtherPlotSettingsPanel().getPlotTitleField().setText(pm.getPlotTitle());
        
        AVColorButton plotBGColorButton = pp.getOtherPlotSettingsPanel().getPlotBGColorButton();
        plotBGColorButton.setColor(pm.getPlotBGColor());
        plotBGColorButton.addActionListener(new AVEColorButtonListener());
        
        pp.getLegendConfiguratorPanel().getAVENameBox().setSelected(li.getShowAVEName());
        pp.getLegendConfiguratorPanel().getArchiveBox().setSelected(li.getShowArchiveName());
        pp.getLegendConfiguratorPanel().getRangeBox().setSelected(li.getShowRange());
        pp.getLegendConfiguratorPanel().getUnitsBox().setSelected(li.getShowUnits());
        
        String[] avaiblePluginsClasses = 
            avController.getAVBase().getPlotPluginsRepository().getAvailablePlotPluginClassNames();
   
        JComboBox plotPluginsBox = pp.getPlotPluginsLoaderPanel().getPlotPluginsBox();
        
        for(int i=0; i<avaiblePluginsClasses.length; i++)
        {
            plotPluginsBox.addItem(avaiblePluginsClasses[i]);
        }
        
        plotPluginsBox.addActionListener(createShowPlotPluginInfoListener());
        
        plotPluginsBox.setSelectedIndex(0);
       
        pp.getPlotPluginsLoaderPanel().getLoadButton().addActionListener(
        		createLoadPlotPluginListener()
        	);
        
        this.pp.getCategoriesBox().addItem(PLOT_PLUGINS_CATEGORY);
        this.pp.getCategoriesBox().addItem(PLOT_LEGEND_CATEGORY);
        this.pp.getCategoriesBox().addItem(OTHER_PLOT_SETTINGS_CATEGORY);
        
        this.pp.getCategoriesBox().addActionListener(createCategoriesListener());
        this.pp.getCategoriesBox().setSelectedIndex(0);
        
        AVBaseUtilities.setWindowToMinimalSize(
        	new AVDialog(
                pp,
                avController.getMainWindow(),
                "Preferences",
                false,
                true,
                avController.getMainAVPanel().getAVEsPanel(),
                createCommitListener(), FlowLayout.CENTER
                ),
                640,
                320);        
    }
}
