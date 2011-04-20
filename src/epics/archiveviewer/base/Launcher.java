/*
 * Created on Mar 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base;

import epics.archiveviewer.base.util.CommandLineArgsParser;
import epics.archiveviewer.commandline.AVCommandLineApplication;
import epics.archiveviewer.xal.AVXALApplication;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Launcher {
	
	public static void main(String[] args)
	{
		try
		{
			CommandLineArgsParser claParser = new CommandLineArgsParser(args);
			if(claParser.containsOption("nogui"))
			{
				AVCommandLineApplication.launch(claParser);
			}
			else
				AVXALApplication.launch(claParser);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
