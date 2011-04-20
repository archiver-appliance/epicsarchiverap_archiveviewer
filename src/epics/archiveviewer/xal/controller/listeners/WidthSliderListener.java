/*
 * Created on 21.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Line2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import epics.archiveviewer.base.Icons;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.aveconfigurators.CommonGraphConfiguratorPanel;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WidthSliderListener implements ChangeListener
{
    private final CommonGraphConfiguratorPanel cgcPanel;
    
    private Icon getImageIcon(float drawWidth)
    {
        float x2 = cgcPanel.getWidthSlider().getWidth();

		if(x2 <=0)
			x2 = cgcPanel.getWidthSlider().getPreferredSize().width;
		Color c = null;
		if(cgcPanel.getAVEColorButton().isEnabled())
			c = cgcPanel.getAVEColorButton().getColor();
		else
			c = Color.BLACK;
		Image i = Icons.createLineImage(
				new Line2D.Float(0, 0, x2, drawWidth),
				c
				);
		return new ImageIcon(i);
    }
    
    private void displayWidth()
    {
        int newValue = this.cgcPanel.getWidthSlider().getValue();
		float newWidth = AVXALUtilities.percentToDrawWidth(newValue);
		
		//only if we can really see the change
		Icon i = getImageIcon(newWidth);
		cgcPanel.getWidthImageLabel().setIcon(i);
    }
    
    public WidthSliderListener(CommonGraphConfiguratorPanel cgcp)
    {
        this.cgcPanel = cgcp;
        displayWidth();
    }
    
    public void stateChanged(ChangeEvent e) {
        displayWidth();
	}		
}
