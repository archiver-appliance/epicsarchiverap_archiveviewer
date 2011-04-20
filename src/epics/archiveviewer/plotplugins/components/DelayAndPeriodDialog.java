/*
 * Created on Jan 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.plotplugins.JFreeChartForWaveforms;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DelayAndPeriodDialog extends JDialog
{
	private static final String DELAY_PERIOD_FIELDS_TOOLTIP = "in msecs";
	private final JFreeChartForWaveforms wfChart;
	
	private final ValuesContainer[] waveforms;
	
	private final ValuesContainer[] nonWaveforms;
	
	private final HashMap wfVCsToLabelIndices;
	
	private JLabel[] waveformLabels;
	
	private JComboBox[] delayVCsBoxes;
	
	private JComboBox[] periodVCsBoxes;
	
	private JTextField[] delayFields;
	
	private JTextField[] periodFields;
	
	private ListCellRenderer vcsRenderer;
	
	private JButton okButton;
	
	private void buildComponents()
	{
		this.waveformLabels = new JLabel[this.waveforms.length];
		this.delayVCsBoxes = new JComboBox[this.waveforms.length];
		this.periodVCsBoxes = new JComboBox[this.waveforms.length];
		this.delayFields = new JTextField[this.waveforms.length];
		this.periodFields = new JTextField[this.waveforms.length];
		
		this.vcsRenderer = new VCsListCellRenderer();
		
		int i=0;
		for(i=0; i<this.waveforms.length; i++)
		{
			this.waveformLabels[i] = new JLabel(this.waveforms[i].getAVEntry().getName());
			this.waveformLabels[i].setToolTipText(this.waveforms[i].getAVEntry().getArchiveDirectory().toString());
			
			this.wfVCsToLabelIndices.put(this.waveforms[i], new Integer(i));
			
			this.delayFields[i] = new JTextField(6);
			this.delayFields[i].setToolTipText(DELAY_PERIOD_FIELDS_TOOLTIP);
			
			this.periodFields[i] = new JTextField(6);
			this.periodFields[i].setToolTipText(DELAY_PERIOD_FIELDS_TOOLTIP);
		}		
		
		if(this.nonWaveforms != null && this.nonWaveforms.length > 0)
		{
			for(i=0; i<this.waveforms.length; i++)
			{
				this.delayVCsBoxes[i] = new JComboBox(nonWaveforms);
				this.delayVCsBoxes[i].insertItemAt(null, 0);
				this.delayVCsBoxes[i].setRenderer(this.vcsRenderer);	
				this.delayVCsBoxes[i].setSelectedIndex(0);
				
				this.periodVCsBoxes[i] = new JComboBox(nonWaveforms);
				this.periodVCsBoxes[i].insertItemAt(null, 0);
				this.periodVCsBoxes[i].setRenderer(this.vcsRenderer);	
				this.periodVCsBoxes[i].setSelectedIndex(0);
			}
		}
		
		
		
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				wfChart.repaint();
				dispose();
			}
		});
	}
	
	private void addComponents()
	{
		JPanel wfLabelPanel = new JPanel(new GridLayout(0,1));
		wfLabelPanel.add(new JLabel());
		
		JPanel delayInputPanel = new JPanel(new GridLayout(0,2));
		delayInputPanel.add(new JLabel("Delay", SwingConstants.CENTER));
		delayInputPanel.add(new JLabel());
		
		JPanel periodInputPanel = new JPanel(new GridLayout(0,2));
		periodInputPanel.add(new JLabel("Period", SwingConstants.CENTER));
		periodInputPanel.add(new JLabel());
		
		for(int i=0; i<this.waveformLabels.length; i++)
		{
			wfLabelPanel.add(this.waveformLabels[i]);
			
			delayInputPanel.add(this.delayFields[i]);
			if(this.delayVCsBoxes[i] == null)
			{
				delayInputPanel.add(new JLabel());
			}
			else
			{
				delayInputPanel.add(this.delayVCsBoxes[i]);
			}
			
			periodInputPanel.add(this.periodFields[i]);
			if(this.periodVCsBoxes[i] == null)
			{
				periodInputPanel.add(new JLabel());
			}
			else
			{
				periodInputPanel.add(this.periodVCsBoxes[i]);
			}
		}
		
		GridLayout g = new GridLayout();
		g.setHgap(5);
		JPanel eastPanel = new JPanel(g);
		eastPanel.add(delayInputPanel);
		eastPanel.add(periodInputPanel);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		buttonsPanel.add(this.okButton, gbc);
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		
		BorderLayout b = new BorderLayout(10, 10);
		JPanel p = new JPanel(b);
		p.add(wfLabelPanel, BorderLayout.CENTER);
		p.add(eastPanel, BorderLayout.EAST);
		p.add(buttonsPanel, BorderLayout.SOUTH);
		
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(p, gbc);
	}
	
	public DelayAndPeriodDialog(JFreeChartForWaveforms jfc, ValuesContainer[] wfVCs, ValuesContainer[] nonWfVCs)
	{
		super(jfc.getAVBFacade().getMainFrame(), "Select Delay And Period PVs...");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.wfChart = jfc;
		this.waveforms = wfVCs;
		this.nonWaveforms = nonWfVCs;
		
		this.wfVCsToLabelIndices = new HashMap();
		
		buildComponents();
		addComponents();
		
		setResizable(false);
	}
	
	public void display(Component component)
	{
		AVBaseUtilities.setWindowToMinimalSize(this, 320, 240);
			
		setLocationRelativeTo(component);
		setVisible(true);
	}
	
	public double getDelay(ValuesContainer waveformVC, int vcItem)
	{
		int delayLabelIndex = ((Integer)this.wfVCsToLabelIndices.get(waveformVC)).intValue();
		
		if(this.delayFields[delayLabelIndex].getText().equals(""))
		{
			try
			{
				ValuesContainer vc = (ValuesContainer) this.delayVCsBoxes[delayLabelIndex].getSelectedItem();
				return ((Double)vc.getValue(vcItem).get(0)).doubleValue();
			}
			catch(Exception e)
			{
				return 0;
			}
		}
		
		return Double.parseDouble(this.delayFields[delayLabelIndex].getText());
	}
	
	public double getPeriod(ValuesContainer waveformVC, int vcItem)
	{
		int periodLabelIndex = ((Integer)this.wfVCsToLabelIndices.get(waveformVC)).intValue();
		
		if(this.periodFields[periodLabelIndex].getText().equals(""))
		{
			try
			{
				ValuesContainer vc = (ValuesContainer) this.periodVCsBoxes[periodLabelIndex].getSelectedItem();
				return ((Double)vc.getValue(vcItem).get(0)).doubleValue();
			}
			catch(Exception e)
			{
				return 1;
			}
		}
		
		return Double.parseDouble(this.periodFields[periodLabelIndex].getText());
	}
	
}
