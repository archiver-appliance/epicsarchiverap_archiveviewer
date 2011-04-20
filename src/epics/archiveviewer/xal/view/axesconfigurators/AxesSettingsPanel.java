/*
 * Created on Feb 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.axesconfigurators;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AxesSettingsPanel extends AVAbstractPanel
{
	private TimeAxesConfigurator timeAxesConfigurator;
	private RangeAxesConfigurator rangeAxesConfigurator;
	private JCheckBox keepAxesRangesBox;
	private JButton plotButton;
	
	
	public AxesSettingsPanel()
	{
		init();
	}
	
	/**
	 * @see epics.archiveviewer.xal.view.components.AVAbstractPanel#addComponents()
	 */
	protected void addComponents() {
		JPanel plotButtonPanel = new JPanel(new BorderLayout());
		plotButtonPanel.add(this.plotButton, BorderLayout.EAST);
		
		JPanel plotButtonAndKeepAxesRangesBoxPanel = new JPanel(new BorderLayout());
		plotButtonAndKeepAxesRangesBoxPanel.add(this.keepAxesRangesBox, BorderLayout.CENTER);
		plotButtonAndKeepAxesRangesBoxPanel.add(plotButtonPanel, BorderLayout.SOUTH);
		
		JPanel plotButtonAndKeepAxesRangesBoxPanel2 = new JPanel(new BorderLayout());
		plotButtonAndKeepAxesRangesBoxPanel2.add(plotButtonAndKeepAxesRangesBoxPanel, BorderLayout.SOUTH);
		
		JPanel rangeAxesPlotButtonKeepRangesBoxPanel = new JPanel(new BorderLayout(3, 0));
		rangeAxesPlotButtonKeepRangesBoxPanel.add(this.rangeAxesConfigurator, BorderLayout.CENTER);
		rangeAxesPlotButtonKeepRangesBoxPanel.add(plotButtonAndKeepAxesRangesBoxPanel2, BorderLayout.EAST);
		rangeAxesPlotButtonKeepRangesBoxPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0,0));
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(this.timeAxesConfigurator, BorderLayout.CENTER);
		p.add(rangeAxesPlotButtonKeepRangesBoxPanel, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
	}
	/**
	 * @see epics.archiveviewer.xal.view.components.AVAbstractPanel#createComponents()
	 */
	protected void createComponents() {
		this.timeAxesConfigurator = new TimeAxesConfigurator();
		this.rangeAxesConfigurator = new RangeAxesConfigurator();
		this.keepAxesRangesBox = new JCheckBox("Keep Ranges");
		this.plotButton = new JButton("Plot");
	}
	
	public TimeAxesConfigurator getTimeAxesConfigurator()
	{
		return this.timeAxesConfigurator;
	}
	
	public RangeAxesConfigurator getRangeAxesConfigurator()
	{
		return this.rangeAxesConfigurator;
	}
	
	public JCheckBox getKeepAxesRangesBox()
	{
		return this.keepAxesRangesBox;
	}
	
	public JButton getPlotButton()
	{
		return this.plotButton;
	}
}
