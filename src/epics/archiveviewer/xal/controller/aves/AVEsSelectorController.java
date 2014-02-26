/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.model.ArchiveDirectoriesRepository;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.listeners.ArchiveDirectoriesListener;
import epics.archiveviewer.base.model.listeners.PlotModelAdapter;
import epics.archiveviewer.base.model.listeners.PlotModelListener;
import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.axes.TimeAxesConfiguratorController;
import epics.archiveviewer.xal.view.AVEsSelectorPanel;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEsSelectorController
{
	private final AVController avController;
	private final ArchiveDirectoriesRepository adsRepository;
	private final AVEsSelectorPanel asp;
	
	private ArchiveDirectory getSelectedArchiveDirectory()
	{
		return adsRepository.
				getArchiveDirectory(
						(String)asp.getArchiveDirectoriesSelectionBox().getSelectedItem());
	}
	
	private ArchiveDirectoriesListener createADListenerForADsBox() {
		return new ArchiveDirectoriesListener() {

			private void updateArchiveDirectoriess() {
				String[] adNames = adsRepository
						.getSortedArchiveDirectoryNames();
				JComboBox adsBox = asp.getArchiveDirectoriesSelectionBox();

				adsBox.removeAllItems();
				for (int i = 0; i < adNames.length; i++) {
					adsBox.addItem(adNames[i]);
				}

				adsBox.validate();

			}

			public void archiveDirectoriesRetrieved() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateArchiveDirectoriess();
					};
				});
			}
		};
	}

    private ActionListener createDensitySelectionListener()
    {
        return new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                String item = (String) asp.getDensitySelectionBox().getSelectedItem();
                avController.getAVBase().setSparsificationOperator(item);
                avController.getAVBase().getVCsCache().clear();
            }
        };
    }
    
    private ActionListener createDisplaySearchDialogListener()
    {
		return new ActionListener()
		{

            public void actionPerformed(ActionEvent arg0)
            {
                avController.showSearchDialog(true);
            }
		    
		};
    }
    
    private ActionListener createDisplayNewFormulaConfiguratorListener()
    {
        return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{				
				try
				{
					JComboBox adsBox = asp.getArchiveDirectoriesSelectionBox(); 
					
					ArchiveDirectory ad = getSelectedArchiveDirectory();
					
					TimeAxesConfiguratorController.storeVisibleTimeAxisParametersInPlotModel(avController);
					
					PlotModel pm = avController.getAVBase().getPlotModel();
					FormulaGraph fg = 
									pm.createNewFormulaGraph(
										ad, 
										avController.
											getAVBase().
												getClient().
													getRetrievalMethodsForCalculation()[0].
														getName());
					new FormulaConfiguratorPanelController(avController, fg);
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't display new formula configurator", ex);
				}
			}
		};
    }
    
    private ActionListener createAddButtonListener()
    {
    	return new ActionListener()
    	{

			public void actionPerformed(ActionEvent e) {
				JComboBox adsBox = asp.getArchiveDirectoriesSelectionBox(); 
				
				PlotModel pm = avController.getAVBase().getPlotModel();
				
				try
				{
					String pvname = asp.getInputField().getText();
					PVGraph pvg = pm.createNewPVGraph(
							new AVEntry(
								pvname.trim(),
								getSelectedArchiveDirectory()
								)
						);
					pm.addGraph(pvg);
					pm.fireAVEsUpdated();
					asp.getInputField().setText("");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
    		
    	};
    }
    
    private ActionListener createRemoveButtonListener()
    {
    	return new ActionListener()
    	{

			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = avController.getMainAVPanel().getAVEsPanel().getAVEsTable().getSelectedRows();
				PlotModel pm = avController.getAVBase().getPlotModel();
				try
				{
					AVEntry[] allAVEs = pm.getAVEntries();
					
					int selectedRowIndex = -1;
					for(int i=0; i<selectedRows.length; i++)
					{
						selectedRowIndex = selectedRows[i];
						pm.removeGraph(allAVEs[selectedRowIndex]);
					}
					
					pm.fireAVEsUpdated();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
    		
    	};
    }
    
    private ActionListener createClearButtonListener()
    {
    	return new ActionListener()
    	{

			public void actionPerformed(ActionEvent e) {
				asp.getInputField().setText("");
				avController.getMainAVPanel().getAVEsPanel().getAVEsTable().clearSelection();
			}
    		
    	};
    }
    
    private PlotModelListener createNewModelListener()
    {
    	return new PlotModelAdapter()
    	{

			public void newModelLoaded() {
				try
				{
					super.newModelLoaded();
					AVEntry[] aves = avController.getAVBase().getPlotModel().getAVEntries();
					if(aves !=null && aves.length > 0)
					{
						//select the archive directory of the first ave
						asp.getArchiveDirectoriesSelectionBox().setSelectedItem(aves[0].getArchiveDirectory().getName().trim());
					}
				}
				catch(Exception e)
				{
					avController.getAVBase().displayError("Can't determine archive directory of the first AV entry", e);
				}
				
			}
    		
    	};
    }
    
    public AVEsSelectorController(AVController avc, AVEsSelectorPanel _asp)
    {
    	this.avController = avc;
    	this.asp = _asp;
        this.adsRepository = this.avController.getAVBase().getArchiveDirectoriesRepository();
        
        this.avController.getAVBase().setSparsificationOperator(_asp.getDensitySelection());
        this.asp.getDensitySelectionBox().addActionListener(createDensitySelectionListener());
        
        ActionListener displaySearchDialogListener  = createDisplaySearchDialogListener();
        
        this.adsRepository.addArchiveDirectoriesListener(createADListenerForADsBox());
        this.asp.getInputField().addActionListener(displaySearchDialogListener);		
		this.asp.getSearchButton().addActionListener(displaySearchDialogListener);
		this.asp.getNewFormulaButton().addActionListener(
				createDisplayNewFormulaConfiguratorListener());
		this.asp.getAddButton().addActionListener(
				createAddButtonListener());
		this.asp.getRemoveButton().addActionListener(
				createRemoveButtonListener());
		this.asp.getClearButton().addActionListener(
				createClearButtonListener());
		
		this.asp.getInputField().setToolTipText(AVXALConstants.SEARCH_INPUT_TOOLTIP);
	
		this.avController.getAVBase().getPlotModel().addPlotModelListener(
				createNewModelListener());
		
    
    }
}
