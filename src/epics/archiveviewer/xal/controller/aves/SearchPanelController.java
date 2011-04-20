/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JList;

import epics.archiveviewer.base.model.MatchingAVEsRepository;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.view.SearchPanel;
import epics.archiveviewer.xal.view.components.AVDialog;
import epics.archiveviewer.xal.view.tables.MatchingAVEsTable;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPanelController
{
    private final AVController avController;
    private final SearchPanel searchPanel;
    private final MatchesTableController matchesTableController;
    private final JDialog searchDialog;
    
    private ActionListener createSearchListener()
    {
        return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				searchPanel.getGoButton().setEnabled(false);
				avController.search(searchPanel);
			}			 
		};
    }
    
    private ActionListener createAddSelectedPVsListener(final MatchingAVEsTable matchesTable)
    {
        return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				AVBaseUtilities.addMatchingAVEsWithSpecifiedIndicesToPlotModel(
									avController.getAVBase(),
									matchesTable.getSelectedRows()
								);
				avController.getAVBase().getPlotModel().fireAVEsUpdated();
				
			}
		};
    }
    
    private ActionListener createClearMatchesListener(final MatchingAVEsRepository matchingAVEsRepository)
    {
        return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				matchingAVEsRepository.clear();
			}
		};
    }
    
    public SearchPanelController(AVController avc)
    {
        this.avController = avc;
        this.searchPanel = new SearchPanel();

        
        ActionListener searchListener = createSearchListener();
        
        this.searchPanel.getInputField().addActionListener(searchListener);
		this.searchPanel.getGoButton().addActionListener(searchListener);
		this.searchPanel.getAddButton().addActionListener(
		        createAddSelectedPVsListener(this.searchPanel.getMatchingAVEsTable()));
		this.searchPanel.getClearButton().addActionListener(
		        createClearMatchesListener(this.avController.getAVBase().getMatchingAVEsRepository()));
		
		this.matchesTableController = new MatchesTableController(
		        this.avController, this.searchPanel);
		
        this.searchDialog = 
        	new AVDialog(
                this.searchPanel,
                this.avController.getMainWindow(),
                "Search Dialog",
                false,
                true,
                this.avController.getMainAVPanel().getPlotPluginsWrapperPane(),
                null, FlowLayout.CENTER
                );
    }
    
    public void showDialog(boolean doSearch)
    {
    	JList archivesList = this.searchPanel.getArchivesList();
		archivesList.setListData(
				this.avController.getAVBase().getArchiveDirectoriesRepository().getSortedArchiveDirectoryNames());
		
		//scroll to the position of and pre-select the archive directroy that is selected on the main panel
		int indexOfSelectedADOnMainPanel = 
			this.avController.getMainAVPanel().getAVEsPanel().
				getAVEsSelectorPanel().getArchiveDirectoriesSelectionBox().getSelectedIndex();
		if(indexOfSelectedADOnMainPanel > -1)
		{
			this.searchPanel.getArchivesListScrollPane().getViewport().scrollRectToVisible(
					archivesList.getCellBounds(indexOfSelectedADOnMainPanel, indexOfSelectedADOnMainPanel));
			archivesList.setSelectedIndex(indexOfSelectedADOnMainPanel);
		}
		
		
		//reset "select archives" box
		if(this.searchPanel.getSelectArchivesBox().isSelected())
		{
			this.searchPanel.getSelectArchivesBox().doClick();
		}
		
    	this.searchDialog.setLocation(avController.getMainAVPanel().getPlotPluginsWrapperPane().getLocationOnScreen());
        this.searchDialog.setVisible(true);
        if(doSearch)
        {
			String temp = this.avController.getMainAVPanel().getAVEsPanel().getAVEsSelectorPanel().getInputField().getText();
			
			if(temp != null && temp.equals("") == false)
			{
				this.searchPanel.getInputField().setText(temp);
				this.searchPanel.getGoButton().setEnabled(true);
			}
        }
    }
}
