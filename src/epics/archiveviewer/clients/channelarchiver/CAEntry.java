/*
 * Created on Nov 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.clients.channelarchiver;

import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.AVEntry;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CAEntry extends AVEntry
{	
	private double startTimeInMsecs;
	private double endTimeInMsecs;
	
	protected CAEntry(String _name, ArchiveDirectory archiveDirectory, double start, double end) throws Exception
	{
		super(_name, archiveDirectory);
		setArchiveTimes(start, end);
	}

	protected void setArchiveTimes(double start, double end)
	{
		this.startTimeInMsecs = start;
		this.endTimeInMsecs = end;
	}
	
	protected double getEndTimeInMsecs() {
		return endTimeInMsecs;
	}
	
	protected double getStartTimeInMsecs() {
		return startTimeInMsecs;
	}
}
