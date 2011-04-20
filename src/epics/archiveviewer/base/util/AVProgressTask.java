/*
 * Created on Aug 6, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package epics.archiveviewer.base.util;

import epics.archiveviewer.ProgressTask;


//stops when currentValue >= 100
public class AVProgressTask implements ProgressTask
{
	private int currentValue;
	private String currentMessage;
	private boolean acceptChanges;

	private boolean interrupted;
	
	private class ActualTask
	{
		ActualTask()
		{
			while (currentValue < 100)
			{
				try
				{
					Thread.sleep(200);
					update();
				}
				catch (Exception e)
				{
					break;
				}
			}
			currentValue = 100;
			return;
		}
	}
    
	//updates the protected fields of this class, i.e.
	//currentValue and currentMessage
	private void update()
	{
    	if(this.currentValue >= 100)
			return;
    	if(this.currentMessage == null)
    	    //only if it is still simulating
    	    this.currentValue = this.currentValue + (100 - this.currentValue)/20;
		setProgressParameters(this.currentValue, this.currentMessage);
	}
	
    //DO NOT CALL from Client, called by GUI only;
	public AVProgressTask()
	{		
		this.currentValue = 0;
		this.currentMessage = null;
		this.interrupted = false;
		this.acceptChanges = true;
		final SwingWorker worker = new SwingWorker()
		{
			public Object construct()
			{
				return new ActualTask();
			}
		};
		worker.start();
	}
	
	public void acceptChanges(boolean flag)
	{
		this.acceptChanges = flag;
	}
	
    public void interrupt()
    {
        this.interrupted = true;
        stop();
    }
    
	public void stop()
	{
	    this.currentValue = 100;
	}
    
    public void setProgressParameters(int v, String s)
    {
        if(this.acceptChanges && this.currentValue < 100)
        {
            this.currentValue = v;
            this.currentMessage = s;
        }
    }   
    
	public boolean interrupted()
	{
	    return this.interrupted;
	}
    
	public int getCurrentValue()
	{
	    return this.currentValue;
	}
	
	public String getCurrentMessage()
	{
		if(this.currentValue < 100)
			return this.currentMessage;
		return null;
	}
}

