/*
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.swingmodels;

import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.AVEntryInfo;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.model.MatchingAVEsRepository;
import epics.archiveviewer.xal.view.tables.MatchingAVEsTable;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MatchesTableModel extends AbstractTableModel
{
	private final ClientPlugin client;
	private final MatchingAVEsRepository matchingAVEsRepository;
	
	public MatchesTableModel(JTable t, ClientPlugin c, MatchingAVEsRepository mar)
	{
		this.client = c;
		this.matchingAVEsRepository = mar;
		addTableModelListener(t);
	}

	public String getColumnName(int column) {
		return MatchingAVEsTable.COLUMN_NAMES[column];
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	public int getColumnCount() {
		return MatchingAVEsTable.COLUMN_NAMES.length;
	}

	public int getRowCount() {
		if(this.matchingAVEsRepository == null)
			return 0;
		return this.matchingAVEsRepository.getNrOfMatchingAVEs();
	}


	public Object getValueAt(int rowIndex, int columnIndex) {
		try
		{
			AVEntry ave = this.matchingAVEsRepository.getMatchingAVE(rowIndex);
			AVEntryInfo aveInfo = null;
			switch(columnIndex)
			{
			case MatchingAVEsTable.PV_COLUMN_INDEX:
				return ave.getName();
			
			case MatchingAVEsTable.DIR_COLUMN_INDEX:
				return ave.getArchiveDirectory().getName();
				
			case MatchingAVEsTable.START_COLUMN_INDEX:
				aveInfo = this.client.getAVEInfo(ave);
				return AVBaseConstants.MAIN_DATE_FORMAT.format(new Date((long)aveInfo.getArchivingStartTime()));
				
			case MatchingAVEsTable.END_COLUMN_INDEX:
				aveInfo = this.client.getAVEInfo(ave);
				return AVBaseConstants.MAIN_DATE_FORMAT.format(new Date((long)aveInfo.getArchivingEndTime()));
			}
			
		}
		catch(Exception e)
		{
			//do nothing
		}
		return null;
	}
}
