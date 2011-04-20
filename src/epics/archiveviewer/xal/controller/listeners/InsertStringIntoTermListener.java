/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import epics.archiveviewer.xal.view.aveconfigurators.CalculatorPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InsertStringIntoTermListener implements ActionListener
{
	private final JTextField destination;
	private final String inputString;
	
	public InsertStringIntoTermListener(JTextField _dest, String _inputString)
	{
		this.destination = _dest;
		this.inputString = _inputString;
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
			int pos = this.destination.getCaretPosition();
			sb.append(old.substring(0, pos));
			sb.append(this.inputString);
			sb.append(old.substring(pos));
		}
		else
		{
			int pos1 = this.destination.getSelectionStart();
			int pos2 = this.destination.getSelectionEnd();
			sb.append(old.substring(0, pos1));
			sb.append(this.inputString);
			sb.append("(");
			sb.append(old.substring(pos1, pos2));
			sb.append(")");
			sb.append(old.substring(pos2));
		}
		this.destination.setText(sb.toString());
	}
}
