/*
 * Created on 20.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import java.util.ArrayList;
import java.util.HashSet;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.base.model.listeners.ArchiveDirectoriesListener;
import epics.archiveviewer.base.model.listeners.SearchMatchesListener;
import gov.sns.tools.messaging.MessageCenter;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MatchingAVEsRepository
{
	private final MessageCenter messageCenter;
	private final SearchMatchesListener smListenerProxy;
    //duplicates not accepted
	private final ArrayList matchingAVEsList;
	private final HashSet matchingAVEsSet;
	
	public MatchingAVEsRepository()
	{
		this.matchingAVEsList = new ArrayList();
		this.matchingAVEsSet = new HashSet();
		this.messageCenter = MessageCenter.newCenter();
		this.smListenerProxy = 
			(SearchMatchesListener) this.messageCenter.registerSource(this, SearchMatchesListener.class);
	}
	
	public void addMatchingAVEs(AVEntry[] aves)
	{
		for(int i=0; i<aves.length; i++)
		{
			if(this.matchingAVEsSet.contains(aves[i]) == false)
			{
				this.matchingAVEsList.add(aves[i]);
				this.matchingAVEsSet.add(aves[i]);
			}
		}
		this.smListenerProxy.matchesAdded();
	}
	
	public AVEntry[] getAllMatchingAVEs()
	{
		return (AVEntry[]) this.matchingAVEsList.toArray(new AVEntry[this.matchingAVEsList.size()]);
	}
	
	public int getNrOfMatchingAVEs()
	{
		return this.matchingAVEsList.size();
	}
	
	public AVEntry getMatchingAVE(int index) throws Exception
	{
		return (AVEntry) this.matchingAVEsList.get(index);
	}
	
	public void clear()
	{
		this.matchingAVEsList.clear();
		this.matchingAVEsSet.clear();
		this.smListenerProxy.matchesCleared();
	}
	
	public void addSearchMatchesListener(SearchMatchesListener sml)
	{
		this.messageCenter.registerTarget(sml, this, SearchMatchesListener.class);
	}
	
	public void removeSearchMatchesListener(SearchMatchesListener sml)
	{
		this.messageCenter.removeTarget(sml, this, SearchMatchesListener.class);
	}
}
