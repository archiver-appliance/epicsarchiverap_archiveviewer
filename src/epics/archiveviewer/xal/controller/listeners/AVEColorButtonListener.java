/*
 * Created on Feb 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JSlider;

import epics.archiveviewer.xal.view.aveconfigurators.CommonGraphConfiguratorPanel;
import epics.archiveviewer.xal.view.components.AVColorButton;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEColorButtonListener implements ActionListener
{
	private final ActionListener additionalActionListener;

	private ActionListener createSetColorListener(
			final AVColorButton avColorButton,
			final JColorChooser jc)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				avColorButton.setColor(jc.getColor());
				if(additionalActionListener != null)
					additionalActionListener.actionPerformed(e);
			}
		};
	}
	
	public AVEColorButtonListener()
	{
		this(null);
	}
	
	public AVEColorButtonListener(ActionListener additonalAL)
	{
		this.additionalActionListener = additonalAL;
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() instanceof AVColorButton)
		{
			AVColorButton avColorButton = (AVColorButton) e.getSource();
			JColorChooser jc = new JColorChooser(avColorButton.getColor());
		    JColorChooser.createDialog(
		            avColorButton,
		            "Choose a Color", 
		            true,
		            jc,
		            createSetColorListener(avColorButton, jc),
		            null
		    	).setVisible(true);
		}
	}
}
