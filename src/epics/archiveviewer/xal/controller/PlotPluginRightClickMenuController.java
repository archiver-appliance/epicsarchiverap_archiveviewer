/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.model.AxesIntervalsManager;
import epics.archiveviewer.xal.controller.util.SwingWorkersRepository;
import epics.archiveviewer.xal.view.plotplugins.PlotPluginRightClickMenu;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotPluginRightClickMenuController {
	private final AVController avController;
	private final AVBase avBase;
	private final PlotPlugin plotPlugin;
	private final AxesIntervalsManager axesIntervalsManager;
	private final JMenuItem previousPlotItem;
	private final JMenuItem nextPlotItem;
	
	private PopupMenuListener createPopupMenuListener()
	{
		return new PopupMenuListener()
		{

			public void popupMenuCanceled(PopupMenuEvent e) {
				//do nothing
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				//do nothing
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				try
				{
					previousPlotItem.setEnabled(axesIntervalsManager.isGoingBackPossible(plotPlugin));
					nextPlotItem.setEnabled(axesIntervalsManager.isGoingForwardPossible(plotPlugin));
				}
				catch(Exception ex)
				{
					avBase.displayError(
							"Failed to determine axis ranges history", ex);
				}
			}
			
		};
	}
	
	private ActionListener createPlotGoBackListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					axesIntervalsManager.goBack(plotPlugin);					
				}
				catch(Exception ex)
				{
					avBase.displayError("Can't determine previous axis range", ex);
					return;
				}
				avController.plot();
			}	
		};
	}
	
	private ActionListener createPlotGoForwardListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					axesIntervalsManager.goForward(plotPlugin);					
				}
				catch(Exception ex)
				{
					avBase.displayError("Can't determine next axis range", ex);
					return;
				}
				avController.plot();
			}	
		};
	}


	public PlotPluginRightClickMenuController(AVController avc, PlotPlugin pp)
	{
		this.avController = avc;
		this.avBase = this.avController.getAVBase();
		this.plotPlugin = pp;
		this.axesIntervalsManager = this.avBase.getAxesIntervalsManager();
		PlotPluginRightClickMenu pprcm = new PlotPluginRightClickMenu(this.plotPlugin);
		
		this.previousPlotItem = pprcm.getPreviousPlotItem();
		this.nextPlotItem = pprcm.getNextPlotItem();
		
		this.previousPlotItem.setEnabled(false);
		this.nextPlotItem.setEnabled(false);
		
		pprcm.getMenu().addPopupMenuListener(
				createPopupMenuListener());
		this.previousPlotItem.addActionListener(
				createPlotGoBackListener());
		this.nextPlotItem.addActionListener(
				createPlotGoForwardListener());
				
	}
	
}
