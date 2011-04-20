/*
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.tables;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.base.fundamental.FormulaParameter;
import epics.archiveviewer.base.model.MatchingAVEsRepository;
import epics.archiveviewer.base.model.listeners.SearchMatchesListener;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MatchingAVEsTable extends JTable
{
	public static final String[] COLUMN_NAMES = 
	{
		"PV",
		"Directory",
		"Archiving Start Time",
		"Archiving End Time"
	};	
	public static final int PV_COLUMN_INDEX = 0;	
	public static final int DIR_COLUMN_INDEX = 1;
	public static final int START_COLUMN_INDEX = 2;	
	public static final int END_COLUMN_INDEX = 3;	
	public static final int COLUMN_WIDTH_UNIT = 70;
	public static final int DEFAULT_NR_VISIBLE_ROWS = 16;
	
	public MatchingAVEsTable()
	{
		super(new Object[][]{}, COLUMN_NAMES);
		
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setRowSelectionAllowed(true);
		this.setColumnSelectionAllowed(false);
		this.setShowHorizontalLines(true);
		this.setShowVerticalLines(false);
		
		this.setPreferredScrollableViewportSize(
				new Dimension(
						this.getPreferredSize().width,
						DEFAULT_NR_VISIBLE_ROWS * this.getRowHeight()
						)
		);
	}
	
	public void setModel(TableModel tm)
	{
		super.setModel(tm);
		TableColumnModel columnModel = this.getColumnModel();

		columnModel.getColumn(PV_COLUMN_INDEX).setMinWidth((int)(COLUMN_WIDTH_UNIT * 2.5));
		columnModel.getColumn(DIR_COLUMN_INDEX).setMinWidth((int)(COLUMN_WIDTH_UNIT * 2.5));
		columnModel.getColumn(START_COLUMN_INDEX).setMinWidth((int)(COLUMN_WIDTH_UNIT * 2));
		columnModel.getColumn(END_COLUMN_INDEX).setMinWidth((int)(COLUMN_WIDTH_UNIT * 2));
	}
}
