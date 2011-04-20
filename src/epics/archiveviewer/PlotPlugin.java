package epics.archiveviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Rectangle2D;

import javax.swing.JPopupMenu;

/**
 * Extend this class to create new plot plugins
 * @author serge
 */
public abstract class PlotPlugin
{	
	/** 
	 * This field is accessed when the user tries to load a new plot plugin;
	 * please override it in the subclass
	 */
	public static final String DESCRIPTION = "your plot plugin description";
	/** the access object to AV base*/
	private final AVBaseFacade avBaseFacade;
	/** the object for image persistence parameters*/
	private final ImagePersistenceBean persistenceParameters;
	
	/**
	 * constructor
	 * @param avbf the av base access object
	 * @param ngpip the image persistence parameters object; may be NULL => draws to screen
	 * @throws Excpetion if, among others, the plugin can not be used to create file images directly
	 */
	public PlotPlugin(AVBaseFacade avbf, ImagePersistenceBean ngpip ) throws Exception
	{
		this.avBaseFacade = avbf;
		this.persistenceParameters = ngpip;
	}
	
	/**
	 * Returns the access object to AV base
	 * @return the access object to AV base
	 */
	public AVBaseFacade getAVBFacade()
	{
		return this.avBaseFacade;
	}
	
	/**
	 * Returns the image persistence parameters object
	 * @return the image persistence parameters object
	 */
	public ImagePersistenceBean getPersistenceParameters()
	{
		return this.persistenceParameters;
	}
	
	/**
	 * Displays a plot image for the specified values containers (main method)
	 * @param nonNullVCs an array of values containers to be plotted; elements must not be NULL
	 * @throws Exception
	 */
	public abstract void displayGraphs(ValuesContainer[] nonNullVCs) throws Exception;

	/**
	 * Returns a Rectangle2D for the part of this PlotPlugin's visual component which the user can zoom into
	 * @return a Rectangle2D for the part of this PlotPlugin's visual component which the user can zoom into
	 */
	public abstract Rectangle2D getZoomablePlotArea();

	/**
	 * Returns a display name of this PlotPlugin
	 * @return a display name of this PlotPlugin
	 */
	public abstract String getName();
	
	/**
	 * Returns the component of this PlotPlugin that should be added to the ArchiveViewer Swing hierarchy
	 * (mouse listeners will be registered to this component)
	 * @return the component of this PlotPlugin that should be added to the ArchiveViewer Swing hierarchy
	 */
	public abstract Component getComponent();
	
	/**
	 * Sets new bounds for the specified domain axis (not necessarily time axis)
	 * @param domainAxisLabel the label of the domain axis; may be NULL if only one domain axis present
	 * @param min the new lower bound 
	 * @param max the new upper bound
	 */
	public abstract void setDomainAxisBounds(String domainAxisLabel, double min, double max);
	
	/**
	 * Sets new bounds for the specified range axis
	 * @param rangeAxisLabel the label of the range axis
	 * @param min the new lower bound 
	 * @param max the new upper bound
	 */
	public abstract void setRangeAxisBounds(String rangeAxisLabel, double min, double max);
	
	/**
	 * Returns true if domain axes are time axes; false otherwise (if true, plot zoom in/scrolling
	 * triggers new data retrieval)
	 * @return true if domain axes are time axes; false otherwise
	 */
	public abstract boolean isDomainTime();
	
	/**
	 * Returns the right-mouse-click menu
	 * @return the right-mouse-click menu
	 */
	public abstract JPopupMenu getRightClickMenu();

	/**
	 * Returns the upper bound of the specified range axis
	 * @param rangeAxisLabel the range axis label
	 * @return the upper bound of the specified range axis
	 * @throws Exception
	 */
	public abstract double getUpperBoundOfRangeAxis(String rangeAxisLabel) throws Exception;

	/**
	 * Returns the lower bound of the specified range axis
	 * @param rangeAxisLabel the range axis label
	 * @return the lower bound of the specified range axis
	 * @throws Exception
	 */
	public abstract double getLowerBoundOfRangeAxis(String rangeAxisLabel) throws Exception;

	/**
	 * Returns an array of domain axes labels (might be different from the time axes specified by
	 * the user); needed by AV base to get information on axes bounds
	 * @return an array of domain axes labels
	 */
	public abstract String[] getDomainAxesLabels();
	
	/**
	 * Returns the upper bound of the specified domain axis
	 * @param domainAxisLabel the label of a domain axis 
	 * @return the upper bound of the specified domain axis
	 * @throws Exception
	 */
	public abstract double getUpperBoundOfDomainAxis(String domainAxisLabel) throws Exception;

	/**
	 * Returns the lower bound of the specified domain axis
	 * @param domainAxisLabel the label of the domain axis
	 * @return the lower bound of the specified domain axis
	 * @throws Exception
	 */
	public abstract double getLowerBoundOfDomainAxis(String domainAxisLabel) throws Exception;

	/**
	 * Returns the value of the specified x axis that corresponds to the specified x coordinate 
	 * in the space of the component returned by getComponent() 
	 * @param xAxisLabel the label of the domain axis
	 * @param xCoordinate the x coordinate in space of the component returned by getComponent()
	 * @return the value of the specified x axis that corresponds to the specified x coordinate 
	 * @throws Exception
	 */
	public abstract double getCorrespondingDomainValue(String xAxisLabel, double xCoordinate) throws Exception;

	/**
	 * Returns the value of the specified range axis that corresponds to the specified y coordinate 
	 * in the space of the component returned by getComponent() 
	 * @param yAxisLabel the label of the range axis
	 * @param yCoordinate the y coordinate in space of the component returned by getComponent()
	 * @return the value of the specified range axis that corresponds to the specified y coordinate 
	 * @throws Exception
	 */
	public abstract double getCorrespondingRangeValue(String yAxisLabel, double yCoordinate) throws Exception;
	
	/**
	 * Sets retrieval methods available from the current server
	 * @param rms an array of available retrieval methods
	 */
	public abstract void setAvailableRetrievalMethods(RetrievalMethod[] rms);
	
	/**
	 * Returns the suitable retrieval method for this PlotPlugin
	 * @return the suitable retrieval method for this PlotPlugin
	 * @throws Exception
	 */
	public abstract RetrievalMethod getChosenRetrievalMethod() throws Exception;
	
	/**
	 * Returns the width of the plot panel
	 * @return the width of the plot panel
	 */
	public abstract int getPlotPanelWidth();
	
	/** Clears the entire chart */
	public abstract void clear();
	
	/**
	 * Depending on the flag, enables or disables anti alias drawing mechanism
	 * @param flag a flag
	 */
	public abstract void setAntiAlias(boolean flag);
	
	/**
	 * Depending on the flag, tells the PlotPlugin to leave (or not) the ignored items
	 * @param flag a flag
	 */
	public abstract void setLeaveIgnoredItems(boolean flag);	
}