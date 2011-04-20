/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.aveconfigurators;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;
import epics.archiveviewer.xal.view.tables.ArgumentsAndAVEsTable;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArgumentsInputPanel extends AVAbstractPanel
{
	
	private JList avesList;
	private ArgumentsAndAVEsTable argumentAVEsTable;
	private JButton addButton;
	private JButton removeButton;
	
	public ArgumentsInputPanel()
	{
		init();
	}
	
	protected void addComponents() {
		JPanel addButtonPanel = new JPanel(new BorderLayout());
		addButtonPanel.add(this.addButton, BorderLayout.EAST);
		
		JPanel removeButtonPanel = new JPanel(new BorderLayout());
		removeButtonPanel.add(this.removeButton, BorderLayout.EAST);
		
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(this.avesList, BorderLayout.CENTER);
		listPanel.add(addButtonPanel, BorderLayout.SOUTH);
		
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(new JScrollPane(this.argumentAVEsTable), BorderLayout.CENTER);
		tablePanel.add(removeButtonPanel, BorderLayout.SOUTH);
		
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, tablePanel);
		jsp.setDividerSize(10);
		
		setLayout(new BorderLayout());
		add(jsp, BorderLayout.CENTER);
	}

	protected void createComponents() {
		this.avesList = new JList(new DefaultListModel());
		this.avesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.argumentAVEsTable = new ArgumentsAndAVEsTable();
		
		this.addButton = new JButton("Add");
		this.removeButton = new JButton("Remove");
	}
	
	public JList getAVEsList()
	{
		return this.avesList;
	}
	
	public ArgumentsAndAVEsTable getArgumentsAndAVEsTable()
	{
		return this.argumentAVEsTable;
	}
	
	public JButton getAddButton()
	{
		return this.addButton;
	}
	
	public JButton getRemoveButton()
	{
		return this.removeButton;
	}
}
