/*
 * Created on Mar 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import epics.archiveviewer.ProgressTask;
import epics.archiveviewer.base.model.listeners.ProgressListener;
import epics.archiveviewer.base.util.AVProgressTask;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.view.StatusPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatusPanelProgressListener extends ProgressListener
{
	private static final int NR_OF_NO_CHANGES_BEFORE_INDETERMINATE = 77;
	private static final Color ACTIVE_COLOR = Color.RED;
	private static final Color IDLE_COLOR = Color.BLACK;
	
	
	private final AVController avController;
	private final StatusPanel statusPanel;

	private int noValueChangeCounter;
	
	private boolean firstRun;
	
	private AVProgressTask getAVProgressTask()
	{
		return (AVProgressTask) super.getProgress();
	}
	
	public StatusPanelProgressListener(AVController avc, StatusPanel sp, AVProgressTask avp)
	{
		super(avp);
		this.avController = avc;
		this.statusPanel = sp;
		this.noValueChangeCounter = 0;
		this.firstRun = true;
		this.statusPanel.getInterruptButton().setBackground(IDLE_COLOR);
	}
	
	public void actionPerformed(ActionEvent e) {
			AVProgressTask avProgressTask = getAVProgressTask();
			
			if(this.firstRun)
			{
				this.statusPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.statusPanel.getInterruptButton().setBackground(ACTIVE_COLOR);
				this.firstRun = false;
			}
		
		 	int val = avProgressTask.getCurrentValue();
		 	
			if (val >= ProgressTask.MAX)
			{
				statusPanel.getInterruptButton().setBackground(IDLE_COLOR);
			    statusPanel.getProgressBar().setIndeterminate(false);
				avController.getAVBase().removeProgressListener(this);
				statusPanel.getProgressBar().setValue(0);
				statusPanel.setCursor(null);
				this.firstRun = true;
			}
			else
		    {
			    if(statusPanel.getProgressBar().getValue() == val)
			    {
			    	//if progress value remained the same
			        noValueChangeCounter++;
			        if(noValueChangeCounter > NR_OF_NO_CHANGES_BEFORE_INDETERMINATE)
			        {
			            statusPanel.getProgressBar().setIndeterminate(true);
			        }
			    }
			    else
			    {
			        noValueChangeCounter = 0;
				    statusPanel.getProgressBar().setValue(val);
				    statusPanel.getProgressBar().setIndeterminate(false);
			    }
			}
			//in any case
			String s = avProgressTask.getCurrentMessage();
			if(s != null && s.equals(statusPanel.getStatusLabel().getText()) == false)
			    avController.getAVBase().displayInformation(s);
	}

}
