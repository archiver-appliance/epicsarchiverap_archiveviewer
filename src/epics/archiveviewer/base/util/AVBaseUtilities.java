/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.util;

import java.awt.Component;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ImagePersistenceBean;
import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseFacadeImpl;
import epics.archiveviewer.base.fundamental.Formula;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.FormulaParameter;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.fundamental.Range;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.PlotModel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVBaseUtilities {
	
	public static void assignArchiveToAVEs(AVBase avBase, AVEntry[] aves, String newADName) throws Exception
	{
		PlotModel plotModel = avBase.getPlotModel();
		String aveName = null;
		for(int i=0; i<aves.length; i++)
		{
			if(plotModel.getGraph(aves[i]) instanceof FormulaGraph)
			{
				plotModel.changeFormulaDirectory(
						aves[i],
						avBase.getArchiveDirectoriesRepository().getArchiveDirectory(newADName));
			}
			else
			{
				plotModel.changePVDirectory(
						aves[i],
						avBase.getArchiveDirectoriesRepository().getArchiveDirectory(newADName));
			}
		}
	}
	
	
	public static Document parse(Reader r) throws Exception
	{
		DOMParser parser = new DOMParser();
		InputSource inputSource = new InputSource(r);
		parser.parse(inputSource);
		r.close();
		return parser.getDocument();
	}
	
	/** Changes ranges of range axes so that plots of graphs assigned to
    * different axes do not overlap.
    * The idea is:
    * 1. Calculate size of the full range of all axes (i.e. sum of the individual axes ranges)
    * 2. Using order in which axes were returned by the Plot Plugin, 
    * place all axes on the imaginary range from [0; full range size]
    * 3. Calculate the linear function that maps the actual range of an
    * axis onto its space on the imaginary range
    * 4. Using this function, determine from the entire imaginary range 
    * what the new (actual) range of each axis is ("map it back")
	*/
    public static void alignRangeAxesRanges(AVBase avBase) throws Exception
    {
    	int i = 0;
    	double rangeSizeCounter = 0;
    	
    	String[] rangeAxisNames = avBase.getPlotModel().getRangeAxesNames();
    	Range[] rangesInPlotPlugin = new Range[rangeAxisNames.length];
    	
    	PlotPlugin selectedPlotPlugin = avBase.getPlotPluginsRepository().getSelectedPlotPlugin();
    	
    	double actualOldMin = -1;
    	double actualOldMax = -1;
    	
    	for(i=0; i<rangeAxisNames.length; i++)
    	{
    		actualOldMin = selectedPlotPlugin.getLowerBoundOfRangeAxis(rangeAxisNames[i]); 
    		actualOldMax = selectedPlotPlugin.getUpperBoundOfRangeAxis(rangeAxisNames[i]);
    		rangesInPlotPlugin[i] = new Range(actualOldMin, actualOldMax);
    		
    		rangeSizeCounter += actualOldMax - actualOldMin;
    		
    	}
    	
    	//yup, we must know it before we start all calculations below
    	final double fullRange = rangeSizeCounter;
    	
    	//the place on the imaginary range 
    	double transMin = -1;
    	double transMax = -1;
    	
    	//the actual new range => extend transMin/Max linearly
    	double actualNewMin = -1;
    	double actualNewMax = -1;
    
    	//reset
    	rangeSizeCounter = 0;
    	for(i=0; i<rangesInPlotPlugin.length; i++)
    	{
    		try
			{
	    		actualOldMin = rangesInPlotPlugin[i].min.doubleValue();
	    		actualOldMax = rangesInPlotPlugin[i].max.doubleValue();
	    		
	    		if(	Double.isNaN(actualOldMax) || 
	    			Double.isNaN(actualOldMin))
	    			continue;
	    		
	    		transMin = rangeSizeCounter;
	    		rangeSizeCounter += (actualOldMax - actualOldMin);
	    		transMax = rangeSizeCounter;
	    		
	    		////linear function y=a*x + b => x = (y-b)/a
	    		//	the equations are:
	    		//	actualOldMin = a * transMin + b (1)
	    		//	actualOldMax = a * transMax + b (2)
	    		// (1) => 	b = actualOldMin - a * transMin (3)
	    		// substitute (3) into (2)
	    		// 			actualOldMax = a * transMax +actualOldMin - a * transMin
	    		//	=>
	    		/**a = (actualOldMax - actualOldMin) / (transMax - transMin) = 1*/
	    		//since we give the same range, just a different space
	    		//again (3)
	    		//b = actualOldMin - transMin;
	    		
	    		//other equations are:
	    		// (don't forget, our imaginary range was chosen from [0;fullRange]
	    		/** actualNewMin (= 1 * 0 + b) = b*/// = actualOldMin - transMin;
	    		//actualNewMax = fullRange + b
	    		//actualNewMin = b;
	    		//actualNewMax = fullRange + b = fullRange + actualNewMin
	    		
	    		actualNewMin = actualOldMin - transMin;
	    		actualNewMax = fullRange + actualNewMin;
	    		
	    		selectedPlotPlugin.setRangeAxisBounds(rangeAxisNames[i], actualNewMin, actualNewMax);
	    		
			}
    		catch(Exception e)
			{
    			avBase.displayError("Can't align range axis " + rangeAxisNames[i], e);
			}
    	}
    }
	
	public static PlotPlugin loadPlotPlugin(AVBase avBase, JFrame mainFrame, String className,
			ImagePersistenceBean ngpip) throws Exception
	{
		PlotPlugin plotPlugin = 
			(PlotPlugin) 
			Class.forName(className).
			getConstructor(new Class[] {AVBaseFacade.class, ImagePersistenceBean.class}).
			newInstance(	
					new Object[] 
			                {
								new AVBaseFacadeImpl(avBase, mainFrame), 
								ngpip
			                }
			);
		
		plotPlugin.setAvailableRetrievalMethods(avBase.getClient().getRetrievalMethodsForPlot());
		
		avBase.getPlotPluginsRepository().addPlotPlugin(plotPlugin);
		return plotPlugin;
	}
	
	//does NOT clear the axes manager
	public static void copyAxesRangesFromPlotModelIntoAxesManager(AVBase avBase) throws Exception
	{
		PlotModel plotModel = avBase.getPlotModel();

		String[] timeAxisNames = plotModel.getTimeAxesNames();
		int i=0;
		TimeAxis tA = null;
		Date[] dates = null;
		HashMap timeAxisNamesAndRanges = new HashMap();
		for(i=0; i<timeAxisNames.length; i++)
		{
			tA = plotModel.getTimeAxis(timeAxisNames[i]);
			dates = TimeParser.parse(tA.getStartTime(), tA.getEndTime());
			timeAxisNamesAndRanges.put(
					timeAxisNames[i],
					new Range(
							dates[0].getTime(),
							dates[1].getTime()
							)
						);
		}
		
		String[] rangeAxisNames = plotModel.getRangeAxesNames();
		HashMap rangeAxisNamesAndRanges = new HashMap();
		RangeAxis rA = null;
		for(i=0; i<rangeAxisNames.length; i++)
		{
			rA = plotModel.getRangeAxis(rangeAxisNames[i]);
			rangeAxisNamesAndRanges.put(
					rangeAxisNames[i],
					new Range(rA.getMin(), rA.getMax())
				);
		}
		
		PlotPlugin plotPlugin = avBase.getPlotPluginsRepository().getSelectedPlotPlugin();		
		avBase.getAxesIntervalsManager().addIntervals(plotPlugin, timeAxisNamesAndRanges, rangeAxisNamesAndRanges);
		
	}
	
	public static void addMatchingAVEsWithSpecifiedIndicesToPlotModel(
			AVBase avBase, int[] selectedIndices)
	{
		PVGraph pvg = null;
		for(int i=0; i<selectedIndices.length; i++)
		{
			try
			{
				pvg = avBase.getPlotModel().createNewPVGraph(
						avBase.getMatchingAVEsRepository().getMatchingAVE(selectedIndices[i]));
				avBase.getPlotModel().addGraph(pvg);							
			}
			catch(Exception ex)
			{
				//do nothing
			}
		}
	}
	
	public static String[] tokenize(String input, String delimiter)
	{
		if(input == null)
			return null;
		StringTokenizer st = new StringTokenizer(input, delimiter);
		ArrayList result = new ArrayList();
		while(st.hasMoreTokens())
		{
			result.add(st.nextToken());
		}		
		return (String[]) result.toArray(new String[result.size()]);
	}
	
	public static String assemble(String[] input, String delimiter)
	{
		if(input == null)
			return null;
		StringBuffer sb = new StringBuffer();
	    for(int i=0; i<input.length; i++)
	    {
	        sb.append(input[i]);
	        sb.append(delimiter);
	    }
	    //remove last character
	    sb.deleteCharAt(sb.length() - 1);
	    return sb.toString();
	}
	
	
	//goes through term, copies all characters into
	//a result string, thereby replacing all occurences of oldArgument substring that
	//are not followed or preceeded by a Java identifier (a.o. numbers or letters 
	// => a different argument and/or function)
	//with newArgument
	
	public static String replaceArgumentInTerm(
			final String term, 
			String oldArgument, 
			String newArgument)
	{
		int oldArgLength = oldArgument.length();
		int termLength = term.length();
		int currentIndex = 0;
		int nextIndex = 0;
		StringBuffer sb = new StringBuffer();
		String temp = null;
		while(
				(nextIndex = currentIndex + oldArgLength) <= termLength
			)
		{
			//incl., excl.
			temp = term.substring(currentIndex, currentIndex + oldArgLength);
			if(temp.equals(oldArgument))
			{
				//if previous character doesn't exist or it exists
				//but is not a Java Identifier (i.e. "+" etc.)
				//AND
				//if next character doesn't exist or it exists
				//but is not a Java Identifier (i.e. "+" etc.)
				//=> we found our oldArgument, replace 
				if(	
					(	currentIndex == 0 ||
						!Character.isJavaIdentifierPart(
								term.charAt(currentIndex - 1)
							)
					)	&&
					(	nextIndex ==  termLength ||
							!Character.isJavaIdentifierPart(
									term.charAt(nextIndex)
							)
					)
				)
				{
					sb.append(newArgument);
				}
				else
				{
					//not oldArgument
					sb.append(temp);
				}
				currentIndex += oldArgLength;
			}
			else
			{
				sb.append(term.charAt(currentIndex));
				currentIndex++;
			}
		}
		//append the rest of original term
		sb.append(term.substring(currentIndex));
		return sb.toString();			
	}
	
	//Formula f is considered to contain pv arguments only
	//plotModel may be NULL
	//returns all PVs in the plot model, if such is provided (i.e. non null), as well
	public static AVEntry[] getPVsNeededToCalculateFormula(Formula f, PlotModel plotModel)
	throws Exception
	{
		HashSet aves = new HashSet();
		int i=0;
		if(plotModel != null)
		{
		    //get all PVs that are in the config table
		    AVEntry[] pmAVEs = plotModel.getAVEntries();
		    for(i=0; i<pmAVEs.length; i++)
		    {
		        if(plotModel.getGraph(pmAVEs[i]) instanceof PVGraph)
		            aves.add(pmAVEs[i]);
		    }
		}		
		FormulaParameter[] formulaParameters = f.getFormulaParameters();
		for(i=0; i<formulaParameters.length; i++)
		{
		    if(formulaParameters[i].isAVEFormula())
		        throw new IllegalArgumentException("Formula must contain PV arguments only");
			aves.add(
				new AVEntry(
					formulaParameters[i].getAVEName(),
					f.getAVEntry().getArchiveDirectory()
				)
			);
		}
		return (AVEntry[]) aves.toArray(new AVEntry[aves.size()]);
	}
	
	//returns AE => VC
	public static HashMap createHashMapForVCsFromTheSameRequest(ValuesContainer[] vcs)
	{
		HashMap aesAndVCs = new HashMap();
		for(int i=0; i<vcs.length; i++)
		{
			aesAndVCs.put(vcs[i].getAVEntry(), vcs[i]);
		}
		return aesAndVCs;
	}
	
	//hash map contains all VCs from the same request!!!
	//Fomula f must contain only PVs as arguments 
	public static ValuesContainer createFormulaVC(
			Formula f, 
			ValuesContainer[] vcs) throws Exception
	{
	    HashMap avesAndVCs = new HashMap();
	    int j=0;
	    for(j=0; j<vcs.length; j++)
	    {
	        avesAndVCs.put(vcs[j].getAVEntry(), vcs[j]);
	    }
	    
		ArrayList argVCs = new ArrayList();		
		FormulaParameter[] formulaParameters = f.getFormulaParameters();
		AVEntry ave = null;		
		
		for(j=0; j<formulaParameters.length; j++)
		{
			ave = new AVEntry(
					formulaParameters[j].getAVEName(),
					f.getAVEntry().getArchiveDirectory()
				);
		
			argVCs.add(avesAndVCs.get(ave));		
		}
		return f.calculate((ValuesContainer[]) argVCs.toArray(new ValuesContainer[argVCs.size()]));
	}
	
	//removes null or empty vcs from the parameter
	public static ValuesContainer[] removeEmptyVCs(ValuesContainer[] vcs)
	{
		Vector nonNullVCs = new Vector();
		for(int i=0; i<vcs.length; i++)
		{
			try
			{
				if(vcs[i] != null && vcs[i].getNumberOfValues() > 0)
					nonNullVCs.add(vcs[i]);
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		return (ValuesContainer[]) nonNullVCs.toArray(new ValuesContainer[nonNullVCs.size()]);
	}

	/**
	 * Converts the specified glob expression to the equal regular expression.
	 * 
	 * @param glob
	 *            the glob expression
	 * @return the regular expression
	 */
	public static String convertGlobToRegular(String glob, boolean caseSensitive)
	{
		StringBuffer reg = new StringBuffer();
		int len = glob.length();
	
		for (int i = 0; i < len; i++)
		{
			switch (glob.charAt(i))
			{
			case '*':
				reg.append(".*");;
	
				break;
	
			case '?':
				reg.append(".");
	
				break;
	
			default:
				{
					if(caseSensitive)
						reg.append(glob.substring(i, i+1));
					else
					{
						reg.append("[");
						reg.append(glob.substring(i, i + 1).toLowerCase());
						reg.append(glob.substring(i, i + 1).toUpperCase());
						reg.append("]");
					}
				}
			}
		}
	
		return reg.toString();
	}

	public static String convertExactMatchToRegular(String str)
	{
		int len = str.length();
		
		StringBuffer result = new StringBuffer();
	
		for (int i = 0; i < len; i++)
		{
			switch (str.charAt(i))
			{
			case '*':
				result.append("\\*");;
	
				break;
	
			case '.':
				result.append("\\.");
	
				break;
	
			default:
				{
					result.append(str.substring(i, i + 1));
				}
			}
		}	   
		return "^" + result.toString() + "$";
	}
	
	/**
	 * Taken from gov.sns.application.XALWindow#saveAsSnapshot()
	 * @param c the component whose snapshot needs to be saved
	 * @param f the file to write
	 * @throws Exception
	 */
	public static void saveSnapshotAsPNG(Component component, File f) throws Exception
	{
		BufferedImage image = new BufferedImage( component.getWidth(), component.getHeight(), BufferedImage.TYPE_3BYTE_BGR );
		component.paintAll( image.createGraphics() );
		ImageIO.write( image, "png", f );
	}
	
	/**
	 * Packs the window (see java.awt.Window#pack()) and then sets its size so that in meets the minimum
	 * parameters, which are supplied to this method
	 * @param w the window whose size to set
	 * @param minWidth the minimum width (in Swing units)
	 * @param minHeight the minimum height (in Swing units)
	 */
	public static void setWindowToMinimalSize(Window w, int minWidth, int minHeight)
	{
		w.pack();
		int width = w.getWidth();
		if(width < minWidth)
		{
			width = minWidth;
		}
		
		int height = w.getHeight();
		if(height < minHeight)
		{
			height = minHeight;
		}
		w.setSize(width, height);		
	}
}

