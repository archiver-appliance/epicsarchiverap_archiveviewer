/*
 * Created on Feb 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.FormulaParameter;
import epics.archiveviewer.xal.controller.listeners.InsertStringIntoTermListener;
import epics.archiveviewer.xal.view.aveconfigurators.FormulaConfiguratorPanel;
import epics.archiveviewer.xal.view.tables.ArgumentsAndAVEsTable;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArgsAndAVEsTableController {
	
	public static final String BUTTON_LABEL = "insert";
	
	private final FormulaConfiguratorPanel fcp;
	
	public ArgsAndAVEsTableController(FormulaConfiguratorPanel _fcp, FormulaGraph fg)
	{
		FormulaParameter[] params = fg.getFormulaParameters();
		this.fcp = _fcp;
		ArgumentsAndAVEsTable table = this.fcp.getArgumentsInputPanel().getArgumentsAndAVEsTable();
		
		TableColumnModel columnModel = table.getColumnModel();
		
		for(int i=0; i<ArgumentsAndAVEsTable.COLUMN_NAMES.length; i++)
		{
			columnModel.getColumn(i).setHeaderValue(ArgumentsAndAVEsTable.COLUMN_NAMES[i]);
		}
		
		ArgsAndAVEsCellRenderer argsAndAVEsRenderer = new ArgsAndAVEsCellRenderer();
		
		columnModel.getColumn(ArgumentsAndAVEsTable.AVE_COLUMN_INDEX).setCellRenderer(argsAndAVEsRenderer);
		columnModel.getColumn(ArgumentsAndAVEsTable.AVE_COLUMN_INDEX).setMinWidth(3 * ArgumentsAndAVEsTable.COLUMN_WIDTH_UNIT);
		
		columnModel.getColumn(ArgumentsAndAVEsTable.ARG_COLUMN_INDEX).setCellRenderer(argsAndAVEsRenderer);
		columnModel.getColumn(ArgumentsAndAVEsTable.ARG_COLUMN_INDEX).setMaxWidth(2 * ArgumentsAndAVEsTable.COLUMN_WIDTH_UNIT);
		columnModel.getColumn(ArgumentsAndAVEsTable.ARG_COLUMN_INDEX).setMinWidth(ArgumentsAndAVEsTable.COLUMN_WIDTH_UNIT);
		
		columnModel.getColumn(ArgumentsAndAVEsTable.BUTTON_COLUMN_INDEX).setCellEditor(new ButtonCellEditor());
		columnModel.getColumn(ArgumentsAndAVEsTable.BUTTON_COLUMN_INDEX).setCellRenderer(new ButtonCellRenderer());
		columnModel.getColumn(ArgumentsAndAVEsTable.BUTTON_COLUMN_INDEX).setMinWidth(2 * ArgumentsAndAVEsTable.COLUMN_WIDTH_UNIT);
		columnModel.getColumn(ArgumentsAndAVEsTable.BUTTON_COLUMN_INDEX).setMaxWidth(2 * ArgumentsAndAVEsTable.COLUMN_WIDTH_UNIT);
	
		
		DefaultTableModel tModel = (DefaultTableModel) table.getModel();
					
    	for(int i=0; i<params.length; i++)
    	{
    		addAVENameAndArg(params[i].getAVEName(), params[i].getArg());
    	}
	}
	
	//if arg NULL => assign a new argument
	public void addAVENameAndArg(String aveName, String arg)
	{
		DefaultTableModel tModel = 
			(DefaultTableModel) fcp.getArgumentsInputPanel().getArgumentsAndAVEsTable().getModel();
		if(arg == null)
		{
			HashSet usedArgs = new HashSet();
			int i=0;
			for(i=0; i<tModel.getRowCount(); i++)
			{
				usedArgs.add(tModel.getValueAt(i, ArgumentsAndAVEsTable.ARG_COLUMN_INDEX));
			}
			i=0;
			String s = "x";
			do
			{
				if(usedArgs.contains(s + i) == false)
					break;
				i++;
			}
			while(i<1000);
			arg = s + i;
		}
		
		Object[] row = new Object[ArgumentsAndAVEsTable.COLUMN_NAMES.length];
		row[ArgumentsAndAVEsTable.AVE_COLUMN_INDEX] = aveName;
		row[ArgumentsAndAVEsTable.ARG_COLUMN_INDEX] = arg;
		
    	tModel.addRow(row);
	}
	
	private class ArgsAndAVEsCellRenderer extends DefaultTableCellRenderer
	{

		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column)
		{
			JLabel l = (JLabel) super.getTableCellRendererComponent(
					table,
					value,
					isSelected,
					hasFocus,
					row,
					column);
			l.setHorizontalAlignment(SwingConstants.CENTER);
			if(column  == ArgumentsAndAVEsTable.ARG_COLUMN_INDEX)
			{
				Font f = l.getFont().deriveFont(Font.BOLD);
				l.setFont(f);
			}
			return l;
		}
	}
	
	private class ButtonCellRenderer implements TableCellRenderer
	{
		public Component getTableCellRendererComponent(
		 		JTable table, 
		 		Object value, 
		 		boolean isSelected, 
		 		boolean hasFocus, 
		 		int row, 
		 		int column)
		 {
		 	return new JButton(BUTTON_LABEL);
		 }
	}
	
	private class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor
	{
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) 
		{
			JButton b = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
			
			String arg = fcp.getArgumentsInputPanel().getArgumentsAndAVEsTable().getValueAt(
					row, ArgumentsAndAVEsTable.ARG_COLUMN_INDEX).toString();
			b.addActionListener(new InsertStringIntoTermListener(
					fcp.getCalculatorPanel().getTermField(),
					arg
					)
				);
			
			return b;
		}
		
		public Object getCellEditorValue() {
			return null;
		}
	}	
	
}
