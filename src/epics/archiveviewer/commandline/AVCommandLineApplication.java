/*
 * Created on Feb 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.commandline;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.DrawType;
import epics.archiveviewer.ImagePersistenceBean;
import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.UseCases;
import epics.archiveviewer.base.model.ExportModel;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.base.util.CommandLineArgsParser;
import epics.archiveviewer.commandline.listeners.SIOMessageListener;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVCommandLineApplication
{
	public static final int DEFAULT_IMAGE_WIDTH = 1024;
	public static final double DEFAULT_IMAGE_HEIGHT_FACTOR = 0.75;
	
	public static final String[] lineToArgsArray(final String line)
	{
		if(line  == null)
			return null;

		ArrayList al = new ArrayList();
		
		int firstIndexOfNextArgument = 0;
		int lineLength = line.length();
		
		boolean insideQuotes = false;
		String s = null;		
		char c = 0;		
		for(int i=0; i<lineLength; i++)
		{
			c = line.charAt(i);
			if(c == KeyEvent.VK_SPACE && insideQuotes == false)
			{
				//incl, excl.
				s = line.substring(firstIndexOfNextArgument, i);
				if(s.trim().equals("") == false)
				{
					if(s.charAt(0) == '\'' || s.charAt(0) == '"')
						s = s.substring(1, s.length() - 1);
					al.add(s);
				}
				firstIndexOfNextArgument = i+1;
			}
			else if(c == '\'' || c == '"')
				insideQuotes = !insideQuotes;				
		}
		
		s = line.substring(firstIndexOfNextArgument);
		if(s.trim().equals("") == false)
		{
			if(s.charAt(0) == '\'' || s.charAt(0) == '"')
				s = s.substring(1, s.length() - 1);
			al.add(s);
		}
		return (String[]) al.toArray(new String[al.size()]);
	}
	
	private final AVBase avBase;
	private CommandLineArgsParser claParser;
	
	private void printExamplePlotConfiguration() throws Exception
	{
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-16\"?>");
		System.out.println("<AVConfiguration>");
		System.out.println("\t<connection_parameter>http://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi</connection_parameter>");
		System.out.println("\t<time_axis name=\"Main Time Axis\">");
		System.out.println("\t\t<start>02/20/2005 10</start>");
		System.out.println("\t\t<end>+1d</end>");
		System.out.println("\t\t<location>bottom</location>");
		System.out.println("\t</time_axis>");
		System.out.println("\t<range_axis name=\"Main Range Axis\">");
		System.out.println("\t\t<min/>");
		System.out.println("\t\t<max>10</max>");
		System.out.println("\t\t<type>normal</type>");
		System.out.println("\t\t<location>left</location>");
		System.out.println("\t</range_axis>");
		System.out.println("\t<range_axis name=\"2\">");
		System.out.println("\t\t<min>10</min>");
		System.out.println("\t\t<max/>");
		System.out.println("\t\t<type>log</type>");
		System.out.println("\t\t<location>right</location>");
		System.out.println("\t</range_axis>");
		System.out.println("\t<legend_configuration show_ave_name=\"false\" show_directory_name=\"false\" show_range=\"true\" show_units=\"true\"/>");
		System.out.println("\t<plot_title>Plot#1</plot_title>");
		System.out.println("\t<pv directory_name=\"RCCS/Vac (10/01/04 - present)\" name=\"CCL_Cool:FT101:Seg1_Rtn\">");
		System.out.println("\t\t<time_axis_name>Main Time Axis</time_axis_name>");
		System.out.println("\t\t<range_axis_name>Main Range Axis</range_axis_name>");
		System.out.println("\t\t<color>#FF0000</color>");
		System.out.println("\t\t<draw_type>steps</draw_type>");
		System.out.println("\t\t<draw_width>1.0</draw_width>");
		System.out.println("\t\t<visibility>true</visibility>");
		System.out.println("\t</pv>");
		System.out.println("\t<pv directory_name=\"RCCS/Vac (10/01/04 - present)\" name=\"CCL_Cool:FT102:Seg2_Rtn\">");
		System.out.println("\t\t<time_axis_name>Main Time Axis</time_axis_name>");
		System.out.println("\t\t<range_axis_name>2</range_axis_name>");
		System.out.println("\t\t<color>#00FF00</color>");
		System.out.println("\t\t<draw_type>scatter</draw_type>");
		System.out.println("\t\t<draw_width>1.0</draw_width>");
		System.out.println("\t\t<visibility>true</visibility>");
		System.out.println("\t</pv>");		
		System.out.println("</AVConfiguration>");	
		System.out.println();
	}
	
	private void printPlotConfigurationHelp()
	{
		System.out.println();
		System.out.println("Color:");
		System.out.println("---------------------------------");
		System.out.println("An integer number that represents the Red-Green-Blue value of the color;");
		System.out.println("can be hexadecimal (=> HTML color codes, e.g. '#FF0000')");
		System.out.println();
		System.out.println("Draw Type:");
		System.out.println("---------------------------------");
		System.out.println(DrawType.LINES.toString());
		System.out.println(DrawType.STEPS.toString());
		System.out.println(DrawType.SCATTER.toString());
		System.out.println();
		System.out.println("Time Axis Location:");
		System.out.println("---------------------------------");
		System.out.println(TimeAxisLocation.BOTTOM.toString());
		System.out.println(TimeAxisLocation.TOP.toString());
		System.out.println(TimeAxisLocation.NOT_VISIBLE.toString() + " (empty string means 'not visible')");
		System.out.println();
		System.out.println("Range Axis Location:");
		System.out.println("---------------------------------");
		System.out.println(RangeAxisLocation.LEFT.toString());
		System.out.println(RangeAxisLocation.RIGHT.toString());
		System.out.println(RangeAxisLocation.NOT_VISIBLE.toString() + " (empty string means 'not visible')");
		System.out.println();
		System.out.println("Miscellaneous:");
		System.out.println("---------------------------------");
		System.out.println("If for a PV/formula a range axis is not specified, it is plotted normalized");
		System.out.println("If min/max of a range axis is not specified, it is considered 'whatever it is'");
		System.out.println();
	}
	
	private void printExportHelp()
	{
		System.out.println();
		System.out.println("<list_of_pv_names>");
		System.out.println("-------------------------------------------");
		System.out.println("PV names must be separated by empty spaces, the entire list must be in quotes,");
		System.out.println("e.g. \"PV_1 PV_2 PV_3\"");
		System.out.println();
		System.out.println("<start_time>, <end_time>");
		System.out.println("-------------------------------------------");
		System.out.println("Must be quoted; can be both, relative or absolute time strings, e.g.");
		System.out.println("-1y +2M -3d +10H -7m +6s");
		System.out.println("           or");
		System.out.println("01/01[/2004 [18:[00:[00]]]]");
		System.out.println("For <end_time>, 'now' is allowed.");
		System.out.println();
		System.out.println("<max_count_per_pv>");
		System.out.println("-------------------------------------------");
		System.out.println("The maximum number of values to be retrieved per PV");
		System.out.println();
		System.out.println("[-no_status]");
		System.out.println("-------------------------------------------");
		System.out.println("If missing (default), the status and severity of each data sample is retrieved ");
		System.out.println("as well; if present, only actual data samples are retrieved");
		System.out.println();
		System.out.println("[-method <method_name>]");
		System.out.println("-------------------------------------------");
		System.out.println("You may specify an export method name (if you don't, then watever ");
		System.out.println("the server considers default will be selected; to print supported method names,");
		System.out.println("use the '-print_export_methods' command)");
		System.out.println();
		System.out.println("[-ts_format <time_stamp_format>]");
		System.out.println("-------------------------------------------");
		System.out.println("Use this option to specify your own time stamp format (e.g. 'dd/MM/yyyy HH:mm.ss')");
		System.out.println();
		System.out.println("[-id <exporter_id>]");
		System.out.println("-------------------------------------------");
		System.out.println("Use this option to specify a particular exporter (type '-print_exporter_ids' to ");
		System.out.println("print registered exporters)");
		System.out.println();
	}
	
	private void generateImage(String imageFilePath, int width, int height) throws Exception
	{
		int i=0;
		System.out.println();
		System.out.println("Retrieving data...");
		
		AVEntry[] aves = this.avBase.getPlotModel().getAVEntries();			
		
		PlotPlugin plotPlugin = 
			AVBaseUtilities.loadPlotPlugin(
					this.avBase, 
					null, 
					"epics.archiveviewer.plotplugins.JFreeChartForTimePlots",
					new ImagePersistenceBean
							(
								new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB),
								epics.archiveviewer.ImageFileFormat.PNG,
								new File(imageFilePath)
							)
			);
		plotPlugin.setAvailableRetrievalMethods(this.avBase.getClient().getRetrievalMethodsForPlot());
		
		AVBaseUtilities.copyAxesRangesFromPlotModelIntoAxesManager(this.avBase);
		
		ValuesContainer[] vcs = UseCases.retrieveNecessaryDataForPlot(
				this.avBase,
				plotPlugin.getChosenRetrievalMethod(),
				width,
				null);
	
		System.out.println("Plotting...");
		plotPlugin.displayGraphs(vcs);
		System.out.println("Image generated");
		System.out.println();
	}

	private void handleExport(CommandLineArgsParser claParser) throws Exception
	{
		System.out.println();
		System.out.println("Retrieving data...");
		String[] params = claParser.getParameters("export");
		ArrayList al = new ArrayList();
		StringTokenizer st = new StringTokenizer(params[0], " ");
		while(st.hasMoreTokens())
		{
			al.add(st.nextToken());
		}
		String[] pvNames = (String[]) al.toArray(new String[al.size()]);
		String dirName = params[1];
		String startTime = claParser.getParameters("start")[0];
		String endTime = claParser.getParameters("end")[0];
		int nrOfValues = Integer.parseInt(claParser.getParameters("max_count_per_pv")[0]);
		
		boolean exportStatus = !claParser.containsOption("no_status");
		String methodName = null;
		if(claParser.containsOption("method"))
		{
			methodName = claParser.getParameters("method")[0];
		}
		else
		{
			methodName = avBase.getClient().getRetrievalMethodsForExport()[0].getName();
		}
		String tsFormat = null;
		if(claParser.containsOption("ts_format"))
			tsFormat = claParser.getParameters("ts_format")[0];
		
		String exporterId = null;
		if(claParser.containsOption("id"))
		{
			exporterId  = claParser.getParameters("id")[0];
		}
		else
			exporterId = this.avBase.getExportersRepository().getRegisteredIds()[0];
		
		ExportModel exportModel = new ExportModel(
				avBase.getClient().getConnectionParameter(),
				dirName,
				pvNames,
				nrOfValues,
				startTime,
				endTime,
				methodName,
				tsFormat,
				exportStatus,
				new PrintWriter(System.out)
				);
		
		this.avBase.setExportModel(exportModel);
		UseCases.retrieveAndExportData(
				this.avBase, 
				exporterId,
				null
				);
		System.out.println("Export finished");
		System.out.println();
	}
	
	private void execute(CommandLineArgsParser claParser) throws Exception
	{		
		String[] params = null;
		if(claParser.containsOption("u"))
		{
			System.out.println();
			System.out.println("Connecting...");
			params = claParser.getParameters("u");
			UseCases.connect(this.avBase, params[0], null);
			System.out.println("Connected");
			System.out.println();
		}
		if(claParser.containsOption("print_dirs"))
		{
			System.out.println();
			System.out.println("Retrieving archive directories...");
			String[] arDirNames = this.avBase.getArchiveDirectoriesRepository().getSortedArchiveDirectoryNames();
			for(int i=0; i<arDirNames.length; i++)
			{
				System.out.println(arDirNames[i]);
			}
			System.out.println("Archive directories printed");
			System.out.println();
		}
		if(claParser.containsOption("find_pvs"))
		{
			params = claParser.getParameters("find_pvs");
			System.out.println();
			System.out.println("Searching for PVs...");
			AVEntry[] aves = this.avBase.getClient().search(
					this.avBase.getArchiveDirectoriesRepository().getArchiveDirectory(params[0].trim()), 
					AVBaseUtilities.convertGlobToRegular(params[1], false),
					null);
			for(int i=0; i<aves.length; i++)
			{
				System.out.println(aves[i].getName());
			}
			System.out.println("Search finished");
			System.out.println();
		}
		if(claParser.containsOption("print_plot_cfg_example"))
		{
			printExamplePlotConfiguration();
		}
		if(claParser.containsOption("print_plot_cfg_help"))
		{
			printPlotConfigurationHelp();
		}
		if(claParser.containsOption("f"))
		{
			System.out.println();
			System.out.println("Loading plot configuration...");
			params = claParser.getParameters("f");
			UseCases.loadConfiguration(this.avBase, new File(params[0]), null);
			System.out.println("Plot configuration loaded");
			System.out.println();
		}
		if(claParser.containsOption("image"))
		{
			params = claParser.getParameters("image");
			switch(params.length)
			{
				case 3:
					generateImage(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
					break;
				case 2:
					int width = Integer.parseInt(params[1]);
					generateImage(params[0], width, (int) (width * DEFAULT_IMAGE_HEIGHT_FACTOR));
					break;
				case 1:
					generateImage(params[0], DEFAULT_IMAGE_WIDTH, (int) (DEFAULT_IMAGE_WIDTH * DEFAULT_IMAGE_HEIGHT_FACTOR));
			}			
		}
		if(claParser.containsOption("print_export_help"))
		{
			printExportHelp();
		}
		if(claParser.containsOption("print_export_methods"))
		{
			System.out.println();
			System.out.println("Retrieving export methods...");
			RetrievalMethod[] methods = this.avBase.getClient().getRetrievalMethodsForExport();
			for(int i=0; i<methods.length; i++)
			{
				System.out.println(methods[i].getName());
			}
			System.out.println("Export methods retrieved");
			System.out.println();
		}	
		if(claParser.containsOption("print_exporter_ids"))
		{
			System.out.println();
			String[] registeredIds = this.avBase.getExportersRepository().getRegisteredIds();
			for(int i=0; i<registeredIds.length; i++)
			{
				System.out.println(registeredIds[i]);
			}
			System.out.println();
		}
		if(claParser.containsOption("export"))
			handleExport(claParser);
	}

	
	private void doListenToSystemIn()
	{
		BufferedReader d  = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		while(true)
		{
			if(this.claParser.containsOption("help"))
			{
				CommandLineArgsParser.printHelp();
			}
			else if(this.claParser.containsOption("version"))
			{
				CommandLineArgsParser.printVersion();
			}
			else
			{
				
				// TODO Auto-generated method stub
				try
				{
					execute(claParser);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					claParser.clear();
				}
			}
			
			if(this.claParser.containsOption("quit"))
				break;
			
			try
			{
				System.out.print(">");
				this.claParser = new CommandLineArgsParser(lineToArgsArray(d.readLine()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				this.claParser.clear();
			}		
		}
		System.out.println("Goodbye!");
	}
	
	public AVCommandLineApplication(CommandLineArgsParser initialClaParser) throws Exception
	{
		this.avBase = new AVBase();
		this.avBase.addMessageListener(new SIOMessageListener());
		
		this.claParser = initialClaParser;
		
		doListenToSystemIn();
	}

	public static void launch(final CommandLineArgsParser _claParser) throws Exception
	{
		System.out.println();
		System.out.println("*************************************************************");
		System.out.println("*                                                           *");
		System.out.println("*    THE COMMAND LINE SCRIPT MODE                           *");
		System.out.println("*    -------------------------------------------------------*");
	    System.out.println("*    For help, type '-h(elp)'; to quit type '-(quit)'.      *");
	    System.out.println("*                                                           *");
		System.out.println("*    Otherwise, use command line options to (gradually)     *");
		System.out.println("*    assemble a desired task.                               *");
		System.out.println("*    Following procedure is recommended:                    *");
		System.out.println("*    1. Connect to a server, using the '-u' option          *");
		System.out.println("*    2. Print available archive directories => '-print_dirs'*");
		System.out.println("*    3. Search for desired PVs using '-find_pvs'            *");
		System.out.println("*    4. To plot:                                            *");
		System.out.println("*       Save the plot configuration example to a file       *");
		System.out.println("*                => '-print_plot_config_example > file'     *");
		System.out.println("*       Edit the file to reflect your personal settings     *");
		System.out.println("*                => use '-print_plot_config_help', if help  *");
		System.out.println("*                needed                                     *");
		System.out.println("*       Load a plot config file, using '-f'                 *");
		System.out.println("*       Generate image, using '-image <file>'               *");
		System.out.println("*    5. To export:                                          *");
		System.out.println("*       Print available exporter ids                        *");
		System.out.println("*                => '-print_exporter_ids'                   *");
		System.out.println("*       Print available retrieval methods                   *");
		System.out.println("*                => '-print_export_methods'                 *");
		System.out.println("*       Use the '-export' option to retrieve data           *");
		System.out.println("*                => data will be written to system output   *");
		System.out.println("*                                                           *");
		System.out.println("*************************************************************");
		System.out.println();
		
		new AVCommandLineApplication(_claParser);
	}
}
