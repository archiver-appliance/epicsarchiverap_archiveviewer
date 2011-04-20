/*
 * Created on Feb 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.model;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.FormulaParameter;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.listeners.PlotModelListener;
import epics.archiveviewer.base.persistence.PlotConfigurationLegacyHelper;
import epics.archiveviewer.base.persistence.XMLPlotModel;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.base.util.ColorUtilities;
import epics.archiveviewer.xal.AVXALConstants;
import gov.sns.tools.messaging.MessageCenter;

public class PlotModel {
	public static int SORT_BY_AVE_NAME = 0;
	public static int SORT_BY_AD_NAME = 1;
	
	private static Comparator BY_AVE_NAME_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2) {
			AVEntry ave1 = (AVEntry) o1;
			AVEntry ave2 = (AVEntry) o2;
			
			return ave1.getName().compareTo(ave2.getName());
		}	
	};
	
	private static Comparator BY_AD_NAME_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2) {
			AVEntry ave1 = (AVEntry) o1;
			AVEntry ave2 = (AVEntry) o2;
			
			return ave1.getArchiveDirectory().getName().compareTo(ave2.getArchiveDirectory().getName());
		}
	};
	
	private static void replace(List list, Object oldO, Object newO)
	{
		if(oldO != null && newO != null)
		{
			for(int i=0; i<list.size(); i++)
			{
				if(oldO.equals(list.get(i)))
				{
					list.remove(i);
					list.add(i, newO);
					return;
				}
			}
		}
	}
	
	private final MessageCenter messageCenter;
	private final PlotModelListener pmListenerProxy;
	
	private XMLPlotModel xmlPM;
	
	//data base
	private String connectionParameter;
	private final ArrayList avEntries;
	private final HashMap avesToGraphs;
	private final ArrayList timeAxisNames;
	private final HashMap namesToTimeAxes;
	private final ArrayList rangeAxisNames;
	private final HashMap namesToRangeAxes;
	private String plotTitle;
	private Color plotBGColor;
	private LegendInfo legendInfo;
	
	private Graph getGraph(int index)
	{
		return (Graph) avesToGraphs.get(this.avEntries.get(index));
	}
	
	private HashSet getUsedColorsSet()
	{
		HashSet result = new HashSet();

		for(int i=0; i<this.avEntries.size(); i++)
		{
			result.add(getGraph(i).getColor());
		}
		
		return result;
	}
	
	private void changeTimeAxisReferencesInGraphs(String oldName, String newName)
	{
		Graph g = null;
		for(int i=0; i<this.avEntries.size(); i++)
		{
			g = getGraph(i);
			if(g.getTimeAxisLabel().equals(oldName))
				g.setTimeAxisLabel(newName);
		}
	}
	
	private void changeRangeAxisReferencesInGraphs(String oldName, String newName)
	{
		Graph g = null;
		for(int i=0; i<this.avEntries.size(); i++)
		{
			g = getGraph(i);
			if(g.getRangeAxisLabel().equals(oldName))
				g.setRangeAxisLabel(newName);
		}
	}
	
	public PlotModel()
	{
		this.messageCenter = MessageCenter.newCenter();
		this.pmListenerProxy = 
			(PlotModelListener) this.messageCenter.registerSource(this, PlotModelListener.class);
		
		this.avEntries = new ArrayList();
		this.avesToGraphs = new HashMap();
		this.timeAxisNames = new ArrayList();
		this.namesToTimeAxes = new HashMap();
		this.rangeAxisNames = new ArrayList();
		this.namesToRangeAxes = new HashMap();
		this.legendInfo = new LegendInfo(true, true, true, true);
		loadInitialAxesSettings();
	}	
	
	public void loadInitialAxesSettings()
	{
		try
		{
			this.rangeAxisNames.add(AVBaseConstants.DEFAULT_RANGE_AXIS_NAME);
			this.namesToRangeAxes.put(
					AVBaseConstants.DEFAULT_RANGE_AXIS_NAME,
					new RangeAxis(
							AVBaseConstants.DEFAULT_RANGE_AXIS_NAME,
							AVBaseConstants.DEFAULT_RANGE_MIN,
							AVBaseConstants.DEFAULT_RANGE_MAX,
							AVBaseConstants.DEFAULT_RANGE_AXIS_TYPE,
							AVBaseConstants.DEFAULT_RANGE_AXIS_LOCATION
							)
						);
			
			this.timeAxisNames.add(AVBaseConstants.DEFAULT_TIME_AXIS_NAME);
			this.namesToTimeAxes.put(
					AVBaseConstants.DEFAULT_TIME_AXIS_NAME,
					new TimeAxis(
							AVBaseConstants.DEFAULT_TIME_AXIS_NAME,
							AVBaseConstants.DEFAULT_START_TIME,
							AVBaseConstants.DEFAULT_END_TIME,
							AVBaseConstants.DEFAULT_TIME_AXIS_LOCATION
							)
						);
		}
		catch(Exception e)
		{
			//do nothing
		}
	}
	
	public String getConnectionParameter()
	{
		return this.connectionParameter;
	}
	
	public void setConnectionParameter(String parameter)
	{
		this.connectionParameter = parameter;
	}
	
	public void addGraph(Graph g)
	{		
		if(this.avesToGraphs.containsKey(g.getAVEntry()) == false)
			this.avEntries.add(g.getAVEntry());
		this.avesToGraphs.put(g.getAVEntry(), g);
	}
	
	public Graph getGraph(AVEntry ave)
	{
		return (Graph) this.avesToGraphs.get(ave);
	}
	
	public AVEntry[] getAVEntries()
	{
		return (AVEntry[]) this.avEntries.toArray(new AVEntry[this.avEntries.size()]);
	}
	
	public int getNrOfAVEs()
	{
		return this.avEntries.size();
	}
	
	public AVEntry getAVEntry(int index)
	{
		return (AVEntry) this.avEntries.get(index);
	}
	
	public String[] getTimeAxesNames()
	{
		return (String[]) this.timeAxisNames.toArray(new String[this.timeAxisNames.size()]);	
	}
	
	public String[] getRangeAxesNames()
	{
		return (String[]) this.rangeAxisNames.toArray(new String[this.rangeAxisNames.size()]);	
	}
	
	public String getTimeAxisName(int index) throws Exception
	{
		return (String) this.timeAxisNames.get(index);
	}
	
	public String getRangeAxisName(int index)
	{
		return (String) this.rangeAxisNames.get(index);
	}
	
	//TODO remove exceptions handling
	public void addTimeAxis(TimeAxis tA)
	{
		if(this.namesToTimeAxes.containsKey(tA.getName()) == false)
			this.timeAxisNames.add(tA.getName());
		this.namesToTimeAxes.put(tA.getName(), tA);
	}
	
	public TimeAxis getTimeAxis(String timeAxisName)
	{
		return (TimeAxis) this.namesToTimeAxes.get(timeAxisName);
	}
	
	public void addRangeAxis(RangeAxis rA)
	{
		if(this.namesToRangeAxes.containsKey(rA.getName()) == false)
			this.rangeAxisNames.add(rA.getName());
		this.namesToRangeAxes.put(rA.getName(), rA);
	}
	
	public RangeAxis getRangeAxis(String rangeAxisName)
	{
		return (RangeAxis) this.namesToRangeAxes.get(rangeAxisName);
	}

	public String getPlotTitle()
	{
		if(this.plotTitle == null)
			this.plotTitle = "";
		return this.plotTitle;
	}
	
	public void setPlotTitle(String plotTitle)
	{
		this.plotTitle = plotTitle;
	}
	
	public Color getPlotBGColor()
	{
		if(this.plotBGColor == null)
			this.plotBGColor = AVBaseConstants.DEFAULT_PLOT_BACKGROUND;
		return this.plotBGColor;
	}
	
	public void setPlotBGColor(Color c)
	{
		this.plotBGColor = c;
	}
	
	public LegendInfo getLegendInfo() throws Exception
	{
		return this.legendInfo;
	}
	
	public void setLegendInfo(LegendInfo li)
	{
		this.legendInfo = li;
	}	
	
	public void removeGraph(AVEntry ave)
	{
		this.avesToGraphs.remove(ave);
		for(int i=0; i<this.avEntries.size(); i++)
		{
			if(ave.equals(this.avEntries.get(i)))
			{
				this.avEntries.remove(i);			
				return;
			}
		}
	}
	
	public void removeTimeAxis(String timeAxisName) throws Exception
	{
		if(this.timeAxisNames.size() == 1)
			throw new Exception("Must not remove all time axes");
		this.namesToTimeAxes.remove(timeAxisName);
		this.avEntries.remove(timeAxisName);
		
		changeTimeAxisReferencesInGraphs(timeAxisName, getTimeAxisName(0));
	}
	
	public void removeRangeAxis(String rangeAxisName) throws Exception
	{
		if(this.namesToRangeAxes.size() == 1)
			throw new Exception("Must not remove all range axes");
		this.namesToRangeAxes.remove(rangeAxisName);
		this.rangeAxisNames.remove(rangeAxisName);
		changeRangeAxisReferencesInGraphs(rangeAxisName, getRangeAxisName(0));
	}
	
	//no changes in formulas
	public void changePVName(AVEntry oldAVE, String newName) throws Exception
	{
		AVEntry newAVE = new AVEntry(newName, oldAVE.getArchiveDirectory());
		if(this.avesToGraphs.containsKey(newAVE))
			throw new Exception("This AV entry already exists");
		PVGraph pvg = (PVGraph) this.avesToGraphs.get(oldAVE);
		
		this.avesToGraphs.remove(oldAVE);
		this.avesToGraphs.put(newAVE, 
				new PVGraph(
						newAVE,
						pvg.getTimeAxisLabel(),
						pvg.getRangeAxisLabel(), 
						pvg.getColor(), 
						pvg.getDrawType(),
						pvg.getDrawWidth(),
						pvg.isVisible()));
		
		replace(this.avEntries, oldAVE, newAVE);
	}
	
	public void changeFormulaName(AVEntry oldAVE, String newName) throws Exception
	{
		AVEntry newAVE = new AVEntry(newName, oldAVE.getArchiveDirectory());
		if(this.avesToGraphs.containsKey(newAVE))
			throw new Exception("This AV entry already exists");
		FormulaGraph fg = (FormulaGraph) this.avesToGraphs.get(oldAVE);
		
		this.avesToGraphs.remove(oldAVE);
		this.avesToGraphs.put(newAVE, 
				new FormulaGraph(
						newAVE,
						fg.getTerm(),
						fg.getFormulaParameters(),
						fg.getRetrievalMethodName(),
						fg.getRequestedNumberOfValues(),
						fg.getTimeAxisLabel(),
						fg.getRangeAxisLabel(), 
						fg.getColor(), 
						fg.getDrawType(),
						fg.getDrawWidth(),
						fg.isVisible()
					)
				);
		
		replace(this.avEntries, oldAVE, newAVE);
	}
	
	//no changes in formulas
	public void changePVDirectory(AVEntry oldAVE, ArchiveDirectory newAD) throws Exception
	{
		AVEntry newAVE = new AVEntry(oldAVE.getName(), newAD);
		if(this.avesToGraphs.containsKey(newAVE))
			throw new Exception("This AV entry already exists");
		PVGraph pvg = (PVGraph) this.avesToGraphs.get(oldAVE);
		
		this.avesToGraphs.remove(oldAVE);
		this.avesToGraphs.put(newAVE, 
				new PVGraph(
						newAVE,
						pvg.getTimeAxisLabel(),
						pvg.getRangeAxisLabel(), 
						pvg.getColor(), 
						pvg.getDrawType(),
						pvg.getDrawWidth(),
						pvg.isVisible()));
		
		replace(this.avEntries, oldAVE, newAVE);
	}
	
	//no changes in formulas
	public void changeFormulaDirectory(AVEntry oldAVE, ArchiveDirectory newAD) throws Exception
	{
		AVEntry newAVE = new AVEntry(oldAVE.getName(), newAD);
		if(this.avesToGraphs.containsKey(newAVE))
			throw new Exception("This AV entry already exists");
		FormulaGraph fg = (FormulaGraph) this.avesToGraphs.get(oldAVE);
		
		this.avesToGraphs.remove(oldAVE);
		this.avesToGraphs.put(newAVE, 
				new FormulaGraph(
						newAVE,
						fg.getTerm(),
						fg.getFormulaParameters(),
						fg.getRetrievalMethodName(),
						fg.getRequestedNumberOfValues(),
						fg.getTimeAxisLabel(),
						fg.getRangeAxisLabel(), 
						fg.getColor(), 
						fg.getDrawType(),
						fg.getDrawWidth(),
						fg.isVisible()
					)
				);
		
		replace(this.avEntries, oldAVE, newAVE);
	}
	
	//also change all references
	public void changeTimeAxisName(String oldName, String newName) throws Exception
	{
		if(newName == null || newName.trim().equals(""))
			throw new Exception("No empty name allowed!");
		
		if(this.namesToTimeAxes.containsKey(newName))
			throw new Exception("Time axis with the name "+ newName + " already exists");
		
		TimeAxis tA = (TimeAxis) this.namesToTimeAxes.get(oldName);
		this.namesToTimeAxes.remove(oldName);
		this.namesToTimeAxes.put(	newName,
									new TimeAxis(
											newName,
											tA.getStartTime(),
											tA.getEndTime(),
											tA.getLocation()
									)
								);
		replace(this.timeAxisNames, oldName, newName);
		
		changeTimeAxisReferencesInGraphs(oldName, newName);
	}
	
	//also change all references
	public void changeRangeAxisName(String oldName, String newName) throws Exception
	{
		if(newName == null || newName.trim().equals(""))
			throw new Exception("No empty name allowed!");
		
		if(this.namesToRangeAxes.containsKey(newName))
			throw new Exception("Range axis with the name "+ newName + " already exists");
		
		RangeAxis rA = (RangeAxis) this.namesToRangeAxes.get(oldName);
		this.namesToRangeAxes.remove(oldName);
		this.namesToRangeAxes.put(	newName,
									new RangeAxis(
											newName,
											rA.getMin(),
											rA.getMax(),
											rA.getType(),
											rA.getLocation()
									)
								);
		
		replace(this.rangeAxisNames, oldName, newName);
		
		changeRangeAxisReferencesInGraphs(oldName, newName);
	}
	
	//does not add the result to this model!!!
	public FormulaGraph createNewFormulaGraph(ArchiveDirectory ad, String defaultMethodName) 
	throws Exception
	{
		String name = "formula";
		int i=0;
		AVEntry ave = null;
		do
		{
			ave = new AVEntry(name + i, ad);
			i++;
			//some limit 
			if(i > 1000)
				throw new Exception("Can't create a formula graph");
		}
		while(this.avesToGraphs.containsKey(ave));
		
		return new FormulaGraph(
					ave,
					AVXALConstants.DEFAULT_TERM,
					new FormulaParameter[]{},
					defaultMethodName,
					AVBaseConstants.DEFAULT_NR_VALUES,
					getTimeAxisName(0),
					getRangeAxisName(0),
					ColorUtilities.getNextAvailableColor(getUsedColorsSet()),
					AVBaseConstants.DEFAULT_DRAW_TYPE,
					AVBaseConstants.DEFAULT_DRAW_WIDTH,
					true
				);	
	}
	
	//does not add the result to this model!!!
	public PVGraph createNewPVGraph(AVEntry ave) throws Exception
	{
		return new PVGraph(
					ave,
					getTimeAxisName(0),
					getRangeAxisName(0),
					ColorUtilities.getNextAvailableColor(getUsedColorsSet()),
					AVBaseConstants.DEFAULT_DRAW_TYPE,
					AVBaseConstants.DEFAULT_DRAW_WIDTH,
					true
				);	
	}

	public boolean isArgAVEFormula(String aveName)
	{
		Iterator graphsIt = this.avesToGraphs.values().iterator();
		Graph g = null;
		while(graphsIt.hasNext())
		{
			g = (Graph) graphsIt.next();
			if(	g.getAVEntry().getName().equals(aveName)	&&
				g instanceof FormulaGraph)
				return true;
		}
		return false;
	}
	
	public void clear()
	{
		this.avEntries.clear();
		this.avesToGraphs.clear();
		this.rangeAxisNames.clear();
		this.namesToRangeAxes.clear();
		this.timeAxisNames.clear();
		this.namesToTimeAxes.clear();
	}
	
	public void addPlotModelListener(PlotModelListener pml)
	{
		this.messageCenter.registerTarget(pml, this, PlotModelListener.class);
	}
	
	public void removePlotModelListener(PlotModelListener pml)
	{
		this.messageCenter.removeTarget(pml, this, PlotModelListener.class);
	}
	
	public void fireAVEsUpdated()
	{
		this.pmListenerProxy.avesUpdated();
	}
	
	public void fireTimeAxesUpdated()
	{
		this.pmListenerProxy.timeAxesUpdated();
	}
	
	public void fireRangeAxesUpdated()
	{
		this.pmListenerProxy.rangeAxesUpdated();
	}	
	
	public void fireNewModelLoaded()
	{
		this.pmListenerProxy.newModelLoaded();
	}
	
	public void loadEverythingButAVEs(File f) throws Exception
	{
		//		legacy support
		DocumentImpl fDoc = (DocumentImpl) AVBaseUtilities.parse(new FileReader(f));
		this.xmlPM = new XMLPlotModel(PlotConfigurationLegacyHelper.translateToCurrentDTD(fDoc));
		
		setConnectionParameter(this.xmlPM.getConnectionParameter());
		int i=0;
		{
			String[] timeAxisNames = this.xmlPM.getTimeAxesNames();
			for(i=0; i<timeAxisNames.length; i++)
			{
				addTimeAxis(this.xmlPM.getTimeAxis(timeAxisNames[i]));
			}
		}
		
		{
			String[] rangeAxisNames = this.xmlPM.getRangeAxesNames();
			for(i=0; i<rangeAxisNames.length; i++)
			{
				addRangeAxis(this.xmlPM.getRangeAxis(rangeAxisNames[i]));
			}
		}
		
		setLegendInfo(this.xmlPM.getLegendInfo());
		setPlotTitle(this.xmlPM.getPlotTitle());
	}
	
	public void loadAVEs(AVBase avBase)
	{		
		AVEntry[] aves = this.xmlPM.getAVEntries(avBase);
		
		int i=0;
		Graph g = null;
		for(i=0; i<aves.length; i++)
		{
			try
			{
			    g = this.xmlPM.getGraph(aves[i]);
			    addGraph(g);
			}
			catch(Exception e)
			{
			    avBase.displayError(
			            "Could not create configuration for AV entry " +
			            aves[i].toString(),
			            e);
			}
			
		}
	}
	
	/**
	 * Closes the writer, too
	 * @param w
	 * @throws IOException
	 */
	public void serialize(Writer w) throws Exception
	{
		Document doc = AVBaseUtilities.parse(new StringReader(XMLPlotModel.BASIC_DOCUMENT_AS_STRING)); 
		this.xmlPM = new XMLPlotModel(doc);
		
		//see DTD
		
		this.xmlPM.setConnectionParameter(getConnectionParameter());
		
		AVEntry[] aves = getAVEntries();
		
		int i=0;
		Graph g = null;
		for(i=0; i<aves.length; i++)
		{
			g = getGraph(aves[i]);
			if(g instanceof FormulaGraph)
				this.xmlPM.addFormula((FormulaGraph)g);
			else
				this.xmlPM.addPV((PVGraph)g);
		}
		{
			String[] timeAxisNames = getTimeAxesNames();
			TimeAxis tA = null;
			for(i=0; i<timeAxisNames.length; i++)
			{
				tA = getTimeAxis(timeAxisNames[i]);
				this.xmlPM.addTimeAxis(
						tA.getName(),
						tA.getStartTime(),
						tA.getEndTime(),
						tA.getLocation().toString()
				);
			}
		}
		
		{
			String[] rangeAxisNames = getRangeAxesNames();
			RangeAxis rA = null;
			for(i=0; i<rangeAxisNames.length; i++)
			{
				rA = getRangeAxis(rangeAxisNames[i]);
				String min = "";
				if(rA.getMin() != null)
					min = rA.getMin().toString();
				String max = "";
				if(rA.getMax() != null)
					max = rA.getMax().toString();
				
				this.xmlPM.addRangeAxis(
						rA.getName(),
						min,
						max,
						rA.getType().toString(),
						rA.getLocation().toString()
				);
			}
		}
		
		this.xmlPM.setLegendInfo(getLegendInfo());
		this.xmlPM.setPlotTitle(getPlotTitle());
		
		
		OutputFormat of = new OutputFormat(doc, "UTF-16", true);
		of.setLineWidth(Integer.MAX_VALUE);
		XMLSerializer xs = new XMLSerializer(w, of);
		xs.serialize(doc);
		w.close();
	}
	
	public void sortAVEs(int byWhat) throws Exception
	{
		Object[] temp = this.avEntries.toArray(new Object[this.avEntries.size()]);
		
		if(byWhat == SORT_BY_AVE_NAME)
			Arrays.sort(temp, BY_AVE_NAME_COMPARATOR);
		else
			Arrays.sort(temp, BY_AD_NAME_COMPARATOR);
		
		this.avEntries.clear();
		for(int i=0; i<temp.length; i++)
		{
			this.avEntries.add(temp[i]);
		}
	}
	
	public void assignSameColorToAVEsWithSameName()
	{
		HashMap aveNamesToColors = new HashMap();
		AVEntry currentAVE = null;
		String aveName = null;
		Graph g = null;
		Color c = null;
		int i=0;
		for(i=0; i<this.avEntries.size(); i++)
		{
			currentAVE = (AVEntry) this.avEntries.get(i);
			aveName = currentAVE.getName();
			g = (Graph) this.avesToGraphs.get(currentAVE);
			
			if(aveNamesToColors.containsKey(aveName))
			{
				//assign the color to the graph of this AVE
				c = (Color) aveNamesToColors.get(aveName);
				g.setColor(c);
			}
			else
			{
				//add this ave name and its color to the hashmap
				aveNamesToColors.put(aveName, g.getColor()); 
			}			
		}
	}
}
