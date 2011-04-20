/*
 * Created on Mar 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ToggleSignOfATermListener implements ActionListener
{
	private final JTextField destination;
	
	public ToggleSignOfATermListener(JTextField _dest)
	{
		this.destination = _dest;
	}
	
	/**
	 * this method executes after the appropriate action took place
	 * 
	 * @param e
	 *            the ActionEvent
	 */
	public void actionPerformed(ActionEvent e)
	{
		String old = this.destination.getText();
	
		StringBuffer sb = new StringBuffer();
		if (this.destination.getSelectedText() == null)
		{
			if(old.charAt(0) == KeyEvent.VK_MINUS)
				sb.append(old.substring(1));
			else
			{
				sb.append("-(");
				sb.append(old);
				sb.append(")");
			}
		}
		else
		{
			int pos1 = this.destination.getSelectionStart();
			int pos2 = this.destination.getSelectionEnd();
			sb.append(old.substring(0, pos1));
			String selectedString = old.substring(pos1, pos2);
			if(selectedString.charAt(0) == KeyEvent.VK_MINUS)
				sb.append(selectedString.substring(1));
			else
			{
				sb.append("-(");
				sb.append(selectedString);
				sb.append(")");
			}	
			sb.append(old.substring(pos2));
		}
		this.destination.setText(sb.toString());
	}
}
