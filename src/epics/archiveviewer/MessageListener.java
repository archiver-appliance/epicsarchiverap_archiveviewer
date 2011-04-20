/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer;

/**
 * The interface for message processors
 * @author serge
 */
public interface MessageListener {
	/**
	 * Displays the specified error message and the specified exception
	 * @param s error message
	 * @param e exception
	 */
	public void displayError(String s, Exception e);
	/**
	 * Displays the specified warning message and the specified exception
	 * @param s warning message
	 * @param e exception
	 */
	public void displayWarning(String s, Exception e);
	/**
	 * Display the specified message
	 * @param s a message
	 */
	public void displayInformation(String s);
}
