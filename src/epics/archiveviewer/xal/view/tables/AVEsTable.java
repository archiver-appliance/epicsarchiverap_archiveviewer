/*
 * Created on Feb 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.tables;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;

import epics.archiveviewer.xal.view.tooltip.MultiLineToolTip;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEsTable extends JTable
{
	public final static int AVE_NAME_COLUMN_INDEX = 0;
	public final static int MORE_BUTTON_COLUMN_INDEX = 1;
	public final static int NR_OF_COLUMNS = 2; 
	public final static int BUTTON_WIDTH = 40;
	public final static int DEFAULT_NR_VISIBLE_ROWS = 5;
	
	public AVEsTable()
	{
		super(0, NR_OF_COLUMNS);
		this.setShowGrid(false);
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.setCellSelectionEnabled(true);
		
		for(int i=0; i<NR_OF_COLUMNS; i++)
		{
			getColumnModel().getColumn(i).setHeaderValue(null);
		}
		
		this.setPreferredScrollableViewportSize(
				new Dimension(
						(int) this.getPreferredSize().getWidth(),
						DEFAULT_NR_VISIBLE_ROWS * this.getRowHeight()
						)
		);
	}
	
	//see J2SDK's JavaDoc 
	public void changeSelection(int rowIndex,
            int columnIndex,
            boolean toggle,
            boolean extend)
	{
		if(	columnIndex == MORE_BUTTON_COLUMN_INDEX && 
			isCellSelected(rowIndex, AVE_NAME_COLUMN_INDEX))
		{
			toggle = true;
		}
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}
	
   public JToolTip createToolTip() 
   {
        JToolTip tip = new MultiLineToolTip(320, 240);
        tip.setComponent(this);
        return tip;
    }
}
