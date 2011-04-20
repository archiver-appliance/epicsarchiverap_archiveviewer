/*
 * Created on Oct 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Icons {	
	public static final BufferedImage LEFT_ARROW;
	
	public static final BufferedImage RIGHT_ARROW;
	
	public static final BufferedImage UP_ARROW;
	
	public static final BufferedImage DOWN_ARROW;
	
	public static final BufferedImage ZOOM_OUT_HORIZONTAL_IMAGE;

	public static final BufferedImage ZOOM_OUT_VERTICAL_IMAGE;
	
	public static final BufferedImage ZOOM_IN_HORIZONTAL_IMAGE;
	
	public static final BufferedImage ZOOM_IN_VERTICAL_IMAGE;
	
	public static final BufferedImage DOCK_IMAGE;
	
	public static final BufferedImage UNDOCK_IMAGE;
	
	public static final BufferedImage PLAY_IMAGE;
	
	public static final BufferedImage STOP_IMAGE;
	
	//returns i.createGraphics()
	private static final Graphics2D createStandardBackground(BufferedImage i)
	{
		Graphics2D g = i.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		return g;
	}
	
	/** Creates the arrow images */
	static
	{			
		LEFT_ARROW = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = createStandardBackground(LEFT_ARROW);

		int[] xArray = new int[]
		{
				0, 
				6, 
				6,
				12,
				12,
				6,
				6
		};
		int[] yArray = new int[]
		{
				6,
				0,
				4,
				4,
				8,
				8,
				12
		};
		g.fillPolygon(xArray, yArray, 7);
		g.dispose();
		////////////////
		RIGHT_ARROW = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);

		g = createStandardBackground(RIGHT_ARROW);

		xArray = new int[]
		{
				0, 
				6, 
				6,
				12,
				6,
				6,
				0
		};
		yArray = new int[]
		{
				4,
				4,
				0,
				6,
				12,
				8,
				8
		};
		g.fillPolygon(xArray, yArray, 7);
		g.dispose();


		///////////////
		UP_ARROW = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		
		g = createStandardBackground(UP_ARROW);
		
		xArray = new int[]
		{
				0, 
				6, 
				12,
				8,
				8,
				4,
				4
		};
		yArray = new int[]
		{
				6,
				0,
				6,
				6,
				12,
				12,
				6
		};

		g.fillPolygon(xArray, yArray, 7);
		g.dispose();
		
		///////////////
		DOWN_ARROW = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(DOWN_ARROW);
		
		xArray = new int[]
		{
				0, 
				4, 
				4,
				8,
				8,
				12,
				6
		};
		yArray = new int[]
		{
				6,
				6,
				0,
				0,
				6,
				6,
				12
		};
		g.fillPolygon(xArray, yArray, 7);
		g.dispose();
		
		///////////////
		ZOOM_OUT_HORIZONTAL_IMAGE = new BufferedImage(26, 12, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(ZOOM_OUT_HORIZONTAL_IMAGE);
		
		xArray = new int[]
		{
				0, 
				11, 
				11
		};
		yArray = new int[]
		{
				6, 
				0,
				11
		};
		g.fillPolygon(xArray, yArray, 3);
		
		xArray = new int[]
		{
				14,
				14,
				25
		};
		yArray = new int[]
		{
				0,
				11,
				6				
		};
		g.fillPolygon(xArray, yArray, 3);
		g.dispose();
		
		///////////////
		ZOOM_OUT_VERTICAL_IMAGE = new BufferedImage(15, 18, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(ZOOM_OUT_VERTICAL_IMAGE);
		
		xArray = new int[]
		{
				0, 
				7,
				14
		};
		yArray = new int[]
		{
				7,
				0,
				7
		};
		g.fillPolygon(xArray, yArray, 3);
		
		xArray = new int[]
		{
				0,
				7,
				14
		};
		yArray = new int[]
		{
				10,
				17,
				10			
		};
		g.fillPolygon(xArray, yArray, 3);
		g.dispose();
		///////////////////////
		
		///////////////
		ZOOM_IN_HORIZONTAL_IMAGE = new BufferedImage(26, 12, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(ZOOM_IN_HORIZONTAL_IMAGE);
		
		xArray = new int[]
		{
				0, 
				0, 
				11
		};
		yArray = new int[]
		{ 
				0,
				11,
				6
		};
		g.fillPolygon(xArray, yArray, 3);
		
		xArray = new int[]
		{
				14,
				25,
				25
		};
		yArray = new int[]
		{
				6,
				0,
				11				
		};
		g.fillPolygon(xArray, yArray, 3);
		g.dispose();
		
		///////////////
		ZOOM_IN_VERTICAL_IMAGE = new BufferedImage(15, 18, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(ZOOM_IN_VERTICAL_IMAGE);
		
		xArray = new int[]
		{
				0, 
				7,
				14
		};
		yArray = new int[]
		{
				0,
				7,
				0
		};
		g.fillPolygon(xArray, yArray, 3);
		
		xArray = new int[]
		{
				0,
				7,
				14
		};
		yArray = new int[]
		{
				17,
				10,
				17
		};
		g.fillPolygon(xArray, yArray, 3);
		g.dispose();
		///////////////////////
		
		DOCK_IMAGE = new BufferedImage(17, 17, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(DOCK_IMAGE);
		
		xArray = new int[]
		{
				8,
				16,
				16,
				8
		};
		yArray = new int[]
		{
				0,
				0,
				8,
				8
		};
		
		g.drawPolygon(xArray, yArray, 4);
		
		xArray = new int[]
		{
				0,
				12,
				12,
				0
		};
		yArray = new int[]
		{
				4,
				4,
				16,
				16
		};
	
		g.setStroke(new BasicStroke(2));
		g.drawPolygon(xArray, yArray, 4);		
		g.dispose();
		///////////////////////
		UNDOCK_IMAGE = new BufferedImage(17, 17, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(UNDOCK_IMAGE);
		
		xArray = new int[]
		{
				0,
				12,
				12,
				0
		};
		yArray = new int[]
		{
				4,
				4,
				16,
				16
		};
		
		g.drawPolygon(xArray, yArray, 4);
		
		xArray = new int[]
		{
				8,
				16,
				16,
				8
		};
		yArray = new int[]
		{
				0,
				0,
				8,
				8
		};

		g.setStroke(new BasicStroke(2));
		g.drawPolygon(xArray, yArray, 4);
		g.dispose();
		
		////////////////////
		PLAY_IMAGE = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(PLAY_IMAGE);
		
		xArray = new int[]
		{
				0, 
				0, 
				11
		};
		yArray = new int[]
		{
				0,
				11, 
				6
		};
		g.fillPolygon(xArray, yArray, 3);
		g.dispose();
		
		///////////////////////
		STOP_IMAGE = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		g = createStandardBackground(STOP_IMAGE);
		
		xArray = new int[]
		{
				0,
				12,
				12,
				0
		};
		yArray = new int[]
		{
				0,
				0,
				12,
				12
		};
		
		g.fillPolygon(xArray, yArray, 4);
		
		g.dispose();
	}
	
	public static BufferedImage createLineImage(Line2D line, Color c)
	{
		BufferedImage image = new BufferedImage(line.getBounds().width, line.getBounds().height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(c);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
		return image;
	}
}

