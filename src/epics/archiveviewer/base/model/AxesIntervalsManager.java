/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.fundamental.Range;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AxesIntervalsManager {
	//pp => list of HistoryItems
	//intervalsHistory is sorted from most recent to oldest
	private final HashMap plotPluginsAndIntervalsHistory;
	
	//currentIndex contains the index of the ranges that are going to be
	//returned when getCurrentTime/RangeInterval() is called;
	private final HashMap plotPluginsAndCurrentIndices;
	
	private int getCurrentHistoryIndex(PlotPlugin pp)
	{
		return ((Integer)this.plotPluginsAndCurrentIndices.get(pp)).intValue();
	}
	
	public AxesIntervalsManager()
	{
		this.plotPluginsAndIntervalsHistory = new HashMap();
		this.plotPluginsAndCurrentIndices = new HashMap();
	}
	
	public void addIntervals(PlotPlugin pp, HashMap timeAxisLabelsAndRanges, HashMap rangeAxisLabelsAndRanges)
	{
		ArrayList intervalsHistory = (ArrayList) this.plotPluginsAndIntervalsHistory.get(pp);
		if(intervalsHistory == null)
		{
			intervalsHistory = new ArrayList();
			this.plotPluginsAndIntervalsHistory.put(pp, intervalsHistory);
		}
		
		Integer currentIndexAsInteger = (Integer) this.plotPluginsAndCurrentIndices.get(pp);
		if(currentIndexAsInteger != null)
		{
			//remove all more recent history entries before the currentIndex
			int currentIndex = currentIndexAsInteger.intValue();
			for(int i=0; i<currentIndex; i++)
			{
				intervalsHistory.remove(i);
			}			
		}
		
		this.plotPluginsAndCurrentIndices.put(pp, new Integer(0));	
		intervalsHistory.add(0, new HistoryItem(timeAxisLabelsAndRanges, rangeAxisLabelsAndRanges));
	}
	
	public Range getCurrentTimeInterval(PlotPlugin pp, String tAName) throws Exception
	{
		ArrayList intervalsHistory = (ArrayList) this.plotPluginsAndIntervalsHistory.get(pp);
		int currentIndex = getCurrentHistoryIndex(pp);
		HistoryItem historyItem = (HistoryItem) intervalsHistory.get(currentIndex);
		return (Range) historyItem.timeIntervals.get(tAName);
	}
	
	public Range getCurrentRangeInterval(PlotPlugin pp, String rAName) throws Exception
	{
		ArrayList intervalsHistory = (ArrayList) this.plotPluginsAndIntervalsHistory.get(pp);
		int currentIndex = getCurrentHistoryIndex(pp);
		HistoryItem historyItem = (HistoryItem) intervalsHistory.get(currentIndex);
		return (Range) historyItem.rangeIntervals.get(rAName);
	}
	
	public void goBack(PlotPlugin pp) throws Exception
	{
		if(isGoingBackPossible(pp) == false)
			throw new Exception("No more axes intervals available");
		int currentIndex = getCurrentHistoryIndex(pp);
		currentIndex++;
		this.plotPluginsAndCurrentIndices.put(pp, new Integer(currentIndex));
	}
	
	public void goForward(PlotPlugin pp) throws Exception
	{
		if(isGoingForwardPossible(pp) == false)
			throw new Exception("No more axes intervals available");
		int currentIndex = getCurrentHistoryIndex(pp);
		currentIndex--;
		this.plotPluginsAndCurrentIndices.put(pp, new Integer(currentIndex));
	}
	
	public boolean isGoingBackPossible(PlotPlugin pp) throws Exception
	{
		int currentIndex = getCurrentHistoryIndex(pp);
		return currentIndex < ((ArrayList) this.plotPluginsAndIntervalsHistory.get(pp)).size() - 1;
	}
	
	public boolean isGoingForwardPossible(PlotPlugin pp) throws Exception
	{
		int currentIndex = getCurrentHistoryIndex(pp);
		return currentIndex > 0;
	}
	
	public void clear()
	{
		this.plotPluginsAndCurrentIndices.clear();
		this.plotPluginsAndIntervalsHistory.clear();
	}
	
	//for testing purposes only
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		Iterator ppIt = this.plotPluginsAndIntervalsHistory.keySet().iterator();
		ArrayList intervalsHistory = null;
		Range r = null;
		int i=0;
		while(ppIt.hasNext())
		{
			PlotPlugin pp = (PlotPlugin) ppIt.next();
			
			sb.append(pp.getName());
			sb.append("\n");
			sb.append("current index: ");
			sb.append(this.plotPluginsAndCurrentIndices.get(pp));
			sb.append("\n");
			intervalsHistory = (ArrayList) this.plotPluginsAndIntervalsHistory.get(pp);
			HistoryItem historyItem = null;
			for(i=0; i<intervalsHistory.size(); i++)
			{
				historyItem = (HistoryItem) intervalsHistory.get(i);
				//time axes
				Iterator axisNameIt = historyItem.timeIntervals.keySet().iterator();
				String axisName = null;
				while(axisNameIt.hasNext())
				{
					axisName = (String) axisNameIt.next();
					sb.append(axisName);
					sb.append("\n");
					
					r = (Range) historyItem.timeIntervals.get(axisName);
					sb.append("min: ");
					sb.append(AVBaseConstants.MAIN_DATE_FORMAT.format(new Date((long)r.min.doubleValue())));
					sb.append("  ");
					sb.append("max: ");
					sb.append(AVBaseConstants.MAIN_DATE_FORMAT.format(new Date((long)r.max.doubleValue())));
					sb.append("\n");	
				}
				//range axes
				axisNameIt = historyItem.rangeIntervals.keySet().iterator();
				while(axisNameIt.hasNext())
				{
					axisName = (String) axisNameIt.next();
					sb.append(axisName);
					sb.append("\n");
					
					r = (Range) historyItem.rangeIntervals.get(axisName);
					sb.append("min: ");
					sb.append(r.min);
					sb.append("  ");
					sb.append("max: ");
					sb.append(r.max);
					sb.append("\n");	
				}
				
			}
		}
		return sb.toString();
	}
	
	private class HistoryItem
	{
		public final HashMap timeIntervals;
		public final HashMap rangeIntervals;
		
		public HistoryItem(HashMap _timeIntervals, HashMap _rangeIntervals)
		{
			this.timeIntervals = _timeIntervals;
			this.rangeIntervals = _rangeIntervals;
		}
	}
}
