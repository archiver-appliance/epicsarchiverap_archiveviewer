/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.fundamental.Range;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.xal.controller.AVController;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * This class is a <CODE>MouseListener</CODE> for a plot plugin
 */
public class TimePlotZoomListener extends MouseAdapter implements MouseMotionListener
{
	/** a constant for the smallest drag distance for zoom to be allowed */
	private final static int MINIMUM_DRAG_DISTANCE = 10;
	
	private final AVController avController;
	private final AVBase avBase;
	private final PlotModel plotModel;
	private final PlotPlugin plotPlugin;
	private final Component zoomComponent;

	/** the graphics context */
	private Graphics2D g;
	
	//must not be set before the first click inside the zoom component occurs
	private Rectangle2D zoomablePlotArea;

	/** the location where the dragging started */
	private Point dragStartPoint;

	/**
	 * the rectangle has dragStartPoint as one corner and the current mouse
	 * location as the opposite corner
	 */
	private Rectangle dragRectangle; //can not be null because of some

	/**
	 * Returns true if the specified <CODE>Point</CODE> is inside the
	 * allowed zoom area
	 * 
	 * @see #zoomablePlotArea
	 * @param p
	 *            any point on screen
	 * @return true if the specified <CODE>Point</CODE> is inside the
	 *         allowed zoom area
	 */
	private boolean isPointInsideZoomArea(Point p)
	{
		double dx = p.x - zoomablePlotArea.getX();
		double dy = p.y - zoomablePlotArea.getY();

		if ((dx < 0) || (dx > zoomablePlotArea.getWidth()))
		{
			return false;
		}
		else if ((dy < 0) || (dy > zoomablePlotArea.getHeight()))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Draws a <CODE>Rectangle</CODE> from dragStartPoint to the specified
	 * <CODE>Point</CODE>
	 * 
	 * @param p
	 *            any point inside the visiblePlotArea
	 */
	private void drawDragRectangle(Point p)
	{
		if (dragStartPoint == null)
		{
			return;
		}

		try
		{
			Point topLeftCorner = new Point(
					Math.min(dragStartPoint.x, p.x), Math.min(
							dragStartPoint.y, p.y));
			int width = Math.abs(dragStartPoint.x - p.x);
			int height = Math.abs(p.y - dragStartPoint.y);
			dragRectangle = new Rectangle(topLeftCorner, new Dimension(
					width, height));
			this.g.draw(dragRectangle);
		}
		catch (Exception e)
		{
			//do nothing
		}
	}
	
	/**
	 * Returns true, if the dragRectangle is big enough to allow zoom; false
	 * otherwise
	 * 
	 * @see #MINIMUM_DRAG_DISTANCE
	 * @return true, if the dragRectangle is big enough to allow zoom; false
	 *         otherwise
	 */
	private boolean enoughDragged()
	{
		if (dragRectangle.width < MINIMUM_DRAG_DISTANCE)
		{
			return false;
		}
		else if (dragRectangle.height < MINIMUM_DRAG_DISTANCE)
		{
			return false;
		}

		return true;
	}
	
	/**
	 * Makes all necessary calculations for a succesful zoom in
	 * 
	 * @throws RuntimeException
	 *             if some errors occur
	 */
	private void handleZoomRectangle() throws RuntimeException
	{
		int i = 0;

		String[] timeAxisNames = this.plotPlugin.getDomainAxesLabels();
		HashMap timeAxisNamesAndRanges = new HashMap();
		
		//load new values
		//first handle the time axes
		//plotArea rectangle and dragRectangle coordinates
		double dragLeftX = dragRectangle.getX();
		double dragRightX = dragLeftX + dragRectangle.getWidth();
		
		for (i = 0; i < timeAxisNames.length; i++)
		{
			try
			{
				double newMin = this.plotPlugin.getCorrespondingDomainValue(
						timeAxisNames[i], dragLeftX);
				double newMax = this.plotPlugin.getCorrespondingDomainValue(
						timeAxisNames[i], dragRightX);
				
				//put into axes manager
				timeAxisNamesAndRanges.put(timeAxisNames[i], new Range(newMin, newMax));					
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		//now the range axes
		String[] rangeAxisNames = this.plotModel.getRangeAxesNames();
		HashMap rangeAxisNamesAndRanges = new HashMap();
		//plotArea rectangle and dragRectangle coordinates
		double dragUpperY = dragRectangle.getY();
		double dragLowerY = dragUpperY + dragRectangle.getHeight();

		for (i = 0; i < rangeAxisNames.length; i++)
		{
			try
			{
				double newMin = this.plotPlugin.getCorrespondingRangeValue(
						rangeAxisNames[i], dragLowerY);
				double newMax = this.plotPlugin.getCorrespondingRangeValue(
						rangeAxisNames[i], dragUpperY);
				
				//put into axes manager
				rangeAxisNamesAndRanges.put(rangeAxisNames[i], new Range(newMin, newMax));		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		this.avBase.getAxesIntervalsManager().addIntervals(this.plotPlugin, timeAxisNamesAndRanges, rangeAxisNamesAndRanges);
		this.avController.plot();
	}

	/**
	 * Resets the parameters of this <CODE>ZoomListener</CODE> (e.g. after
	 * the mouse was released)
	 */
	private void reset()
	{
		dragStartPoint = null;
		zoomablePlotArea = null;
		dragRectangle = new Rectangle(0, 0, 0, 0);		
	}

	
	/** Creates a new instance of <CODE>ZoomListener</CODE> */
	public TimePlotZoomListener(AVController avc, PlotPlugin pp) throws Exception
	{
		if(pp.isDomainTime() == false)
			throw new IllegalArgumentException("This listener can be added to plot plugins that have" +
					"time axes as their domains only");
		this.avController = avc;
		this.avBase = this.avController.getAVBase();
		this.plotModel = this.avBase.getPlotModel();
		this.plotPlugin = pp;
		this.zoomComponent = this.plotPlugin.getComponent();
		this.dragRectangle = new Rectangle(0, 0, 0, 0);
	}



	/**
	 * Does nothing
	 * 
	 * @param e
	 *            the mouse event
	 */
	public void mouseClicked(MouseEvent e)
	{
		//do nothing
	}

	/**
	 * If the left mouse button was pressed, retrieve or initialize the
	 * local attribute values
	 * 
	 * @param e
	 *            the mouse event
	 */
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			//set only now
			this.zoomablePlotArea = this.plotPlugin.getZoomablePlotArea();
			this.g = (Graphics2D) e.getComponent().getGraphics();		
		}
	}

	/**
	 * If the left mouse was released, ends the graphical zoom
	 * functionality, calculates the new axes settings and stores the
	 * current settings in the zoomHistoryBox
	 * 
	 * @param e
	 *            the mouse event
	 */
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() != MouseEvent.BUTTON1)
		{
			return;
		}

		//repaints the zoom rectangle, making it invisible
		//the color parameter is taken from ChartPanel.java
		this.g.setXORMode(Color.GRAY);
		this.g.draw(this.dragRectangle);

		if (enoughDragged() == true)
		//zoom in
		{
			try
			{
				handleZoomRectangle();
			}
			catch (Exception ex)
			{
				//do nothing
			}
		}

		reset();
	}

	/**
	 * If the left mouse buttons is pressed and the mouse is dragged, looks
	 * at the drag distance if you can draw the drag rectangle. Possibly,
	 * overpaints the previous drag rectangle first
	 * 
	 * @param e
	 *            mouse event
	 */
	public void mouseDragged(MouseEvent e)
	{
	    if (this.zoomablePlotArea != null)
		{
			Point p = e.getPoint();
			if (isPointInsideZoomArea(p))
			{
				
				if(this.dragStartPoint == null)
					this.dragStartPoint = p;
	
				/**
				 * repaints the current rectangle; Color taken from ChartPanel.java
				 */
				this.g.setXORMode(Color.GRAY);
				this.g.draw(this.dragRectangle);
	
				drawDragRectangle(p);
			}
		}
	}

	/**
	 * Does nothing
	 * 
	 * @param e
	 *            mouse event
	 */
	public void mouseMoved(MouseEvent e)
	{
		//do nothing
	}
}

