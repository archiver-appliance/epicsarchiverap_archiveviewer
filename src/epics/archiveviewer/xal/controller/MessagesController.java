/*
 * Created on Mar 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import epics.archiveviewer.MessageListener;
import epics.archiveviewer.xal.view.MessagesDialog;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessagesController
{
	private final Color ERROR_COLOR = Color.RED;
	private final Color WARNING_COLOR = Color.YELLOW;
	private final Color INFO_COLOR = Color.BLACK;
	
	private final MessagesDialog messagesDialog;
	private final JLabel statusLabel;
	
	
	public MessagesController(AVController avc)
	{
		this.messagesDialog = MessagesDialog.getInstance(avc.getMainWindow());
		this.statusLabel = avc.getMainAVPanel().getStatusPanel().getStatusLabel();
		
		avc.getAVBase().addMessageListener(new XALMessageListener());
	}
	
	private class XALMessageListener implements MessageListener
	{
	
		public void displayError(final String s, final Exception e) {
			final Runnable doPrintErrorMessage = new Runnable()
			{
				public void run()
				{
					messagesDialog.addMessage(MessagesDialog.ERROR_MESSAGE, s, e);
					statusLabel.setForeground(ERROR_COLOR);
					statusLabel.setText("Error");
				}
			};
			SwingUtilities.invokeLater(doPrintErrorMessage);
		}
	
	
		public void displayWarning(final String s, final Exception e) {
			final Runnable doPrintWarningMessage = new Runnable()
			{
				public void run()
				{
					messagesDialog.addMessage(MessagesDialog.WARNING_MESSAGE, s, e);
					statusLabel.setForeground(WARNING_COLOR);
					statusLabel.setText("Warning");
				}
			};
			SwingUtilities.invokeLater(doPrintWarningMessage);
	
		}
	
	
		public void displayInformation(final String s) {
			final Runnable doPrintInformationMessage = new Runnable()
			{
				public void run()
				{
					statusLabel.setForeground(INFO_COLOR);
					statusLabel.setText(s);	
				}
			};
			SwingUtilities.invokeLater(doPrintInformationMessage);
		
		}
	}

}
