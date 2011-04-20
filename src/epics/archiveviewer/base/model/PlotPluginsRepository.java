/*
 * Created on 20.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.model.listeners.PlotPluginsListener;
import epics.archiveviewer.base.util.AVBaseUtilities;
import gov.sns.tools.messaging.MessageCenter;

import java.util.ArrayList;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotPluginsRepository
{
	private final ArrayList plotPlugins;
	private final MessageCenter messageCenter;
	private final PlotPluginsListener ppListenerProxy;
	
	private PlotPlugin selectedPlotPlugin;
	
	public PlotPluginsRepository()
	{
	    this.plotPlugins = new ArrayList();
	    this.messageCenter = MessageCenter.newCenter();
	    this.ppListenerProxy = 
	    	(PlotPluginsListener) this.messageCenter.registerSource(this, PlotPluginsListener.class);
	}
	
	public String[] getAvailablePlotPluginClassNames()
	{
		return AVBaseConstants.AVAILABLE_PLOT_PLUGINS_CLASS_NAMES;
	}
	
	//allows duplicated
	public void addPlotPlugin(PlotPlugin pp)
	{
	    this.plotPlugins.add(pp);
	    //if it's first plugin
	    if(this.plotPlugins.size() == 1)
	    	setSelectedPlotPlugin(pp);
	    this.ppListenerProxy.plotPluginAdded(pp);
	}
	
	public boolean removePlotPlugin(PlotPlugin pp)
	{
	    boolean b = this.plotPlugins.remove(pp); 
	    this.ppListenerProxy.plotPluginRemoved(pp);
	    return b;
	}
	
	public int size()
	{
		return this.plotPlugins.size();
	}
	
	public PlotPlugin getPlotPlugin(int index) throws Exception
	{
		return (PlotPlugin) this.plotPlugins.get(index);
	}
	
	public void setSelectedPlotPlugin(PlotPlugin pp)
	{
	    this.selectedPlotPlugin = pp;
	}
	
	public PlotPlugin getSelectedPlotPlugin()
	{
	    return this.selectedPlotPlugin;
	}
	
	public void addPlotPluginsListener(PlotPluginsListener ppl)
	{
		this.messageCenter.registerTarget(ppl, this, PlotPluginsListener.class);
	}
	
	public void removePlotPluginsListener(PlotPluginsListener ppl)
	{
		this.messageCenter.removeTarget(ppl, this, PlotPluginsListener.class);
	}
}
