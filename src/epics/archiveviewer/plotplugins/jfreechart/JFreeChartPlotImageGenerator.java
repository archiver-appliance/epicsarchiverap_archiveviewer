/*
 * Created on Mar 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;

import epics.archiveviewer.base.util.SwingWorker;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JFreeChartPlotImageGenerator {
	
	private static int getImageWidth(Image image) throws Exception
	{
		if(image instanceof VolatileImage)
			return ((VolatileImage) image).getWidth();
		else if(image instanceof BufferedImage)
			return ((BufferedImage) image).getWidth();
		
		return image.getWidth(null);
	}
	
	private static int getImageHeight(Image image) throws Exception
	{
		if(image instanceof VolatileImage)
			return ((VolatileImage) image).getHeight();
		else if(image instanceof BufferedImage)
			return ((BufferedImage) image).getHeight();
		
		return image.getHeight(null);
	}
	
	//anchor may be NULL
	public static synchronized void generate(
			final Image chartBuffer,	
			final JFreeChart chart,
			final Dimension size,
			final Insets insets,
			final Point2D anchor,
			final ChartRenderingInfo info)
	{        
        if (chart == null)
            return;
        
        Rectangle2D available = new Rectangle2D.Double();
        available.setRect(
            insets.left, insets.top,
            size.getWidth() - insets.left - insets.right,
            size.getHeight() - insets.top - insets.bottom
        );

        SwingWorker swingWorker = new SwingWorker()
		{
        	public Object construct()
        	{
        		try
        		{
	                Graphics2D bufferG2 = (Graphics2D) chartBuffer.getGraphics();
	            	Rectangle2D bufferArea = new Rectangle2D.Double(
	            				0, 
	            				0, 
	            				getImageWidth(chartBuffer), 
	            				getImageHeight(chartBuffer)
	            			);
	
	            	//draw onto the buffer
	                chart.draw(bufferG2, bufferArea, anchor, info);
	                
	        		return new Integer(0);
        		}
        		catch(Exception e)
        		{
        			return null;
        		}
        	}
		};
		swingWorker.start();
		//wait for swing worker to finish drawing
		swingWorker.get();
	}

}
