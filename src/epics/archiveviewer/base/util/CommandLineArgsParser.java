/*
 * Created on Feb 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import epics.archiveviewer.base.AVBaseConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommandLineArgsParser {
	
	/**
	 * -nogui
	 * -u url
	 * -f file
	 * -plot
	 * -window_size [width [height]]
	 * -a archive
	 * -d home_dir
	 * -e export_home_dir
	 * -snapshot_dir snapshot_dir TODO
	 * -print_dirs
	 * -print_plot_cfg_example
	 * -print_plot_cfg_help
	 * -image file [width [height]]
	 * -find_pvs <dir_name> <glob_ex>
	 * -print_export_help
	 * -print_export_methods
	 * -print_exporter_ids
	 * -export
	 * -start
	 * -end
	 * -max_count_per_pv
	 * -no_status
	 * -method
	 * -ts_format
	 * -id
	 * -q(uit)
	 * -v(ersion)
	 * -h(elp)
	 */
	
	private final HashMap argsAndValsAsStringArrays;
	
	/** Prints the help information (e.g. if the user specified wrong options) */
	public static void printHelp()
	{
		System.out.println();
		System.out.println(	"java -jar archiveviewer_base.jar [-nogui] [-options]");
		System.out.println();
		System.out.println(	"-u <param>");
		System.out.println(	"       Connects to the archive data server at the specified parameter (e.g. url)");
		System.out.println();
		System.out.println(	"-f <plot_config_file> [-plot]");
		System.out.println(	"       Loads a plot configuration file and, if specified, plots immediately");
		System.out.println();
		System.out.println(	"-window_size [width [height]]");
		System.out.println(	"       Sets the window size");
		System.out.println();
		System.out.println(	"-a <name>");
		System.out.println(	"       Selects the specified archive directory ('gui'-mode only)");
		System.out.println();
		System.out.println(	"-d <absolute_path>");
		System.out.println(	"       Sets the home directory for config files ('gui'-mode only");
		System.out.println();
		System.out.println(	"-snapshot_dir <snapshot_dir>");
		System.out.println(	"       Sets the snapshot directory");
		System.out.println();
		System.out.println(	"-nogui");
		System.out.println(	"       Enters the ArchiveViewer command line ('nogui') mode");
		System.out.println();
		System.out.println( "************************************************************************************");
		System.out.println( "*                                                                                  *");
		System.out.println( "*      'nogui'-mode lets you execute commands one after another (similar to        *");
		System.out.println( "*      a scripting mode); however you can also put more than one command on a line *");
		System.out.println( "*      which then will be executed in a logical order.                             *");
		System.out.println( "*      Note:                                                                       *");
		System.out.println( "*      When running in 'nogui'-mode, you will have to type the command '-q(uit)'   *");
		System.out.println( "*      to exit the aplication. Otherwise, it will be waiting for your input.       *");
		System.out.println( "*                                                                                  *");  	                                                                    
		System.out.println( "************************************************************************************");
		System.out.println();
		System.out.println(	"-print_dirs");
		System.out.println(	"       Prints archive directories that are known by the previously specified server");
		System.out.println(	"       ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-find_pvs <dir_name> <glob_ex>");
		System.out.println(	"       Prints PVs from the specified archive directory whose names match the");
		System.out.println(	"       specified glob expression ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-print_plot_cfg_example");
		System.out.println(	"       Prints an example plot configuration ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-print_plot_cfg_help");
		System.out.println(	"       Prints help for the plot configuration file ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-image <image file> [<width> [<height>]]");
		System.out.println(	"       Creates a plot image using the previously loaded plot configuration");
		System.out.println(	"       ('nogui'-mode only)");
		System.out.println();
		System.out.println( "-export ");
		System.out.println( "<list_of_pvs> <dir_name>  -start <time> -end <time> -max_count_per_pv <nr>]");
		System.out.println(	"[-no_status] [-method <method_name>]");
		System.out.println(	"[-ts_format <time_stamp_format>] [-id <exporter_id>]");
		System.out.println( "        Retieves data of listed PVs from specified archive directory from the ");
		System.out.println(	"        specified time range and prints it to the system output ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-print_export_help");
		System.out.println(	"        Prints help for export options ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-print_export_methods");
		System.out.println(	"        Prints export method names supported by the current server ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-print_exporter_ids");
		System.out.println(	"        Prints ids of registered exporters ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-q(uit)");
		System.out.println(	"        Quits the ArchiveViewer ('nogui'-mode only)");
		System.out.println();
		System.out.println(	"-v(ersion)");
		System.out.println(	"        Prints the version info");
		System.out.println();
		System.out.println(	"-h(elp)");
		System.out.println(	"        Prints this help");
		System.out.println();
	}
	
	public static void printVersion()
	{
		System.out.println();
		System.out.println(AVBaseConstants.APPLICATION_NAME);
		System.out.println(AVBaseConstants.APPLICATION_VERSION);
		System.out.println();
	}
	
	private void checkArgumentsForValidity(HashMap m) throws Exception
	{
		Iterator it = m.keySet().iterator();
		String key = null;
		String[] value = null;
		while(it.hasNext())
		{
			key = it.next().toString();
			value = (String[]) m.get(key);
			
			if(
				key.equals("nogui") == false &&
				key.equals("u") == false &&
				key.equals("f") == false &&
				key.equals("plot") == false &&
				key.equals("window_size") == false &&
				key.equals("a") == false &&
				key.equals("d") == false &&
				key.equals("e") == false &&
				key.equals("snapshot_dir") == false &&
				key.equals("print_dirs") == false &&
				key.equals("print_plot_cfg_example") == false &&
				key.equals("print_plot_cfg_help") == false &&
				key.equals("image") == false &&
				key.equals("find_pvs") == false &&
				key.equals("print_export_help") == false &&
				key.equals("print_export_methods") == false &&
				key.equals("print_exporter_ids") == false &&
				key.equals("export") == false &&
				key.equals("start") == false &&
				key.equals("end") == false &&
				key.equals("no_status") == false &&
				key.equals("method") == false &&
				key.equals("max_count_per_pv") == false &&
				key.equals("ts_format") == false &&
				key.equals("id") == false &&
				key.equals("quit") == false &&
				key.equals("version") == false &&
				key.equals("help") == false)
			{
				throw new IllegalArgumentException("Unknown command: <" + key + ">");
			}
			
			if(key.equals("u"))
			{
				if(value.length < 1)
					throw new IllegalArgumentException("Connection parameter is missing");
			}
			else if(key.equals("find_pvs"))
			{
				if(	value.length < 2)
					throw new IllegalArgumentException("The 'find PVs' option is used wrongfully");
			}
			else if(key.equals("f"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Archive name or key paramter is missing");
			}
			else if(key.equals("window_size"))
			{
				if(	value.length == 0)
					throw new IllegalArgumentException("Window size parameters are missing");
					
			}
			else if(key.equals("d"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Home directory parameter is missing");
			}
			else if(key.equals("e"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Home directory parameter is missing");
			}
			else if(key.equals("snapshot_dir"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Snapshot directory parameter is missing");
			}
			else if(key.equals("image"))
			{
				if(	value.length == 0)
					throw new IllegalArgumentException("Image parameters are missing");
			}
			else if(key.equals("export"))
			{
				if(	
						m.containsKey("max_count_per_pv") == false ||
						m.containsKey("start") == false ||
						m.containsKey("end") == false
					)
					throw new IllegalArgumentException("Export command used incorrectly (commands missing)");
				if(	value.length < 2)
					throw new IllegalArgumentException("Export parameters are missing");
			}
			else if(key.equals("start"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Start time parameter is missing");
			}
			else if(key.equals("end"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("End time parameter is missing");
			}
			else if(key.equals("max_count_per_pv"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Max count parameter is missing");
			}
			else if(key.equals("method"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Method parameter is missing");
			}
			else if(key.equals("ts_format"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Timestamp format parameter is missing");
			}
			else if(key.equals("id"))
			{
				if(	value.length < 1)
					throw new IllegalArgumentException("Exporter id parameter is missing");
			}
		}
	}
	
	private void parseArguments(String[] args) throws Exception
	{
		if(args == null)
			return;
		//replace v with version and h with help
		String currentArg = null;
		String key = null;
		ArrayList values = new ArrayList();
		for(int i=0; i<args.length; i++)
		{
			currentArg = args[i];
			if(currentArg.startsWith("-"))
			{
				try
				{
					//see if it's a negative number
					//inclusive exclusive
					Integer.parseInt(currentArg.substring(1,2));
					//if it's a number, treat it as a parameter
				}
				catch(Exception e)
				{
					//it's not a number
					//eliminate the '-'
					currentArg = currentArg.substring(1);
					//put previous key in the map, except for the first time
					if(key != null)
					{
						this.argsAndValsAsStringArrays.put(key, values.toArray(new String[values.size()]));
						values = new ArrayList();
					}
					if(currentArg.equals("q"))
						currentArg = "quit";
					if(currentArg.equals("h"))
						currentArg = "help";
					if(currentArg.equals("v"))
						currentArg = "version";
					key = currentArg;
					continue;
				}
			}
			values.add(currentArg);
		}
		if(key!=null)
			this.argsAndValsAsStringArrays.put(key, values.toArray(new String[values.size()]));
		checkArgumentsForValidity(argsAndValsAsStringArrays);
	}
	
	public CommandLineArgsParser(String[] args) throws Exception
	{
		this.argsAndValsAsStringArrays = new HashMap();
		parseArguments(args);
	}	
	
	public String[] getParameters(String keyOption)
	{
		return (String[]) this.argsAndValsAsStringArrays.get(keyOption);
	}
	
	public boolean containsOption(String keyOption)
	{
		return this.argsAndValsAsStringArrays.containsKey(keyOption);
	}
	
	public void clear()
	{
		this.argsAndValsAsStringArrays.clear();
	}
}
