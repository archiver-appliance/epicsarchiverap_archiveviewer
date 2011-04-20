/*
 * Created on Dec 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.plotplugins.JFreeChartCorrelator;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CorrelatorInputPanel extends JPanel
{
	private final JFreeChartCorrelator correlatorChart;
	
	private JLabel domainVCLabel;
	
	private JComboBox vcsBox;
	
	private ActionListener vcsBoxListener;
	
	private void drawCorrelatedChart()
	{
		ArrayList aL = new ArrayList();
		
		for(int i=0; i<vcsBox.getItemCount(); i++)
		{
			aL.add(vcsBox.getItemAt(i));
		}
		
		int selectedIndex = vcsBox.getSelectedIndex();
		if(selectedIndex < 0)
			return;
		ValuesContainer domainVC = (ValuesContainer) aL.get(selectedIndex);
		aL.remove(selectedIndex);
		
		//the rest of VCs are range VCs
		ValuesContainer[] rangeVCs = new ValuesContainer[aL.size()];
		aL.toArray(rangeVCs);
		
		try
		{
			this.correlatorChart.displayCorrelatedNondiscreteVCs(domainVC, rangeVCs);	
		}
		catch(Exception ex)
		{
			this.correlatorChart.getAVBFacade().displayError("Couldn't plot", ex);
		}
	}
	
	private void addComponents()
	{
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(this.domainVCLabel, BorderLayout.WEST);
		p1.add(this.vcsBox, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(p1, BorderLayout.WEST);
	}
	
	private void buildComponents()
	{
		this.domainVCLabel = new JLabel("Domain ");
		
		this.vcsBox = new JComboBox();
		this.vcsBox.setRenderer(new VCsListCellRenderer());
		
		this.vcsBoxListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				drawCorrelatedChart();
			}
		};
	}
	
	public CorrelatorInputPanel(JFreeChartCorrelator jfcc)
	{
		this.correlatorChart = jfcc;
		buildComponents();
		addComponents();
	}
	
	public void loadVCs(ArrayList vcsToCorrelate)
	{
		int previousSelectedIndex = this.vcsBox.getSelectedIndex();
		this.vcsBox.removeActionListener(this.vcsBoxListener);
		this.vcsBox.removeAllItems();
		for(int i=0; i<vcsToCorrelate.size(); i++)
		{
			this.vcsBox.addItem(vcsToCorrelate.get(i));
		}
		if(this.vcsBox.getItemCount() < 2)
			return;
		
		this.vcsBox.addActionListener(this.vcsBoxListener);
		
		if(previousSelectedIndex < 0 || previousSelectedIndex > this.vcsBox.getItemCount() - 1)
			this.vcsBox.setSelectedIndex(0);
		else
			this.vcsBox.setSelectedIndex(previousSelectedIndex);
	}
	
	public void clear()
	{
		this.vcsBox.removeAllItems();
	}
}
