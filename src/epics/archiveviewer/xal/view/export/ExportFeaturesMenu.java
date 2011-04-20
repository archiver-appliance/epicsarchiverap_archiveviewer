/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.export;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportFeaturesMenu extends JMenu
{
	private JMenuItem loadTimeBoundsFromSelectedAxisItem;
	private JMenuItem showMoreOptionsItem;
	
	private void addComponents()
	{
		add(this.loadTimeBoundsFromSelectedAxisItem);
		addSeparator();
		add(this.showMoreOptionsItem);
	}
	
	private void createComponents()
	{
		this.loadTimeBoundsFromSelectedAxisItem = new JMenuItem("Load Times From Selected Axis");
		this.showMoreOptionsItem = new JMenuItem("More...");
	}
	
	public ExportFeaturesMenu()
	{
		createComponents();
		addComponents();
	}
	
	public JMenuItem getLoadTimeBoundsItem()
	{
		return this.loadTimeBoundsFromSelectedAxisItem;
	}
	
	public JMenuItem getShowMoreOptionsItem()
	{
		return this.showMoreOptionsItem;
	}
}
