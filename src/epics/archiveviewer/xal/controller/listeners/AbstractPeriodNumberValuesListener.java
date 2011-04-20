/*
 * Created on Mar 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractPeriodNumberValuesListener implements KeyListener
{
	private final JTextField dependingField;
	
	protected abstract double getCurrentTimeRangeInSeconds();

	public AbstractPeriodNumberValuesListener(JTextField theOtherField)
	{
		this.dependingField = theOtherField;
	}
	
	public void keyPressed(KeyEvent e) {

	}
	
	public void keyReleased(KeyEvent e) {
		try
		{
			JTextField thisField = (JTextField)e.getSource();
			int thisFieldValue = Integer.valueOf(thisField.getText()).intValue();
			int dependingFieldValue = (int) (getCurrentTimeRangeInSeconds()/thisFieldValue);
			this.dependingField.setText(Integer.toString(dependingFieldValue));
		}
		catch(Exception ex)
		{
			//do nothing
		}
	}
	
	public void keyTyped(KeyEvent e) {
	}
}
