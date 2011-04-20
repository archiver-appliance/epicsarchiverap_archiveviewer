/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.ui.RectangleEdge;

import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.Icons;
import epics.archiveviewer.plotplugins.JFreeChartForWaveforms;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WFControlPanel extends JPanel
{
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
	private final JFreeChartForWaveforms wfChart;
	
	private final Timer playTimer;
	
	private WaveformTimePlotsChart timeChart;
	private JSlider slider;
	
	private JButton wfParametersButton;
	private JButton playButton;
	private JButton stopButton;
	
	private JLabel speedLabel;
	private JSpinner speedSpinner;
	
	private JLabel tsLabel;
	private JTextField tsField;
	
	private Shape verticalMarker;	
	private double previousPlotHeight;
	private double previousPlotWidth;
	
	private void play()
	{
		int sliderValue = this.slider.getValue();
		if(sliderValue == this.slider.getMaximum())
		{
			stopPlaying();
		}
		else
		{
			sliderValue += 1;
			this.slider.setValue(sliderValue);
		}
	}
	
	private void stopPlaying()
	{
		playButton.setEnabled(true);
		playTimer.stop();
	}
	
	private void addComponents()
	{
		JPanel westPanel = new JPanel(new BorderLayout());	
		westPanel.add(this.timeChart.getDataChartPanel(), BorderLayout.CENTER);	
		westPanel.add(this.slider, BorderLayout.SOUTH);
		
		JPanel buttonsPanel = new JPanel(new GridLayout(1,0));
		buttonsPanel.add(this.playButton);
		buttonsPanel.add(this.stopButton);
		
		JPanel buttonsPanel2 = new JPanel(new GridBagLayout());
		buttonsPanel2.add(buttonsPanel, new GridBagConstraints());
		
		JPanel labelsPanel = new JPanel(new GridLayout(0,1));
		labelsPanel.add(this.speedLabel);
		labelsPanel.add(this.tsLabel);
		
		JPanel speedSpinnerWFParametersButtonPanel = new JPanel(new BorderLayout());
		speedSpinnerWFParametersButtonPanel.add(this.speedSpinner, BorderLayout.WEST);
		speedSpinnerWFParametersButtonPanel.add(this.wfParametersButton, BorderLayout.EAST);
		
		JPanel fieldsPanel = new JPanel(new GridLayout(0,1));
		fieldsPanel.add(speedSpinnerWFParametersButtonPanel);
		fieldsPanel.add(this.tsField);
		
		JPanel valuesPanel = new JPanel(new BorderLayout());
		valuesPanel.add(labelsPanel, BorderLayout.WEST);
		valuesPanel.add(fieldsPanel, BorderLayout.CENTER);
		
		BorderLayout b1 = new BorderLayout();
		b1.setVgap(3);
		JPanel controlsPanel = new JPanel(b1);
		controlsPanel.add(valuesPanel, BorderLayout.NORTH);
		controlsPanel.add(buttonsPanel2, BorderLayout.SOUTH);
		
		JPanel controlsPanel2 = new JPanel(new FlowLayout());
		controlsPanel2.add(controlsPanel);
		
		JPanel controlsPanel3 = new JPanel(new BorderLayout());
		controlsPanel3.add(controlsPanel, BorderLayout.SOUTH);
		
		
		JPanel eastPanel = new JPanel(new GridBagLayout());
		eastPanel.add(controlsPanel3);	
		
		BorderLayout b2 = new BorderLayout();
		b2.setHgap(5);
		setLayout(b2);
		add(westPanel, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
	}
	
	private void buildComponents()
	{
		try
		{
			this.timeChart = new WaveformTimePlotsChart(this.wfChart.getAVBFacade());
		}
		catch(Exception e)
		{
			return;
		}
		
		this.slider = new JSlider(JSlider.HORIZONTAL, 0, 800, 0);
		this.slider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				int index = ((JSlider)e.getSource()).getValue();
				wfChart.displayWaveformsAt(index);
			}
		});
		this.slider.setEnabled(false);
		this.slider.setToolTipText("You can move back and forth with your arrow keys");
		
		this.wfParametersButton = new JButton("...");
		this.wfParametersButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					wfChart.getDelayAndPeriodDialog().display(wfParametersButton);
				}
				catch(Exception ex)
				{
					//do nothing
				}
			}
		});
		this.wfParametersButton.setEnabled(false);
		
		this.playButton = new JButton(new ImageIcon(Icons.PLAY_IMAGE));
		this.playButton.setEnabled(false);
		this.playButton.addActionListener(new ActionListener()	
		{
			public void actionPerformed(ActionEvent e)
			{
				playButton.setEnabled(false);
				int delay = (int) (1000/((Number)speedSpinner.getValue()).doubleValue());
				playTimer.setDelay(delay);
				playTimer.start();
			}
		});
		
		this.stopButton = new JButton(new ImageIcon(Icons.STOP_IMAGE));
		this.stopButton.addActionListener(new ActionListener()	
		{
			public void actionPerformed(ActionEvent e)
			{
				stopPlaying();
			}
		});
		
		this.speedLabel = new JLabel("Speed");
		this.speedLabel.setToolTipText("Values per second");
		
		
		this.speedSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 25, 1));

		this.tsLabel = new JLabel("Timestamp ");
		this.tsField = new JTextField(20);
		this.tsField.setEditable(false);
	}
	
	public WFControlPanel(JFreeChartForWaveforms jfc)
	{
		this.wfChart = jfc;
		this.playTimer = new Timer(0, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				play();
			}
		});
		this.playTimer.setRepeats(true);
		
		this.previousPlotHeight = -1;
		this.previousPlotWidth = -1;
		
		buildComponents();
		addComponents();
	}
	
	public void displayGraphs(ValuesContainer[] waveforms) throws Exception
	{
		String[] timeAxesLabels = this.wfChart.getAVBFacade().getTimeAxesLabels();
		
		this.tsField.setText("");
		this.slider.setEnabled(true);
		this.playButton.setEnabled(true);
		this.wfParametersButton.setEnabled(true);
		
		this.timeChart.displayGraphs(waveforms);
		
		if(waveforms.length <= 0)
			return;
		int nrValues = waveforms[0].getNumberOfValues() - 1;
		if(	this.slider.getValue() == 0 &&
			this.slider.getMaximum() == nrValues)
		{
			//change listener wouldn't capture even if we set these parameters again
			this.wfChart.displayWaveformsAt(0);
		}
		else
		{
			this.slider.setValue(0);
			this.slider.setMaximum(nrValues);
		}
	}
	
	public WaveformTimePlotsChart getTimeChart() {
		return this.timeChart;
	}
	
	public void setSliderEnabled(boolean flag)
	{
		this.slider.setEnabled(flag);
	}
	
	public void setTimestamp(double tsInMsecs)
	{
		this.tsField.setText(DATE_FORMAT.format(new Date((long)tsInMsecs)));
	}
	
	public void drawMarker(double tsInMsecs)
	{

		Graphics2D g = (Graphics2D) this.timeChart.getDataChartPanel().getGraphics();
		g.setXORMode(Color.GRAY);
		
		Object previousAntiAliasSettings = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Rectangle2D plotArea = this.timeChart.getZoomablePlotArea();
		
		if(	plotArea.getWidth() != this.previousPlotWidth ||
			plotArea.getHeight() != this.previousPlotHeight)
		{
			this.previousPlotHeight = plotArea.getHeight();
			this.previousPlotWidth = plotArea.getWidth();
		}
		else
		{
			//repaints the current marker; color taken from ChartPanel.java
			g.fill(this.verticalMarker);
		}
		
		double newX = this.timeChart.getDataPlot().getDomainAxis().valueToJava2D(tsInMsecs, plotArea, RectangleEdge.TOP);
		this.verticalMarker = new Rectangle2D.Double(newX, plotArea.getMinY(), 2, plotArea.getHeight());
		g.fill(this.verticalMarker);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, previousAntiAliasSettings);
	}
}
