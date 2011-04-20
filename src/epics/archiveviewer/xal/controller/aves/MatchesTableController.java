/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import epics.archiveviewer.base.model.MatchingAVEsRepository;
import epics.archiveviewer.base.model.listeners.SearchMatchesListener;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.swingmodels.MatchesTableModel;
import epics.archiveviewer.xal.view.SearchPanel;
import epics.archiveviewer.xal.view.tables.MatchingAVEsTable;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MatchesTableController
{
    private final MatchingAVEsTable matchesTable;
    private final SearchMatchesListener searchMatchesListener;
    
    private MouseListener createMatchesTableSelectorListener(
            final AVController avController)
    {
        return new MouseAdapter()
		{
			private boolean rowAlreadyClicked = false;

			private int currentRowIndex = -1;

			public void mousePressed(MouseEvent e)
			{
				if (	this.currentRowIndex == matchesTable.rowAtPoint(e.getPoint()) && 
						this.rowAlreadyClicked)
				{
					AVBaseUtilities.addMatchingAVEsWithSpecifiedIndicesToPlotModel(
							avController.getAVBase(),
							new int[]{this.currentRowIndex}
							);
					avController.getAVBase().getPlotModel().fireAVEsUpdated();
					//reset
					this.currentRowIndex = -1;
					this.rowAlreadyClicked = false;
				}
				else
				{
					this.currentRowIndex = matchesTable.getSelectedRow();
					this.rowAlreadyClicked = true;
				}
			}
		};
    }
    
    private SearchMatchesListener createSearchMatchesListener(
            final AVController avController, final SearchPanel sp)
    {
    	return
    	new SearchMatchesListener()
			{
				public void matchesAdded() {
					sp.getGoButton().setEnabled(true);
					
				    MatchesTableModel matchesTM = (MatchesTableModel) matchesTable.getModel();
					matchesTM.fireTableDataChanged();
					matchesTable.doLayout();	
					matchesTable.scrollRectToVisible(
							matchesTable.getCellRect(
									matchesTM.getRowCount() - 1, 
									MatchingAVEsTable.PV_COLUMN_INDEX,
									false
									)
								);
				}

				public void matchesCleared() {
				    ((MatchesTableModel) matchesTable.getModel()).fireTableDataChanged();
					matchesTable.doLayout();
				}
			};
    }
    
    public MatchesTableController(AVController avController, SearchPanel sp)
    {
        this.matchesTable = sp.getMatchingAVEsTable();
    	MatchingAVEsRepository matchingAVEsRepository = avController.getAVBase().getMatchingAVEsRepository();
		
		this.matchesTable.setModel(
		        new MatchesTableModel(
					this.matchesTable,
					avController.getAVBase().getClient(),
					matchingAVEsRepository
		        )
		);
		
		this.searchMatchesListener = createSearchMatchesListener(avController, sp);
		
		this.matchesTable.addMouseListener(
		        createMatchesTableSelectorListener(avController));
		matchingAVEsRepository.addSearchMatchesListener(this.searchMatchesListener);
    }
    
    public SearchMatchesListener getSearchMatchesListener()
    {
        return this.searchMatchesListener;
    }
}
