/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.plotplugins;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import epics.archiveviewer.PlotPlugin;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotPluginRightClickMenu {
	private final JPopupMenu menu;
	private final JMenuItem previousPlotItem;
	private final JMenuItem nextPlotItem;
	
	public PlotPluginRightClickMenu(PlotPlugin pp)
	{
		this.menu = pp.getRightClickMenu();
		this.previousPlotItem = new JMenuItem("Previous plot");
		this.nextPlotItem = new JMenuItem("Next plot");
		
		this.menu.addSeparator();
		this.menu.add(this.previousPlotItem);
		this.menu.add(this.nextPlotItem);
	}
	
	public JPopupMenu getMenu()
	{
		return this.menu;
	}
	
	public JMenuItem getPreviousPlotItem()
	{
		return this.previousPlotItem;
	}
	
	public JMenuItem getNextPlotItem()
	{
		return this.nextPlotItem;
	}
}
