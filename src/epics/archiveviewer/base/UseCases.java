/*
 * Created on Jan 19, 2005
 * @version 14-Sep-2009 Bob Hall. Set new includeSparcified flag in RequestObject.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.ClientPlugin;
import epics.archiveviewer.Exporter;
import epics.archiveviewer.RequestObject;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.export.MatlabExporter;
import epics.archiveviewer.base.fundamental.Formula;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.PlotPluginsRepository;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.base.util.AVProgressTask;
import epics.archiveviewer.base.util.TimeParser;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UseCases {
	

	//	returns an ArrayList of proper ValuesContainers, may return NULL
	//	if retrievalMethod is NULL, use whatever retrieval method the formula graph specifies
	// 	(also, disregard requestedNrOfValues in such case)
	private static ArrayList retrieveFormulaDataForExport(
			AVBase avBase,
			final Formula[] _formulas,
			RequestObject request,
			AVProgressTask avp
			) throws Exception
	{		
		ArrayList result = new ArrayList();
		ValuesContainer vc = null;
		int i=0;
		
		ArrayList yetToBeCalculatedFormulaGraphs = new ArrayList();
		HashSet formulaNames = new HashSet();
		for(i=0; i<_formulas.length; i++)
		{
			yetToBeCalculatedFormulaGraphs.add(_formulas[i]);
			formulaNames.add(_formulas[i].getAVEntry().getName());
		}
		
		Formula f = null;
		HashMap formulasWithPVArgumentsOnlyMap = new HashMap();
		
		//- if we can not calculate a formula and tempFG is NULL, tempFG is set to this formula
		//- if we remove a formula, tempFG is set to NULL
		//- if at some point current processed formula equals tempFG => 
		// "bad formula" exception
		Formula tempF = null;
		
		while(yetToBeCalculatedFormulaGraphs.isEmpty() == false)
		{
			f = (Formula) yetToBeCalculatedFormulaGraphs.get(0);
			
			if(f == tempF)
			{
				//bad formulas
				StringBuffer sb = new StringBuffer("Bad formulas: ");
				for(i=0; i<yetToBeCalculatedFormulaGraphs.size(); i++)
				{
					f = (Formula) yetToBeCalculatedFormulaGraphs.get(i);
					sb.append(f.getAVEntry().getName());
					sb.append("; ");
				}
				throw new Exception(sb.toString());
			}
			//always remove it => if not able to calculate, add back to list at the end
		    yetToBeCalculatedFormulaGraphs.remove(f);
		    
		    Formula currentFormulaWithPVArgumentsOnly = f.tryToEliminateFormulaArguments(formulasWithPVArgumentsOnlyMap);
		    
		    if(currentFormulaWithPVArgumentsOnly == null)
		    {
		    	//formula f uses a formula that was not resolved in respect to pvs,
		    	//i.e. contains formula args
		    	//add the formula that we couldn't calculate because it contains other formulas
		    	//at the end of the array list
		    	yetToBeCalculatedFormulaGraphs.add(f);
		    	if(tempF == null)
		    		tempF = f;
		    }
		    else
		    {
		    	//add to the map of resolved formulas, i.e. formulas that have no formula arguments
		    	formulasWithPVArgumentsOnlyMap.put(currentFormulaWithPVArgumentsOnly.getAVEntry().getName(), currentFormulaWithPVArgumentsOnly);
		    	AVEntry[] pvs = AVBaseUtilities.getPVsNeededToCalculateFormula(
		    	        currentFormulaWithPVArgumentsOnly,
		    	        avBase.getPlotModel());	
			    
		    	//retrieve all data a new
			    ValuesContainer[] argumentVCs = avBase.getClient().retrieveData(
			    				pvs,
			    				request, 
			    				avp);
			    
			    //recalculate the formula vc, because may be the term etc. changed
			    vc = AVBaseUtilities.createFormulaVC(
			    		currentFormulaWithPVArgumentsOnly, 
						argumentVCs
					);
			    if(vc.getNumberOfValues() > 0)
			    {
			    	result.add(vc);
			    }
			    tempF = null;
		    }		    
		}
		
		return result;
	}
	
	//	returns an ArrayList of proper ValuesContainers, may return NULL
	//	if retrievalMethod is NULL, use whatever retrieval method the formula graph specifies
	// 	(also, disregard requestedNrOfValues in such case)
	private static ArrayList retrieveFormulaDataForPlot(
			AVBase avBase,
			RetrievalMethod retrievalMethod,
			int requestedNrOfValues,
			final FormulaGraph[] _formulaGraphs,
			double requestStartTime,
			double requestEndTime,
			AVProgressTask avp
			) throws Exception
	{		
		ArrayList vcsToPlot = new ArrayList();
		ValuesContainer vc = null;
		int i=0;
		
		ArrayList yetToBeCalculatedFormulaGraphs = new ArrayList();
		HashSet formulaNames = new HashSet();
		for(i=0; i<_formulaGraphs.length; i++)
		{
			yetToBeCalculatedFormulaGraphs.add(_formulaGraphs[i]);
			formulaNames.add(_formulaGraphs[i].getAVEntry().getName());
		}
		
		Formula f = null;
		HashMap formulasWithPVArgumentsOnlyMap = new HashMap();
		
		//- if we can not calculate a formula and tempFG is NULL, tempFG is set to this formula
		//- if we remove a formula, tempFG is set to NULL
		//- if at some point current processed formula equals tempFG => 
		// "bad formula" exception
		Formula tempF = null;
		
		while(yetToBeCalculatedFormulaGraphs.isEmpty() == false)
		{
			f = (Formula) yetToBeCalculatedFormulaGraphs.get(0);
			
			if(f == tempF)
			{
				//bad formulas
				StringBuffer sb = new StringBuffer("Bad formulas: ");
				for(i=0; i<yetToBeCalculatedFormulaGraphs.size(); i++)
				{
					f = (Formula) yetToBeCalculatedFormulaGraphs.get(i);
					sb.append(f.getAVEntry().getName());
					sb.append("; ");
				}
				throw new Exception(sb.toString());
			}
			//always remove it => if not able to calculate, add back to list at the end
		    yetToBeCalculatedFormulaGraphs.remove(f);				
		    
		    Formula currentFormulaWithPVArgumentsOnly = f.tryToEliminateFormulaArguments(formulasWithPVArgumentsOnlyMap);
		    
		    if(currentFormulaWithPVArgumentsOnly == null)
		    {
		    	//formula f uses a formula that was not resolved in respect to pvs,
		    	//i.e. contains formula args
		    	//add the formula that we couldn't calculate because it contains other formulas
		    	//at the end of the array list
		    	yetToBeCalculatedFormulaGraphs.add(f);
		    	if(tempF == null)
		    		tempF = f;
		    }
		    else
		    {
			    RequestObject request = new RequestObject();
			    if(retrievalMethod != null)
			    {
			    	//if the chosen plotplugin desires timestamp-aligned data
			    	request.setMethod(retrievalMethod);
			    	request.setRequestedNrOfValues(requestedNrOfValues);
			    }
			    else
			    {
			    	request.setMethod(avBase.getClient().getRetrievalMethod(((FormulaGraph)f).getRetrievalMethodName()));
			    	request.setRequestedNrOfValues(((FormulaGraph)f).getRequestedNumberOfValues());
			    }

			    request.setRange(requestStartTime, requestEndTime);
			    request.setSparsificationOperator(avBase.getSparsificationOperator());
			    request.setExporterID("spreadsheet");
			   	
		    	//add to the map of resolved formulas, i.e. formulas that have no formula arguments
		    	formulasWithPVArgumentsOnlyMap.put(currentFormulaWithPVArgumentsOnly.getAVEntry().getName(), currentFormulaWithPVArgumentsOnly);
		    	AVEntry[] pvs = AVBaseUtilities.getPVsNeededToCalculateFormula(
		    	        currentFormulaWithPVArgumentsOnly, null);	
			    
		    	//retrieve all data a new
			    ValuesContainer[] argumentVCs = avBase.getClient().retrieveData(
			    				pvs,
			    				request, 
			    				avp);
			    
			    
			    //recalculate the formula vc, because may be the term etc. changed
			    vc = AVBaseUtilities.createFormulaVC(
			    		currentFormulaWithPVArgumentsOnly, 
						argumentVCs
					);
			    if(vc.getNumberOfValues() > 0)
			    {
			    	if(((FormulaGraph) f).isVisible())
			    		vcsToPlot.add(vc);
			    }
			    tempF = null;
		    }		    
		    
		}
		
		return vcsToPlot;
	}
	
	//returns an ArrayList of ValuesContainers, may return NULL
	private static ArrayList retrieveRegularPVDataForPlot(
			AVBase avBase,
			RetrievalMethod retrievalMethod,
			int requestedNrOfValues,
			PVGraph[] pvGraphs,
			double requestStartTime,
			double requestEndTime,
			AVProgressTask avp
			) throws Exception
	{
		if(pvGraphs.length == 0)
			return new ArrayList();
		
		RequestObject request = new RequestObject();
		request.setRange(requestStartTime, requestEndTime);
		request.setMethod(retrievalMethod);
		request.setRequestedNrOfValues(requestedNrOfValues);
	    request.setSparsificationOperator(avBase.getSparsificationOperator());
		
		AVEntry[] avEntries = new AVEntry[pvGraphs.length];
		for(int i=0; i<pvGraphs.length; i++)
		{
			avEntries[i] = pvGraphs[i].getAVEntry();
		}
		
		avEntries = avBase.getVCsCache().takeOutFormulasAndCachedAEs(request, avEntries, new HashSet());
		
		ValuesContainer[] vcs = avBase.getClient().retrieveData(avEntries, request, avp);
		
		avBase.getVCsCache().addVCs(request, vcs);

		ArrayList vcsToPlot = new ArrayList();
		
		ValuesContainer vc = null;
		for(int i=0; i<pvGraphs.length; i++)
		{
			if(	pvGraphs[i].isVisible())
			{
				vc = avBase.getVCsCache().getVC(request, pvGraphs[i].getAVEntry());
				if(	vc != null && 
					vc.getNumberOfValues() > 0
				)
				vcsToPlot.add(vc);
			}		
		}
		return vcsToPlot;
	}
	
	public static void connect(
				AVBase avBase,
				String connectionParameter,
				AVProgressTask avp
			) throws Exception
	{
		ClientPlugin client = avBase.getClient();
		client.connect(connectionParameter, avp);
		
		PlotPluginsRepository ppsRepository = avBase.getPlotPluginsRepository();
		for(int i=0; i<ppsRepository.size(); i++)
		{
			ppsRepository.getPlotPlugin(i).setAvailableRetrievalMethods(
					client.getRetrievalMethodsForPlot());
		}
		
	    avBase.getArchiveDirectoriesRepository().setArchiveDirectories(client.getAvailableArchiveDirectories());
	    
		if(avp != null)
			avp.stop();
	}
	
	public static void loadConfiguration(AVBase avBase, File f, AVProgressTask avp) throws Exception
	{
		avBase.getMatchingAVEsRepository().clear();
		avBase.getAxesIntervalsManager().clear();
		
		PlotModel plotModel = avBase.getPlotModel();
		plotModel.clear();
		
		try
		{
			
			avBase.
					getPlotModel().
						loadEverythingButAVEs(f);
			
			UseCases.connect(avBase, avBase.getPlotModel().getConnectionParameter(), avp);
			
			avBase.
					getPlotModel().
						loadAVEs(avBase);
		}
		catch(Exception e)
		{
		    plotModel.clear();
			plotModel.loadInitialAxesSettings();
			throw e;
		}
		
		finally
		{
			plotModel.fireTimeAxesUpdated();
			plotModel.fireRangeAxesUpdated();
			plotModel.fireAVEsUpdated();
			plotModel.fireNewModelLoaded();
			if(avp != null)
				avp.stop();
		}
	}
	
	//does not stop progress task
	public static void search(
			AVBase avBase, 
			String arDirName, 
			String regEx,
			AVProgressTask avp) throws Exception
	{
		AVEntry[] matchingAVEs = 
			avBase.getClient().search(
			        avBase.getArchiveDirectoriesRepository().getArchiveDirectory(arDirName), 
			        regEx, 
			        avp);
		avBase.getMatchingAVEsRepository().addMatchingAVEs(matchingAVEs);
	}
	
	//returns either an Exception, or an Array of non-null ValueContainers
	public static ValuesContainer[] retrieveNecessaryDataForPlot(
			AVBase avBase,
			RetrievalMethod mainRetrievalMethod,
			int requestedNrOfValues,
			AVProgressTask avp) throws Exception
	{
	
        int i,j;
        
		AVEntry[] archiveEntries = avBase.getPlotModel().getAVEntries();
		
		ArrayList vcsToPlot = new ArrayList();

		
    	if(avBase.getVCsCache().getStoredNrValues() > AVBaseConstants.CACHE_SIZE)
    	{
			avBase.getVCsCache().clear();
    	}
    	
    	Graph g = null;
    	TimeAxis tA = null;
    	String[] timeAxisNames = avBase.getPlotModel().getTimeAxesNames();
    	
		for (i = 0; i < timeAxisNames.length; i++) 
		{
			ArrayList pvGraphs = new ArrayList();
			ArrayList formulaGraphs = new ArrayList();
			
			for(j=0; j<archiveEntries.length; j++)
			{
				g = avBase.getPlotModel().getGraph(archiveEntries[j]);
				if(g.getTimeAxisLabel().equals(timeAxisNames[i]))
				{
					if(g instanceof PVGraph)
						pvGraphs.add(g);
					else
						formulaGraphs.add(g);
				}
			}
			
			epics.archiveviewer.base.fundamental.Range r = avBase.getAxesIntervalsManager().getCurrentTimeInterval(
					avBase.getPlotPluginsRepository().getSelectedPlotPlugin(), timeAxisNames[i]);
				
			
			ValuesContainer vc = null;
		
			RetrievalMethod rmForFormulas = null;
			if(mainRetrievalMethod.alignsTimestamps())
				//override whatever stored in formulas
				rmForFormulas = mainRetrievalMethod;
			
			vcsToPlot.addAll(
					retrieveFormulaDataForPlot(
						avBase,
						rmForFormulas,
						AVBaseConstants.DEFAULT_NR_VALUES,
						(FormulaGraph[]) formulaGraphs.toArray(new FormulaGraph[formulaGraphs.size()]),
						r.min.doubleValue(),
						r.max.doubleValue(),
						avp	
					)
			);

			vcsToPlot.addAll
			(
					retrieveRegularPVDataForPlot(
							avBase,
							mainRetrievalMethod,
							requestedNrOfValues,
							(PVGraph[]) pvGraphs.toArray(new PVGraph[pvGraphs.size()]),
							r.min.doubleValue(),
							r.max.doubleValue(),
							avp
					)
			);
		}

		if(avp != null)
			avp.stop();
		
		return (ValuesContainer[]) vcsToPlot.toArray(new ValuesContainer[vcsToPlot.size()]);
	}

	//no caching
	//last modiflied on 8/1/06 by John Lee
	public static void retrieveAndExportData(
			AVBase avBase,
			String exporterId,
			AVProgressTask avp
			) throws Exception
	{	
		int progressValue = 0;
		String progressMessage = ""; 
		
		ArrayList avesList = new ArrayList();	
		String[] pvNames = avBase.getExportModel().getPvNames();
		ArchiveDirectory archiveDir = 
			avBase.getArchiveDirectoriesRepository().getArchiveDirectory(
					avBase.getExportModel().getDirectoryName());
		for(int i=0; i < pvNames.length; i++)
			avesList.add(new AVEntry(pvNames[i], archiveDir));	
		
		Date[] dates = TimeParser.parse(avBase.getExportModel().getStartTime(), avBase.getExportModel().getEndTime());
		final double userStartTimeInMsecs = dates[0].getTime();
		final double userEndTimeInMsecs = dates[1].getTime();
		
		//get all participating archive entries 
		AVEntry[] pvAVEntries = (AVEntry[]) avesList.toArray(new AVEntry[avesList.size()]);
		
		final int requestedTotalNumberOfValuesPerAVE = avBase.getExportModel().getNumberOfValues();
		
		Formula[] formulas = avBase.getExportModel().getFormulas();
		
		//request for a 10000 values per PV at a time, or less => caching parameter
		int nrValuesPerRequest = Math.min(avBase.getExportModel().getCountPerAvePerRetrival(), avBase.getClient().getMaxNrValuesPerPVPerRequest(pvAVEntries.length + formulas.length));
		
		if(nrValuesPerRequest > requestedTotalNumberOfValuesPerAVE)
			nrValuesPerRequest = requestedTotalNumberOfValuesPerAVE;
		
		final RequestObject request = new RequestObject();
		
		RetrievalMethod retrievalMethod = avBase.getClient().getRetrievalMethod(avBase.getExportModel().getMethodName());
		
		request.setMethod(retrievalMethod);
		request.setRequestedNrOfValues(nrValuesPerRequest);

		// For exporting data, use dense (non-sparcified) data.
	    request.setSparsificationOperator(avBase.getSparsificationOperator());
		// Tell the client code what the exporter is so that they can take some special steps
		request.setExporterID(exporterId);
		
		int nrExportedValuesPerAVE = 0;			
		
		boolean forceToEndAfterExportingCurrentlyRetrievedData = false;
		
		boolean firstRun = true;
		
		//unknown yet
		double requestEndTimeInMsecs = -1;
		
		final Writer writer = avBase.getExportModel().getWriter();
		
		final int detailsLevel = 
			avBase.getExportModel().isShowStatus() ? 
					Exporter.EXPORT_DATA_AND_STATUS : Exporter.EXPORT_DATA_ONLY;
		
		final String timestampFormat = avBase.getExportModel().getTimeStampFormat();
		
		double requestStartTimeInMsecs = userStartTimeInMsecs;
		
		//need output file name for exporting
		if (avBase.getExportersRepository().getExporter(exporterId) instanceof MatlabExporter)
			((MatlabExporter)avBase.getExportersRepository().getExporter(exporterId)).setOutputFileName(
					avBase.getExportModel().getWriterFilename());
		//a buffer for matlab exporter to run in 2 threads
		int current_worker = 0;
		ValuesContainer[][] vcs = new ValuesContainer[2][];
		ValuesContainer[][] nonEmptyVCs = new ValuesContainer[2][];
		
		while(	nrExportedValuesPerAVE < requestedTotalNumberOfValuesPerAVE && 
				forceToEndAfterExportingCurrentlyRetrievedData == false){
			if(	nrValuesPerRequest >= requestedTotalNumberOfValuesPerAVE && 
				retrievalMethod.reducesResolution() == true)
			{
				//if method reduces resolution, the user actually specifies a period
				//for which he/she wants a single value
				double periodInMsecs = (userEndTimeInMsecs - userStartTimeInMsecs) / requestedTotalNumberOfValuesPerAVE;
				requestEndTimeInMsecs = periodInMsecs * nrValuesPerRequest + requestStartTimeInMsecs;
			}
			else
				//	just ask for all time range at once
				requestEndTimeInMsecs = userEndTimeInMsecs;
			
			request.setRange(requestStartTimeInMsecs, requestEndTimeInMsecs);
			
			if(avp != null)
				avp.acceptChanges(false);
			vcs[current_worker] = avBase.getClient().retrieveData(
			        					pvAVEntries, 
			        					request, 
			        					avp);
			//modiflied so that only non matlab exporter will export formulas 
			if (!(avBase.getExportersRepository().getExporter(exporterId).getId()).equals("matlab")){
				ArrayList vcsList = new ArrayList();
				for(int i=0; i<vcs[current_worker].length; i++)
					vcsList.add(vcs[current_worker][i]);
				vcsList.addAll(
					retrieveFormulaDataForExport(
							avBase,
							formulas,
							request,
							avp));
				vcsList.toArray(vcs[current_worker] = new ValuesContainer[vcsList.size()]);
			}
			if(avp != null)
				avp.acceptChanges(true);	
			
			//nothing was retrieved
			if(vcs[current_worker] == null || vcs[current_worker].length == 0)
				break;
									
			if(avp != null && avp.interrupted()){
				break;
			}
			
			//modified for creating a non null vc
			Vector nonNullVCsVector = new Vector();
			for (int i = 0; i < vcs[current_worker].length; i++){
				if (vcs[current_worker][i]!= null && vcs[current_worker][i].getNumberOfValues() > 0)
					nonNullVCsVector.add(vcs[current_worker][i]);
			}
			
			if(nonNullVCsVector.isEmpty())
				break;
			
			nonEmptyVCs[current_worker] = new ValuesContainer[nonNullVCsVector.size()];
			nonNullVCsVector.toArray(nonEmptyVCs[current_worker]);
			
			
			int nrRetrievedValuesPerAVE = nonEmptyVCs[current_worker][0].getNumberOfValues();
			
			//look if some samples were already exported (do NOT do it on the first run)
			int indexOfFirstRetrievedValueToBeExported = 0;
			//current start time is the time of the previous sample
			if(firstRun == false){								
				while(nonEmptyVCs[current_worker][0].getTimestampInMsec(indexOfFirstRetrievedValueToBeExported) <= requestStartTimeInMsecs){
					indexOfFirstRetrievedValueToBeExported++;
					if(indexOfFirstRetrievedValueToBeExported == nrRetrievedValuesPerAVE){
						//all retrieved values are already exported
						forceToEndAfterExportingCurrentlyRetrievedData = true;
						break; //this loop
					}
				}
			}
			
			int indexOfLastRetrievedValueToBeExported = nrRetrievedValuesPerAVE - 1;
			
			if(	nrValuesPerRequest > nrRetrievedValuesPerAVE &&
				requestEndTimeInMsecs >= userEndTimeInMsecs)
			{
				//no more values in the desired time period
				//export them all
				forceToEndAfterExportingCurrentlyRetrievedData = true;
			}
			else
			{
				//there are more samples
				//but we might have retrieved more data than user originally requested
				if(nrRetrievedValuesPerAVE - indexOfFirstRetrievedValueToBeExported + nrExportedValuesPerAVE > requestedTotalNumberOfValuesPerAVE)
					indexOfLastRetrievedValueToBeExported = indexOfFirstRetrievedValueToBeExported + (requestedTotalNumberOfValuesPerAVE - nrExportedValuesPerAVE) - 1;
			}
			if(indexOfFirstRetrievedValueToBeExported <= indexOfLastRetrievedValueToBeExported)
				avBase.getExportersRepository().getExporter(exporterId).export(
						nonEmptyVCs[current_worker], 
						indexOfFirstRetrievedValueToBeExported, 
						indexOfLastRetrievedValueToBeExported, 
						writer, 
						!firstRun, 
						detailsLevel, 
						timestampFormat);
			//timestamp of the last currently retrieved value
			requestStartTimeInMsecs = nonEmptyVCs[current_worker][0].getTimestampInMsec(nrRetrievedValuesPerAVE - 1);
			
			firstRun = false;
			
			//to switch buffer
			current_worker = current_worker == 0 ? 1 : 0;
			
			nrExportedValuesPerAVE += 	indexOfLastRetrievedValueToBeExported - 
										indexOfFirstRetrievedValueToBeExported + 
										1;
			
			progressValue = (int) (100 * nrExportedValuesPerAVE / requestedTotalNumberOfValuesPerAVE);
			progressMessage = "Retrieved " + nrExportedValuesPerAVE * (formulas.length + pvAVEntries.length);
			if(	requestedTotalNumberOfValuesPerAVE != Integer.MAX_VALUE && 
				requestedTotalNumberOfValuesPerAVE > 0)
				progressMessage += 	" out of " + requestedTotalNumberOfValuesPerAVE * 
									(formulas.length + pvAVEntries.length);
			progressMessage += " values...";
	
			//keep avp alive for exporter to show extra informations
			if(avp != null)
				avp.setProgressParameters(progressValue >= 100 ? 99 : progressValue, progressMessage);
		}
		// generate matlab file
		if (avBase.getExportersRepository().getExporter(exporterId) instanceof MatlabExporter)
			((MatlabExporter)avBase.getExportersRepository().getExporter(exporterId)).genMatlabFile(avp);

		// clear the buffer
		for (int j = 0; j < 2; j++){
			if (nonEmptyVCs[j] == null)
				continue;
			for(int i=0; i<nonEmptyVCs[j].length; i++)
			    nonEmptyVCs[j][i].clear();
		}
		//to see the last message
		if(avp!=null){
			Thread.sleep(1000);
			avp.stop();
		}
		writer.flush();
		writer.close();
	}
}
