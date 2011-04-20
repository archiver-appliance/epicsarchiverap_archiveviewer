/*
 * Created on Feb 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.UseCases;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.base.util.AVProgressTask;
import epics.archiveviewer.xal.controller.aves.AVEsSelectorController;
import epics.archiveviewer.xal.controller.aves.AVEsTableController;
import epics.archiveviewer.xal.controller.aves.SearchPanelController;
import epics.archiveviewer.xal.controller.axes.AxesSettingsPanelController;
import epics.archiveviewer.xal.controller.axes.RangeAxesConfiguratorController;
import epics.archiveviewer.xal.controller.axes.TimeAxesConfiguratorController;
import epics.archiveviewer.xal.controller.listeners.StatusPanelProgressListener;
import epics.archiveviewer.xal.controller.util.SwingWorkersRepository;
import epics.archiveviewer.xal.view.AVWindow;
import epics.archiveviewer.xal.view.MainAVPanel;
import epics.archiveviewer.xal.view.SearchPanel;
import epics.archiveviewer.xal.view.tooltip.MultiLineToolTipUI;
import gov.sns.application.XalDocument;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import javax.swing.UIManager;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVController extends XalDocument
{
	static
	{
		UIManager.put(MultiLineToolTipUI.UI_CLASS_ID, MultiLineToolTipUI.CLASS_NAME);
	}
	
	//model
	private final AVBase avBase;
	//view
	private final MainAVPanel mainAVPanel;
	
	private SearchPanelController searchPanelController;
		
	private AVProgressTask initProgress()
	{
		AVProgressTask avp = new AVProgressTask();
		StatusPanelProgressListener spProgressListener = 
			new StatusPanelProgressListener(
					this, 
					this.mainAVPanel.getStatusPanel(),
					avp);
		this.avBase.addProgressListener(spProgressListener);
		try
		{
			this.avBase.startProgressTimer();
		}
		catch(Exception e)
		{
			//do nothing
		}
		return avp;
	}

	public AVController() throws Exception
	{
		this.avBase = new AVBase();
		this.avBase.getPlotModel().setLegendInfo(
				new LegendInfo(true, false, true, true));
		this.mainAVPanel = new MainAVPanel();
	}
	
	public AVBase getAVBase()
	{
		return this.avBase;
	}
	
	public MainAVPanel getMainAVPanel()
	{
		return this.mainAVPanel;
	}
	
	 /**
     * Make a main window by instantiating the my custom window.
     */
    public void makeMainWindow() {
        super.mainWindow = new AVWindow(this, this.mainAVPanel);
   }

    /**
     * Save the document to the specified URL.
     * @param url The URL to which the document should be saved.
     */
    public void saveDocumentAs(URL url) {
    	try
    	{
			TimeAxesConfiguratorController.storeVisibleTimeAxisParametersInPlotModel(this);
			RangeAxesConfiguratorController.storeVisibleRangeAxisParametersInPlotModel(this);
	    	this.avBase.getPlotModel().serialize(new FileWriter(url.getFile()));
    	}
    	catch(Exception e)
    	{
    		this.avBase.displayError("Can't save current configuration", e);
    	}
    }
    
	public boolean hasChanges() {
		return true;
	}
	
	public void connect(String connectionParameter, boolean newThread) throws Exception
	{
	    this.avBase.getPlotModel().setConnectionParameter(connectionParameter);
	    if(newThread)
		    SwingWorkersRepository.startConnectWorker(
		    		this.avBase, initProgress());
	    else
	    	UseCases.connect(avBase, avBase.getPlotModel().getConnectionParameter(), null);
	}
	
	public void loadConfiguration(String path, boolean newThread) throws Exception
	{
		File f = new File(path);
		if(newThread)
			SwingWorkersRepository.startLoadFileWorker(this.avBase, f, initProgress());
		else
			UseCases.loadConfiguration(avBase, f, null);
	}
	
	public void reconnect()
	{
		SwingWorkersRepository.startReconnectWorker(this.avBase, initProgress());
	}
	
	public void search(SearchPanel sp)
	{
		try
		{
			//determine the pattern
			String temp = null;
			if(sp.getGlobButton().isSelected())
			{
				temp = 
					AVBaseUtilities.convertGlobToRegular(
						sp.getInputField().getText(),
						sp.getCaseSensitiveBox().isSelected());
			}
			else
				temp = sp.getInputField().getText();
			final String regularExpression = temp;
			
			String[] adNames = null;
			if(sp.getSelectArchivesBox().isSelected())
			{
				Object[] selectedArchiveNames = sp.getArchivesList().getSelectedValues();
				adNames = new String[selectedArchiveNames.length];
				for(int i=0; i<adNames.length; i++)
				{
					adNames[i] = (String) selectedArchiveNames[i];
				}
			}
			else
			{
				adNames = new String[]
				                     {
										getMainAVPanel().
											getAVEsPanel().
												getAVEsSelectorPanel().
													getArchiveDirectoriesSelectionBox().
														getSelectedItem().
															toString()
												};
			}
		
			SwingWorkersRepository.startSearchWorker(
					getAVBase(), adNames, regularExpression, initProgress());
		}
		catch(Exception ex)
		{
			avBase.displayError("Error executing search", ex);
		}
	}
	
	public void plot()
	{
		SwingWorkersRepository.startPlotWorker(getAVBase(), initProgress());
	}
	
	public AVProgressTask export(String exporterId)
	{
		AVProgressTask avp = initProgress();
		SwingWorkersRepository.startExportWorker(
				getAVBase(), 
				exporterId,
				true,
				avp);
		return avp;
	}
	
	public void assignSelectedArchiveToSelectedAVEs()
	{
		try
		{
			String selectedADName = 
				(String)
				this.mainAVPanel.
					getAVEsPanel().
						getAVEsSelectorPanel().
							getArchiveDirectoriesSelectionBox().
								getSelectedItem();
			
			int[] selectedAVETableRows = 
				this.mainAVPanel.
					getAVEsPanel().
						getAVEsTable().
							getSelectedRows();
			
			AVEntry[] allAVEs = this.avBase.getPlotModel().getAVEntries();
			AVEntry[] selectedAVEs = new AVEntry[selectedAVETableRows.length];
			int selectedRowIndex = -1;
			for(int i=0; i<selectedAVEs.length; i++)
			{
				selectedRowIndex = selectedAVETableRows[i];
				selectedAVEs[i] = allAVEs[selectedRowIndex];
			}
			AVBaseUtilities.assignArchiveToAVEs(this.avBase, selectedAVEs, selectedADName);
		}
		catch(Exception e)
		{
			this.avBase.displayError("Can't assign selected archive to selected AV entries", e);
		}
	}
	
	public void resizeToFullScreen()
	{
		try {
			mainWindow.setLocation(0, 0);
			mainWindow.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		} catch (Exception e) {
			this.avBase.displayError("Can't make this window full screen", e);
		}
	}
	
	public void selectArchiveDirectory(String ad)
	{
		this.mainAVPanel.getAVEsPanel().getAVEsSelectorPanel().
			getArchiveDirectoriesSelectionBox().setSelectedItem(ad);
	}
	
	public void showSearchDialog(boolean doSearch)
	{
	    if(this.searchPanelController == null)
	        this.searchPanelController = new SearchPanelController(this);
	    this.searchPanelController.showDialog(doSearch);
	}
	
	public void initSpecificControllers()
	{
		new MessagesController(this);
		new AVEsTableController(this, this.mainAVPanel.getAVEsPanel().getAVEsTable());
		new AVEsSelectorController(this, this.mainAVPanel.getAVEsPanel().getAVEsSelectorPanel());
		new AxesSettingsPanelController(this, this.mainAVPanel.getAxesSettingsPanel());
		new PlotPluginWrappersTabbedPaneController(this, this.mainAVPanel.getPlotPluginsWrapperPane());
		new StatusPanelController(this, this.mainAVPanel.getStatusPanel());
	}
	
	public void clearAll()
	{
		PlotModel pm = this.avBase.getPlotModel();
		pm.clear();
		pm.loadInitialAxesSettings();
		this.avBase.getMatchingAVEsRepository().clear();
		this.avBase.getAxesIntervalsManager().clear();
		
		pm.fireNewModelLoaded();
		pm.fireAVEsUpdated();
		pm.fireTimeAxesUpdated();
		pm.fireRangeAxesUpdated();		
	}
}
