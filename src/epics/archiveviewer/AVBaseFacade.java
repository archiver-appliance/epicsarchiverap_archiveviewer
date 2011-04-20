package epics.archiveviewer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Shape;
import java.util.Date;


/**
 * This is the interface for accessing the AV base from plot plugins
 * @author Sergei Chevtsov
 */
public interface AVBaseFacade extends MessageListener
{
	/**
	 * Returns the main ArchiveViewer GUI frame, or null (if ArchiveViewer doesn't run in GUI mode);
	 * the existing frame should be used as owner for dialogs etc.
	 * @return the main ArchiveViewer GUI frame
	 */
	public Frame getMainFrame();

	/**
	 * Returns the selected draw type for the graph of the specified AV entry;
	 * never NULL
	 * @param ave the ArchiveViewer entry (PV or formula)
	 * @return the selected draw type for the graph of the specified AV entry
	 */
	public DrawType getDrawType(AVEntry ave);

	/**
	 * Returns the selected draw width for the graph of the specified AV entry
	 * @param ave the AV entry
	 * @return the draw type for the graph of the specified AV entry
	 */
	public float getDrawWidth(AVEntry ave);
	
	/**
	 * Returns a shape object for scattered values of the graph of the specified AV entry
	 * 
	 * @param ave the AV entry
	 * @return a shape object for scattered values of the graph of the specified AV entry
	 */
	public Shape getShape(AVEntry ave);

	/**
	 * Returns the color for the graph of the specified AV entry
	 * @param ave the AV entry
	 * @return the color for the graph of the specified AV entry
	 */
	public Color getColor(AVEntry ave);

	/**
	 * Returns the label of the time axis for the graph of the specified AV entry
	 * 
	 * @param ave the AV Entry
	 * @return the label of the time axis for the graph of the specified AV entry
	 * @throws Exception
	 */
	public String getTimeAxisLabel(AVEntry ave) throws Exception;

	
	/**
	 * Returns a Date array with two elements; the first element contains the start time
	 * of the specified time axis, second element the end time
	 * @param timeAxisLabel the time axis label
	 * @return a Date array with two elements (start/end time)
	 * @throws Exception
	 */
	public Date[] getTimeAxisBounds(String timeAxisLabel) throws Exception;

	/**
	 * Returns the location of the specified time axis
	 * @param timeAxisLabel the time axis label
	 * @return the location of the specified time axis
	 * @throws Exception
	 */
	public TimeAxisLocation getTimeAxisLocation(String timeAxisLabel) throws Exception;

	/**
	 * Returns the range axis label for the graph of the specified AV entry
	 * @param ave the AV entry
	 * @return the range axis label for the graph of the specified AV entry
	 * @throws Exception
	 */
	public String getRangeAxisLabel(AVEntry ave) throws Exception;
	
	/**
	 * Returns a Double array with two elements; the first element is the lower (min), the second
	 * the upper bound (max) of the specified range axis; value NULL means "whatever it actually is"
	 * @param rangeAxisLabel the range axis label
	 * @return a Double array with two elements (min/max, either NULL permitted)
	 * @throws Exception
	 */
	public Double[] getRangeAxisBounds(String rangeAxisLabel) throws Exception;

	/**
	 * Returns the scale type of the specified range axis
	 * @param rangeAxisLabel the label of the range axis
	 * @return the scale type of the specified range axis
	 * @throws Exception
	 */
	public RangeAxisType getRangeAxisType(String rangeAxisLabel) throws Exception;
	
	/**
	 * Returns the location of the specified range axis
	 * @param rangeAxisLabel the range axis label
	 * @return the location of the specified range axis
	 * @throws Exception
	 */
	public RangeAxisLocation getRangeAxisLocation(String rangeAxisLabel) throws Exception;

	/**
	 * Returns an array of all range axis labels
	 * @return an array of all range axis labels
	 */
	public String[] getRangeAxesLabels();

	/**
	 * Returns an array of all time axis labels
	 * @return an array of all time axis labels
	 */
	public String[] getTimeAxesLabels();
	
	/**
	 * Returns the label of the selected time axis; useful, when the plugin supports one time axis only
	 * @return the label of the selected time axis
	 * @throws Exception
	 */
	public String getSelectedTimeAxisLabel() throws Exception;
	
	/**
	 * Returns information on legend items
	 * @return information on legend items
	 */
	public LegendInfo getLegendInfo();
	
	/**
	 * Returns the title of the plot as desired by the user; never NULL (but might be an empty string)
	 * @return the title of the plot as desired by the user
	 */
	public String getPlotTitle();
	
	/**
	 * Returns the background color for current plot
	 * @return the background color for current plot
	 */
	public Color getPlotBackgroundColor();
}
