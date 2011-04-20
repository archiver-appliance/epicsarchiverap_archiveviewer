/*
 * Created on Feb 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.components;

import java.awt.Color;

import javax.swing.JButton;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVColorButton extends JButton
{
	public AVColorButton()
	{
		super("...");
	}
	
	public Color getColor()
	{
		return getBackground();
	}
	
	public void setColor(Color c)
	{
		setBackground(c);
		//set the foreground to darker or brigher color, depending on c
		int resultRGB = 0;
		if(c.getRed() < 0x88)
		{
			resultRGB = 0xFF;
		}
		resultRGB *= 0x100;
		if(c.getGreen() < 0x88)
		{
			resultRGB += 0xFF;
		}
		resultRGB *= 0x100;
		if(c.getBlue() < 0x88)
		{
			resultRGB += 0xFF;
		}
		setForeground(new Color(resultRGB));
	}
}
