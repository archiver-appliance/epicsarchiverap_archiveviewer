/*
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.swingmodels;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.model.ArchiveDirectoriesRepository;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.xal.view.tables.AVEsTable;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEsTableModel extends AbstractTableModel
{
	private final AVBase avBase;
	private final PlotModel plotModel;
	private final ArchiveDirectoriesRepository adsRepository;
	
	public AVEsTableModel(JTable t, AVBase avb, ArchiveDirectoriesRepository adr)
	{
		this.avBase = avb;
		this.plotModel = this.avBase.getPlotModel();
		this.adsRepository = adr;
		addTableModelListener(t);
	}

	public String getColumnName(int column) {
		return null;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	public int getColumnCount() {
		return AVEsTable.NR_OF_COLUMNS;
	}

	public int getRowCount() {
		return this.plotModel.getNrOfAVEs();
	}


	public Object getValueAt(int rowIndex, int columnIndex) {
		try
		{
			AVEntry ave = this.plotModel.getAVEntry(rowIndex);
			switch(columnIndex)
			{
			case AVEsTable.AVE_NAME_COLUMN_INDEX:
				return ave.getName();
			
			case AVEsTable.MORE_BUTTON_COLUMN_INDEX:
				return null;
			}
		}
		catch(Exception e)
		{
			// do nothing
		}
		return null;
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		try
		{
			
			if(	columnIndex == AVEsTable.AVE_NAME_COLUMN_INDEX &&
				getValueAt(rowIndex, columnIndex).equals((String)aValue) == false
			)
			{				
				Graph g = getGraph(rowIndex);
				if(g instanceof PVGraph)
				{
					this.plotModel.changePVName(
							g.getAVEntry(),
							aValue.toString()
					);
				}
				else
				{
					this.plotModel.changeFormulaName(
							g.getAVEntry(),
							aValue.toString()
					);
				}
			}
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
		catch(Exception e)
		{
			this.avBase.displayError(
					"Can't set value " + aValue + 
					" at row" + rowIndex + " and column " +  columnIndex, e);
		}
	}
	
	public Graph getGraph(int rowIndex) throws Exception
	{

		AVEntry ave = this.plotModel.getAVEntry(rowIndex);
		return this.plotModel.getGraph(ave);
	}
}
