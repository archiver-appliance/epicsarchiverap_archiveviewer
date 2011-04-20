/*
 * Created on Nov 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;

import javax.swing.JTextArea;
import javax.swing.text.PlainDocument;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerInfoPanel extends AVAbstractPanel
{
	private JTextArea textPad;
	
	public ServerInfoPanel()
	{
		init();
	}
	
	protected void addComponents() 
	{
		setLayout(new BorderLayout());
		add(textPad, BorderLayout.CENTER);
	}
	
	protected void createComponents() {
		this.textPad = new JTextArea(new PlainDocument());
		textPad.setEditable(false);		
	}
	
	public JTextArea getTextPad()
	{
		return this.textPad;
	}
}
