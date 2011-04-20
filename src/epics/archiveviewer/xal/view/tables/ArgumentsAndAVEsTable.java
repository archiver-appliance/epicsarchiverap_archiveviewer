/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.tables;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import epics.archiveviewer.base.fundamental.FormulaParameter;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArgumentsAndAVEsTable extends JTable
{
	public static final Object[] COLUMN_NAMES = 
									{
										"AV Entry",
										"Arg",
										""
									};	
	public static final int AVE_COLUMN_INDEX = 0;
	public static final int ARG_COLUMN_INDEX = 1;	
	public static final int BUTTON_COLUMN_INDEX = 2;	
	public static final int DEFAULT_NR_VISIBLE_ROWS = 007;
	public static final int COLUMN_WIDTH_UNIT = 45; 
	public static final int PREFERRED_WIDTH = COLUMN_WIDTH_UNIT * 8;

	public ArgumentsAndAVEsTable()
	{
		super(0, COLUMN_NAMES.length);
		
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setCellSelectionEnabled(true);
		this.setShowGrid(false);
		
		this.setPreferredScrollableViewportSize(
				new Dimension(
						PREFERRED_WIDTH,
						DEFAULT_NR_VISIBLE_ROWS * this.getRowHeight()
						)
		);
	}
}
