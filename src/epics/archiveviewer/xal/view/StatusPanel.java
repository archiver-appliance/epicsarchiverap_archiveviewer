/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatusPanel extends AVAbstractPanel
{	
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JButton interruptButton;
	
	public StatusPanel()
	{
		init();
	}
	
	protected void createComponents() {
		this.statusLabel = new JLabel();
		this.statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.progressBar = new JProgressBar(0, 100);
		
		this.interruptButton = new JButton();
		this.interruptButton.setBackground(Color.BLACK);
		this.interruptButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		int h = this.progressBar.getPreferredSize().height;
		this.interruptButton.setPreferredSize(new Dimension((int)(1.3*h),h));
	}

	protected void addComponents() {
		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.add(this.progressBar, BorderLayout.CENTER);
		progressPanel.add(this.interruptButton, BorderLayout.EAST);
		
		setLayout(new BorderLayout(5, 0));
		add(this.statusLabel, BorderLayout.CENTER);
		add(progressPanel, BorderLayout.EAST);
	}
	
	public JLabel getStatusLabel()
	{
		return this.statusLabel;
	}
	
	public JProgressBar getProgressBar()
	{
		return this.progressBar;
	}
	
	public JButton getInterruptButton()
	{
		return this.interruptButton;
	}

}
