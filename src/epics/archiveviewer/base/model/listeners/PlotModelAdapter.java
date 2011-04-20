/*
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model.listeners;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotModelAdapter implements PlotModelListener
{

	public void avesUpdated() {
	}

	public void timeAxesUpdated() {
	}

	public void rangeAxesUpdated() {
	}
	
	public void newModelLoaded()
	{
		avesUpdated();
		timeAxesUpdated();
		rangeAxesUpdated();
	}
	
}
