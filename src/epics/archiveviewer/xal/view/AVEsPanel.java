/*
 * Created on Feb 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;
import epics.archiveviewer.xal.view.tables.AVEsTable;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEsPanel extends AVAbstractPanel
{
	private AVEsSelectorPanel avesSelector;
	private AVEsTable avesTable;
	
	public AVEsPanel(){
		init();
	}
	
	protected void addComponents() {
		this.setLayout(new BorderLayout(0,5));
		this.add(this.avesSelector,BorderLayout.NORTH);
		JScrollPane jsp = new JScrollPane(this.avesTable);
		this.add(jsp, BorderLayout.CENTER);
	}
	
	protected void createComponents() {
		this.avesSelector = new AVEsSelectorPanel();
		this.avesTable = new AVEsTable();
	}
	
	public AVEsSelectorPanel getAVEsSelectorPanel()
	{
		return this.avesSelector;
	}
	
	public AVEsTable getAVEsTable()
	{
		return this.avesTable;
	}
	
}
