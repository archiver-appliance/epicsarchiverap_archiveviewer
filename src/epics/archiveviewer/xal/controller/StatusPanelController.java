/*
 * Created on Mar 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.xal.view.StatusPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatusPanelController {
	
	private ActionListener createInterruptListener(final AVBase avBase)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				avBase.interruptAllProgressListeners();
				avBase.displayInformation("Interrupted");
			}
			
		};
	}
	
	public StatusPanelController(AVController avc, StatusPanel sp)
	{
		sp.getInterruptButton().addActionListener(
				createInterruptListener(avc.getAVBase()));

	}

}
