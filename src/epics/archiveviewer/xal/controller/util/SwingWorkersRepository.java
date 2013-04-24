/*
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.UseCases;
import epics.archiveviewer.base.util.AVProgressTask;
import epics.archiveviewer.base.util.SwingWorker;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SwingWorkersRepository {
	
	public static void startConnectWorker(
	        final AVBase avBase, final AVProgressTask avp)
	{
		avBase.displayInformation("Connecting...");
	    SwingWorker sw = new SwingWorker()
		{
			public Object construct() {
				try
				{
				    UseCases.connect(avBase, avBase.getPlotModel().getConnectionParameter(), avp);
				    if(avp != null && avp.interrupted())
				    {
				    	avBase.displayWarning("Interrupted", new InterruptedException());
				    }
				    else
				    	avBase.displayInformation("");
				}
				catch(Exception e)
				{
				    avBase.displayError("Can't connect", e);
				}
				return null;
			}
		};
		sw.start();
	}
	
	public static void startReconnectWorker(
			final AVBase avBase, final AVProgressTask avp)
	{
		avBase.displayInformation("Reconnecting...");
	    SwingWorker sw = new SwingWorker()
		{
			public Object construct() {
				try
				{
				    avBase.getClient().reconnect(avp);
				    if(avp != null && avp.interrupted())
				    {
				    	avBase.displayWarning("Interrupted", new InterruptedException());
				    }
				    else
				    	avBase.displayInformation("");
				}
				catch(Exception e)
				{
				    avBase.displayError("Can't reconnect", e);
				}
				return null;
			}
		};
		sw.start();
	}
	        
	
	public static void startSearchWorker(
			final AVBase avBase,
			final String[] adNames,
			final String regEx,
			final AVProgressTask avp
			)
	{
		avBase.displayInformation("Searching...");
		SwingWorker sw = new SwingWorker()
		{
			public Object construct() {
				for(int i=0; i<adNames.length; i++)
				{
					if(adNames.length > 1)
					{
						avBase.displayInformation("Searching in " + adNames[i] + " ...");
					}
					try
					{
						UseCases.search(avBase, adNames[i], regEx, avp);
					}
					catch(Exception e)
					{
						avBase.displayError("Search error", e);
					}
					if(	avp!= null && avp.interrupted())
					{
						avBase.displayWarning("Interrupted", new InterruptedException());
						return null;
					}	
				}
				avBase.displayInformation("");
				if(avp != null)
					avp.stop();
				return null;
			}
		};
		sw.start();
	}
	
	public static void startPlotWorker(
			final AVBase avBase, final AVProgressTask avp)
	{
		avBase.displayInformation("Retrieving data for plot...");
		SwingWorker sw = new SwingWorker()
		{
			public Object construct() {
				PlotPlugin selectedPlotPlugin = avBase.getPlotPluginsRepository().getSelectedPlotPlugin();
				try
				{
					ValuesContainer[] vcs = 	
						UseCases.retrieveNecessaryDataForPlot(
							avBase,
							selectedPlotPlugin.getChosenRetrievalMethod(),
							selectedPlotPlugin.getPlotPanelWidth(),
							avp
							);
					
					if(avp!= null && avp.interrupted())
					{
						avBase.displayWarning("Interrupted", new InterruptedException());
						return null;
					}
					
					selectedPlotPlugin.displayGraphs(vcs);
					
					if(avp!= null && avp.interrupted())
					{
						avBase.displayWarning("Interrupted", new InterruptedException());
						return null;
					}
					avBase.displayInformation("");
						
				}
				catch(Exception e)
				{
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					PrintWriter out = new PrintWriter(bos);
					e.printStackTrace(out);
					out.close();
					avBase.displayError("Can't plot\n" + bos.toString(), e);
				}
				return null;
			}
		};
		sw.start();		
	}
	
	public static SwingWorker startExportWorker(
			final AVBase avBase, 
			final String exporterId, 
			final boolean displayWarningIfInterrupted,
			final AVProgressTask avp)
	{
		avBase.displayInformation("Exporting...");
		SwingWorker sw = new SwingWorker()
		{
			public Object construct() {
				try
				{
					UseCases.retrieveAndExportData(avBase, exporterId, avp);
					if(avp!= null && avp.interrupted())
					{
						if(displayWarningIfInterrupted)
							avBase.displayWarning("Interrupted", new InterruptedException());
						return null;
					}
					avBase.displayInformation("");
				}
				catch(Exception e)
				{
					avBase.displayError("Can't export", e);
					e.printStackTrace(System.err);
				}
				return null;
			}
		};
		sw.start();		
		return sw;
	}
	
	public static void startLoadFileWorker(final AVBase avBase, final File f, final AVProgressTask avp)
	{
		avBase.displayInformation("Loading file...");
		SwingWorker sw = new SwingWorker()
		{
			public Object construct() {
				try
		    	{
					UseCases.loadConfiguration(avBase, f, avp);
		    		avBase.displayInformation("");
		    	}
		    	catch(Exception e)
		    	{
		    		avBase.displayError("Can't load the file", e);
		    	}
		    	return null;
			}
		};
		sw.start();
	}

}
