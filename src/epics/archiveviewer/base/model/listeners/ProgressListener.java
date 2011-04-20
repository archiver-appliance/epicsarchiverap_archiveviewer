/*
 * Created on Mar 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model.listeners;

import java.awt.event.ActionListener;

import epics.archiveviewer.ProgressTask;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ProgressListener implements ActionListener
{	
	private final ProgressTask progress;
	
	public ProgressListener(ProgressTask p)
	{
		this.progress = p;
	}
	
	public ProgressTask getProgress()
	{
		return this.progress;
	}
	
	public void interrupt()
	{
		if(this.progress != null)
			this.progress.interrupt();
	}
}
