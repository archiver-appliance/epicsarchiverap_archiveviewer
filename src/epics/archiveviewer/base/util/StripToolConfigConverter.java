/*
 * Created on Apr 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Properties;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.ArchiveDirectory;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.UseCases;
import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.base.model.PlotModel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StripToolConfigConverter {
	public static void main(String[] args)
	{
		if(args == null || args.length == 0 || args[0] == null || args[0].startsWith("-h"))
		{
			System.out.println("------------------------------------");
			System.out.println("first arg: url of the archive server");
			System.out.println("second arg: key/name of the archive ");
			System.out.println("third arg: input directory          ");
			System.out.println("fourth arg: output directory        ");
			System.out.println("------------------------------------");
			return;
		}
		try
		{
			int i=0;
			int j=0;
			AVBase avBase = new AVBase();
			UseCases.connect(avBase, args[0], null);
			
			ArchiveDirectory archiveDirectory = null;
			
			try
			{
				Integer passedKey = Integer.valueOf(args[1]);
				//key was passed
				String[] allArchiveDirNames = avBase.getArchiveDirectoriesRepository().getSortedArchiveDirectoryNames();
				ArchiveDirectory ad = null;
				for(i=0; i<allArchiveDirNames.length; i++)
				{
					ad = avBase.getArchiveDirectoriesRepository().getArchiveDirectory(allArchiveDirNames[i]);
					if(ad.getIDKey().equals(passedKey))
					{
						archiveDirectory = ad;
						break;
					}
				}
			}
			catch(Exception e)
			{
				//name was passed
				archiveDirectory = avBase.getArchiveDirectoriesRepository().getArchiveDirectory(args[1]);				
			}
			
			File inputDirectory = new File(args[2]);
			//check the directory for .stp files
			if(inputDirectory.isDirectory() == false)
				throw new Exception("No input directory specified");
			
			File[] stripToolFiles = inputDirectory.listFiles(
					new FilenameFilter()
						{
							public boolean accept(File dir, String name) {
								return name.endsWith(".stp");
							}
						});
			
			String s = null;
			String fileName = null;
			
			PlotModel plotModel = new PlotModel();
			plotModel.setConnectionParameter(args[0]);
			
			for(i=0; i<stripToolFiles.length; i++)
			{
				s = stripToolFiles[i].getName();
				//incl., excl. 
				//e.g. CM19_Pres_Lvl.stp, length = 17
				//0,13 => CM19_Pres_Lvl
				fileName = s.substring(0, s.length()-4);
				
				Properties properties = new Properties();
				properties.load(new FileInputStream(stripToolFiles[i]));
				
				ArrayList pvNames = new ArrayList();
				j=0;
				//no endless loop
				while(j<1000)
				{
					s = properties.getProperty("Strip.Curve." + j + ".Name");
					if(s == null)
						break;
					pvNames.add(s);
					j++;
				}
				
				AVEntry ave = null;
				PVGraph pvg = null;
				for(j=0; j<pvNames.size(); j++)
				{
					ave = new AVEntry((String)pvNames.get(j), archiveDirectory);
					pvg = plotModel.createNewPVGraph(ave);
					plotModel.addGraph(pvg);
				}
				FileWriter fw = new FileWriter(args[3] + System.getProperty("file.separator") + fileName + ".cfg");
				//closes the writer
				plotModel.serialize(fw);
				plotModel.clear();
				plotModel.loadInitialAxesSettings();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
