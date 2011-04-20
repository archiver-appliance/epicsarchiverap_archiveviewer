/*
 * Created on Feb 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.axesconfigurators;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AxesFeaturesMenu extends JMenu
{
	private JMenuItem addNewAxisItem;
	private JMenuItem removeSelectedAxisItem;
	private JMenuItem renameSelectedAxisItem;
	private JMenuItem updateSelectedAxisItem;
	private JMenuItem updateAllAxesItem;
	private JMenuItem resetSelectedAxisItem;
	private JMenuItem resetAllAxesItem;
	
	private void addComponents()
	{
		add(this.addNewAxisItem);
		add(this.removeSelectedAxisItem);
		addSeparator();
		add(this.renameSelectedAxisItem);
		addSeparator();
		add(this.updateSelectedAxisItem);
		add(this.updateAllAxesItem);
		addSeparator();
		add(this.resetSelectedAxisItem);
		add(this.resetAllAxesItem);
	}
	
	private void createComponents()
	{
		this.addNewAxisItem = new JMenuItem("Add New Axis");
		this.removeSelectedAxisItem = new JMenuItem("Remove Selected Axis");
		this.renameSelectedAxisItem = new JMenuItem("Rename Selected Axis");
		this.updateSelectedAxisItem = new JMenuItem("Update Selected Axis From PlotPlugin");
		this.updateAllAxesItem = new JMenuItem("Update All Axes From PlotPlugin");
		this.resetSelectedAxisItem = new JMenuItem("Reset Selected Axis");
		this.resetAllAxesItem = new JMenuItem("Reset All Axes");
	}
	
	public AxesFeaturesMenu()
	{
		createComponents();
		addComponents();
	}
	
	public JMenuItem getAddNewAxisItem()
	{
		return this.addNewAxisItem;
	}
	
	public JMenuItem getRemoveSelectedAxisItem()
	{
		return this.removeSelectedAxisItem;
	}
	
	public JMenuItem getRenameSelectedAxisItem()
	{
		return this.renameSelectedAxisItem;
	}
	
	public JMenuItem getUpdateSelectedAxisItem()
	{
		return this.updateSelectedAxisItem;
	}
	
	public JMenuItem getUpdateAllAxesItem()
	{
		return this.updateAllAxesItem;
	}
	
	public JMenuItem getResetSelectedAxisItem()
	{
		return this.resetSelectedAxisItem;
	}
	
	public JMenuItem getResetAllAxesItem()
	{
		return this.resetAllAxesItem;
	}
}
