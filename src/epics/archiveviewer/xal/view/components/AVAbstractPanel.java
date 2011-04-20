/*
 * Created on Feb 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.components;

import javax.swing.JPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AVAbstractPanel extends JPanel{

	protected final void init()
	{
		createComponents();
		addComponents();
	}
	/**
	 * Creates components that this panel consists of
	 *
	 */
	protected abstract void createComponents();
	
	/**
	 * Lays out components on this panel
	 *
	 */
	protected abstract void addComponents();
}
