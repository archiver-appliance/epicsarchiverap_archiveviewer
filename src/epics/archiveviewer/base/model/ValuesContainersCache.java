/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.RequestObject;
import epics.archiveviewer.ValuesContainer;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ValuesContainersCache {	

	//requestObject to HashMap of (GENUINE ArchiveEntry to VC)
	private final LinkedHashMap requestsAndAEsAndVCsMap;
	
	public ValuesContainersCache()
	{
	    this.requestsAndAEsAndVCsMap = new LinkedHashMap();
	}
	
	public void addVCs(RequestObject r0, ValuesContainer[] vcs)
	{
		if(vcs == null)
			return;
		HashMap aesAndVCs = (HashMap) this.requestsAndAEsAndVCsMap.get(r0);
		if(aesAndVCs == null)
		{
			aesAndVCs = new HashMap();
			this.requestsAndAEsAndVCsMap.put(r0, aesAndVCs);
		}
		for(int i=0; i<vcs.length; i++)
		{
			aesAndVCs.put(vcs[i].getAVEntry(), vcs[i]);
		}
	}
	
	//efficiency method
	public AVEntry[] takeOutFormulasAndCachedAEs(RequestObject rO, AVEntry[] aes, final HashSet formulaNames)
	{
		HashMap aesAndVCs = (HashMap) this.requestsAndAEsAndVCsMap.get(rO);
		if(aesAndVCs == null)
			aesAndVCs = new HashMap();
		
		ArrayList result = new ArrayList();
		for(int i=0; i<aes.length; i++)
		{
			if(	formulaNames.contains(aes[i].getName()) == false &&
				aesAndVCs.containsKey(aes[i]) == false)
					result.add(aes[i]);
		}
		return (AVEntry[]) result.toArray(new AVEntry[result.size()]);
	}
	
	public ValuesContainer getVC(RequestObject rO, AVEntry ave)
	{
		HashMap aesAndVCs = (HashMap) this.requestsAndAEsAndVCsMap.get(rO);
		if(aesAndVCs == null)
			return null;
		return (ValuesContainer) aesAndVCs.get(ave);
	}
	
	//order by time, earliest first
	public RequestObject[] getRequestHistory()
	{
		return (RequestObject[])
			this.requestsAndAEsAndVCsMap.keySet().toArray(new RequestObject[requestsAndAEsAndVCsMap.size()]);
	}

	public ValuesContainer[] getVCs(RequestObject rO)
	{
		HashMap aesAndVCs = (HashMap) this.requestsAndAEsAndVCsMap.get(rO);
		if(aesAndVCs == null)
			return null;
		return (ValuesContainer[]) aesAndVCs.values().toArray(new ValuesContainer[aesAndVCs.size()]);
	}
	
	public HashMap getAVEsAndVCsMap(RequestObject rO)
	{
		return (HashMap) this.requestsAndAEsAndVCsMap.get(rO);
	}
	
	public int getStoredNrValues()
	{
		RequestObject[] requestHistory = getRequestHistory();
		ValuesContainer[] vcs = null;
		int result = 0;
		for(int i=0; i<requestHistory.length; i++)
		{
			vcs = getVCs(requestHistory[i]);
			for(int j=0; j<vcs.length; j++)
			{
				try
				{
					result += vcs[j].getNumberOfValues() * vcs[j].getDimension();
				}
				catch(Exception e)
				{
					//do nothing
				}
			}
		}
		return result;
	}
	
	public void removeVCsForRequest(RequestObject rO)
	{
	    this.requestsAndAEsAndVCsMap.remove(rO);
	}
	
	public void removeVCsForAVEntry(AVEntry ae)
	{
		Iterator aesVCsIt = this.requestsAndAEsAndVCsMap.values().iterator();
		HashMap aesAndVCs = null;
		while(aesVCsIt.hasNext())
		{
			aesAndVCs = (HashMap) aesVCsIt.next();
			if(aesAndVCs != null)
				aesAndVCs.remove(ae);
		}
	}
	
	public void clear()
	{
	    this.requestsAndAEsAndVCsMap.clear();
	}
}
