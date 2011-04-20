/*
 * Created on Feb 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model.listeners;

import epics.archiveviewer.base.model.MatchingAVEsRepository;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SearchMatchesListener {
	public void matchesAdded();
	public void matchesCleared();
}
