/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.export;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OutputPanel extends AVAbstractPanel
{
	private JLabel fileLabel;
	private JTextField filePathField;
	private JButton fileChooserButton;
	private JTextArea textArea;
	private JScrollPane textScrollPane;

	public OutputPanel()
	{
		init();
	}
	
	protected void createComponents() {
		this.fileLabel = new JLabel("File");
		this.filePathField = new JTextField(25);
		this.fileChooserButton = new JButton("...");
		this.textArea = new JTextArea(10, 70);
		this.textArea.setBackground(Color.WHITE);
		this.textScrollPane = new JScrollPane(this.textArea);
	}

	protected void addComponents() {	
		JPanel fileChooserPanel = new JPanel(new BorderLayout(5,0));
		fileChooserPanel.add(this.fileLabel, BorderLayout.WEST);
		fileChooserPanel.add(this.filePathField, BorderLayout.CENTER);
		fileChooserPanel.add(this.fileChooserButton, BorderLayout.EAST);
		
		JPanel fileChooserPanel2 = new JPanel(new BorderLayout());
		fileChooserPanel2.add(fileChooserPanel, BorderLayout.EAST);
		
		setLayout(new BorderLayout(0, 10));
		add(this.textScrollPane, BorderLayout.CENTER);
		add(fileChooserPanel2, BorderLayout.SOUTH);
	}
	
	public JTextField getFilePathField()
	{
		return this.filePathField;
	}
	
	public JButton getFileChooserButton()
	{
		return this.fileChooserButton;
	}
	
	public JTextArea getTextArea()
	{
		return this.textArea;
	}
	
	public JScrollPane getScrollPane()
	{
		return this.textScrollPane;
	}
}
