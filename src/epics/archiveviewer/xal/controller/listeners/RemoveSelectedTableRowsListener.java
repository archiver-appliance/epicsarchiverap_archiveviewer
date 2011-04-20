/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveSelectedTableRowsListener implements ActionListener
{
	private final JTable table;
	
	public RemoveSelectedTableRowsListener(JTable t)
	{
		this.table = t;
	}
	
	public void actionPerformed(ActionEvent e) {
		//sorted asc
		int[] selectedRowIndices = this.table.getSelectedRows();
		DefaultTableModel tableModel = (DefaultTableModel) this.table.getModel();
		//backwards
		
		for(int i=selectedRowIndices.length - 1; i > -1; i--)
		{
			tableModel.removeRow(selectedRowIndices[i]);
		}
		tableModel.fireTableRowsDeleted(selectedRowIndices[0], selectedRowIndices[selectedRowIndices.length - 1]);
	}
}
