/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.util;

import java.awt.Color;
import java.util.HashSet;

import epics.archiveviewer.base.AVBaseConstants;


/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ColorUtilities {
	//the returned color is added to the hash set
	public static Color getNextAvailableColor(HashSet usedColorsSet) throws Exception 
	{
		Color c = null;
		for(int i=0; i<AVBaseConstants.AVAILABLE_COLORS.length; i++)
		{
			c = AVBaseConstants.AVAILABLE_COLORS[i];
			if(usedColorsSet.contains(c) == false)
			{
				usedColorsSet.add(c);
				return c;
			}
		} 
		throw new Exception("No more colors available");
	}
	
	public static String getHTMLName(Color c) throws Exception
	{
		StringBuffer sb = new StringBuffer("#");
		int value = c.getRed();
		if(value == 0)
			sb.append("00");
		else
			sb.append(Integer.toHexString(c.getRed()));
		
		value = c.getGreen();
		if(value == 0)
			sb.append("00");
		else
			sb.append(Integer.toHexString(c.getGreen()));
		
		value = c.getBlue();
		if(value == 0)
			sb.append("00");
		else
			sb.append(Integer.toHexString(c.getBlue()));
		return sb.toString();
	}
	
	public static Color getColorFromHTMLName(String htmlColorName) throws Exception
	{
		StringBuffer sb = new StringBuffer(htmlColorName);
		sb.delete(0,1);
		
		int red = Integer.parseInt(sb.substring(0, 2), 16);
		int green = Integer.parseInt(sb.substring(2, 4), 16);
		int blue = Integer.parseInt(sb.substring(4), 16);
		
		return new Color(red, green, blue);
	}

}
