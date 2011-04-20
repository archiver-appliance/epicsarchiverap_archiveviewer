/*
 * Created on 20.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;

import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.base.model.listeners.ArchiveDirectoriesListener;
import gov.sns.tools.messaging.MessageCenter;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArchiveDirectoriesRepository
{
	private final HashMap archiveNamesAndDirectories;
	private final MessageCenter messageCenter;
	private final ArchiveDirectoriesListener adListenerProxy;
	
	public ArchiveDirectoriesRepository()
	{
	    this.archiveNamesAndDirectories = new HashMap();
	    this.messageCenter = MessageCenter.newCenter();
	    this.adListenerProxy = 
	    	(ArchiveDirectoriesListener) this.messageCenter.registerSource(this, ArchiveDirectoriesListener.class);
	}
	
	public void setArchiveDirectories(ArchiveDirectory[] ads)
	{
	    this.archiveNamesAndDirectories.clear();
		for(int i=0; i<ads.length; i++)
		{
			this.archiveNamesAndDirectories.put(ads[i].getName().trim(), ads[i]);
		}
		this.adListenerProxy.archiveDirectoriesRetrieved();
	}
	
	public String[] getSortedArchiveDirectoryNames()
	{
		String[] result = new String[this.archiveNamesAndDirectories.size()];
		result = (String[]) this.archiveNamesAndDirectories.keySet().toArray(result);
		Arrays.sort(result, null);
		return result;
		
	}
	
	public ArchiveDirectory getArchiveDirectory(String directoryName)
	{
		return (ArchiveDirectory) this.archiveNamesAndDirectories.get(directoryName);
	}
	
	public void addArchiveDirectoriesListener(ArchiveDirectoriesListener adl)
	{
		this.messageCenter.registerTarget(adl, this, ArchiveDirectoriesListener.class);
	}
	
	public void removeArchiveDirectoriesListener(ArchiveDirectoriesListener adl)
	{
		this.messageCenter.removeTarget(adl, this, ArchiveDirectoriesListener.class);
	}
	
}
