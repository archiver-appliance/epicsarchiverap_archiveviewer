/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.commandline.listeners;

import epics.archiveviewer.MessageListener;


/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SIOMessageListener implements MessageListener{

	

	public void displayError(String s, Exception e) {
		System.out.println(s);
		e.printStackTrace();
	}

	public void displayWarning(String s, Exception e) {
		System.out.println(s);
		e.printStackTrace();
	}

	public void displayInformation(String s) {
		System.out.println(s);
	}

}
