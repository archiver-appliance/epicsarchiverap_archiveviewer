/*
 * Created on Feb 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.DrawType;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.TimeAxisLocation;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * Mods:
 *   08-Mar-2010, Bob Hall
 *      Added DEFAULT_PRINTER to specify the default printer for the
 *      "File => Print..." dialog.
 */
public interface AVBaseConstants {
	public static final Color[] AVAILABLE_COLORS = new Color[]
  	{
  			new Color(0xFF0000), new Color(0x0000FF), new Color(0x00FF00),
  			new Color(0xFF00FF), new Color(0x000000), new Color(0x660099), 
  			new Color(0xFF6600), new Color(0x339999), new Color(0x999999), 
  			new Color(0x990033), new Color(0x0099FF), new Color(0x33FFFF), 
  			new Color(0xFF9999), new Color(0x999999), new Color(0xFFFF00), 
  			new Color(0x339966), new Color(0x00FFFF), new Color(0xFFCCCC), 
  			new Color(0xCCFFCC), new Color(0x99FF33), new Color(0xCCCC99),
  	};
	
	public static final Color DEFAULT_PLOT_BACKGROUND = Color.WHITE;
		
	public static final String MORE_BUTTON_LABEL = "...";

	//default values
	public static final int DEFAULT_NR_VALUES = 800;
	public static final String DEFAULT_TIME_AXIS_NAME = "Main Time Axis";
	public static final String DEFAULT_START_TIME = "-1d";
	public static final String DEFAULT_END_TIME = "now";
	public static final String DEFAULT_RANGE_AXIS_NAME = "Main Range Axis"; 
	public static final Double DEFAULT_RANGE_MIN = null;
	public static final Double DEFAULT_RANGE_MAX = null;
	public static final TimeAxisLocation DEFAULT_TIME_AXIS_LOCATION = TimeAxisLocation.BOTTOM;
	public static final RangeAxisLocation DEFAULT_RANGE_AXIS_LOCATION = RangeAxisLocation.LEFT;
	public static final RangeAxisType DEFAULT_RANGE_AXIS_TYPE = RangeAxisType.NORMAL;
	                                     	
	public static final DrawType DEFAULT_DRAW_TYPE = DrawType.STEPS;
	public static final float DEFAULT_DRAW_WIDTH = 1;
	public static final Shape DEFAULT_SHAPE = new Rectangle2D.Double(-3, -3, 6, 6);
	
	public static final int CACHE_SIZE = 400000;
	
	public static final float MIN_DRAW_WIDTH = 1;
	public static final float MAX_DRAW_WIDTH = 11;
	
	// public static final String AV_CLIENT_CLASS_NAME = "epics.archiveviewer.clients.channelarchiver.ArchiverClient";
	// public static final String AV_CLIENT_CLASS_NAME = "epics.archiveviewer.clients.appliancearchiver.RawPBPlugin";
	public static final String AV_CLIENT_CLASS_NAME = "epics.archiveviewer.clients.switcher.SwitchingClient";
	
	public static final String[] AVAILABLE_PLOT_PLUGINS_CLASS_NAMES = 
		new String[]
		           {
					"epics.archiveviewer.plotplugins.JFreeChartCorrelator",
					"epics.archiveviewer.plotplugins.JFreeChartForTimePlots",
					"epics.archiveviewer.plotplugins.JFreeChartForWaveforms"
		           };
	/*
	 * Added a Matlab foreign Exporter, spreadsheet foreign Exporter
	 * Modified on: 8/1/06
	 * John Lee
	 */
	public static final String[] AVAILABLE_FOREIGN_EXPORTER_CLASS_NAMES = 
		new String[]
		           {
					"epics.archiveviewer.base.export.SpreadSheetExporter",
					"epics.archiveviewer.base.export.MatlabExporter"
		           };
	
	public static final Preferences AV_PREFERENCES = Preferences.userNodeForPackage(AVBaseFacade.class);
	public static final String CONNECTION_PREFS_KEY = "Connection_Parameters";
	public static final String PLOT_PLUGINS_TO_LOAD_PREFS_KEY = "Plot_Plugins_To_Load";	
	//in case an array of string must be stored as a preference, this delim
	//is used to generate one string
	public static final String PREFERENCES_VALUES_DELIM = " ";

	/** this is the main date format for producing time <CODE>String</CODE> s */
	public static final DateFormat MAIN_DATE_FORMAT = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss.SSS");
	
	public static final DateFormat SNAPSHOT_FILE_DATE_FORMAT = new SimpleDateFormat(
	"yyyy-MM-dd_HH-mm-ss");

	public static final String APPLICATION_NAME = "ArchiveViewer";

	public static final String APPLICATION_VERSION = "1.2 Alpha";

	public static final String DEFAULT_PRINTER = "physics-lclslog";

}
