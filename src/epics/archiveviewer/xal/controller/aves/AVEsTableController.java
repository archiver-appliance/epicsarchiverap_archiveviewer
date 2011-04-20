/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.listeners.PlotModelAdapter;
import epics.archiveviewer.base.model.listeners.PlotModelListener;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.swingmodels.AVEsTableModel;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.tables.AVEsTable;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEsTableController
{
	private final AVController avController;
	private final AVEsTable avesTable;
	private final AVEsTableModel avesTModel;
	
	private JButton createButton(int rowIndex)
	{
	    JButton b = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);
	 	
	 	try
		{
	 		Graph g = this.avesTModel.getGraph(rowIndex);;
			if(g.isVisible() == false)
			{
				b.setBackground(Color.WHITE);
				b.setForeground(Color.BLACK);
			}
		}
		catch(Exception e)
		{
			avController.getAVBase().displayError("Can't determine the graph in the row " +
					rowIndex, e);
		}			
	 	return b;
	}
	
    private PlotModelListener createPlotModelListener()
    {
        return new PlotModelAdapter()
		{
			public void avesUpdated() {
				((AVEsTableModel)avesTable.getModel()).fireTableDataChanged();
				
				for(int i=0; i<avesTable.getRowCount(); i++)
				{
					avesTable.getCellEditor(i, AVEsTable.MORE_BUTTON_COLUMN_INDEX).stopCellEditing();
				}
			}
		};
    }
    
    private ActionListener createMoreButtonListener(final int row)
    {
    	return new ActionListener()
    	{
			public void actionPerformed(ActionEvent e) {
				try
				{
					int[] selectedAVEIndices = avesTable.getSelectedRows();
					if(selectedAVEIndices.length == 0)
						selectedAVEIndices = new int[]{row};
					
					avesTable.clearSelection();
					
					Graph[] selectedGraphs = new Graph[selectedAVEIndices.length];
					
					for(int i=0; i<selectedGraphs.length; i++)
					{
						selectedGraphs[i] = avesTModel.getGraph(selectedAVEIndices[i]);
					}
					
					if(selectedGraphs.length == 1)
					{
						if(selectedGraphs[0] instanceof PVGraph)
						{
							new PVConfiguratorPanelController(avController, (PVGraph) selectedGraphs[0]);
						}
						else
						{
							new FormulaConfiguratorPanelController(avController, (FormulaGraph) selectedGraphs[0]);
						}
					}
					else
						new MultipleGraphsConfiguratorPanelController(avController, selectedGraphs);
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't display AVE configurator", ex);
				}
			}
    	};
    }
    
    public AVEsTableController(AVController avc, AVEsTable avesT)
    {
    	this.avController = avc;
    	this.avesTable = avesT;
    	this.avesTModel = new AVEsTableModel(
				this.avesTable,
				this.avController.getAVBase(),
				this.avController.getAVBase().getArchiveDirectoriesRepository()
			);
    	
		this.avesTable.setModel(this.avesTModel);
		
		TableColumnModel tcm = this.avesTable.getColumnModel();
		tcm.getColumn(AVEsTable.MORE_BUTTON_COLUMN_INDEX).setMaxWidth(AVEsTable.BUTTON_WIDTH);
		for(int i=0; i<AVEsTable.NR_OF_COLUMNS; i++)
		{
			tcm.getColumn(i).setHeaderValue(null);
		}
		
		tcm.getColumn(AVEsTable.AVE_NAME_COLUMN_INDEX).setCellRenderer(new AVECellRenderer());
		
		tcm.getColumn(AVEsTable.MORE_BUTTON_COLUMN_INDEX).setCellEditor(new ButtonCellEditor());
		tcm.getColumn(AVEsTable.MORE_BUTTON_COLUMN_INDEX).setCellRenderer(new ButtonCellRenderer());
		
		this.avController.getAVBase().getPlotModel().addPlotModelListener(createPlotModelListener());
    }
    
    private class AVECellRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(
		 		JTable table, 
		 		Object value, 
		 		boolean isSelected, 
		 		boolean hasFocus, 
		 		int row, 
		 		int column)
		 {
		 	JLabel l = 
		 		(JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		 	
		 	try
			{
				Graph g = avesTModel.getGraph(row);
				l.setToolTipText(AVXALUtilities.getToolTip(g));
				l.setForeground(g.getColor());
			}
			catch(Exception e)
			{
				avController.getAVBase().displayError("Can't determine the graph in the row " +
						row, e);
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
		 	return createButton(row);
		 }
	}
	
	private class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor
	{
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) 
		{
			JButton b = createButton(row);
			
			b.addActionListener(createMoreButtonListener(row));		
			
			return b;
		}
		
		public Object getCellEditorValue() {
			return null;
		}
	}	
}
