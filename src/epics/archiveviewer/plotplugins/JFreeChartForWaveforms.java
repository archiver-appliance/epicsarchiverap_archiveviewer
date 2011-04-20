/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.ImagePersistenceBean;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.plotplugins.components.DelayAndPeriodDialog;
import epics.archiveviewer.plotplugins.components.WFControlPanel;
import epics.archiveviewer.plotplugins.jfreechart.JFreeChartWrapper;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVCommonRangeAxis;
import epics.archiveviewer.plotplugins.jfreechart.datasets.WaveformXYDataset;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JFreeChartForWaveforms extends JFreeChartWrapper
{
	public static final String NAME = "JFreeChart For Waveforms";
	
	public static String DESCRIPTION;
	
	static
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NAME);
		sb.append("\n");
		sb.append("This plugin plots waveforms against a single time axis. " +
				"It then allows the user to slide through time, simulating the known scope behaviour.\n" +
				"Please, consult the manual for further information.");
		DESCRIPTION = sb.toString();
	}
	
	private final WFControlPanel wfControlPanel;
	
	private final JTextField timeRangeField;
	
	private DelayAndPeriodDialog dapDialog;
	
	private ValuesContainer[] waveforms;
	
	private int currentValueIndex;
	
	private JPanel mainPanel;	
	
	private void displayTimeRangeFromVC(ValuesContainer vc) throws Exception
	{
		Date start = new Date((long) vc.getTimestampInMsec(0));
		Date end = new Date((long) vc.getTimestampInMsec(vc.getNumberOfValues() - 1));
		this.timeRangeField.setText(
				AVBaseConstants.MAIN_DATE_FORMAT.format(start) +
				" - " +
				AVBaseConstants.MAIN_DATE_FORMAT.format(end)
				);
	}
	
	public JFreeChartForWaveforms(AVBaseFacade avbf, ImagePersistenceBean ngpip)
	throws Exception
	{
		super(avbf, ngpip);
		if(ngpip != null)
			throw new Exception("The plugin " + JFreeChartForWaveforms.class.getName() + " can only draw on screen");
		getDataChartPanel().setVerticalZoom(true);
		getDataChartPanel().setHorizontalZoom(true);
		this.wfControlPanel = new WFControlPanel(this);
		this.timeRangeField = new JTextField();
		this.timeRangeField.setBorder(null);
		//for aesthetic purpose only
		this.timeRangeField.setEditable(false);
	}
	
	public DelayAndPeriodDialog getDelayAndPeriodDialog()
	{
		return this.dapDialog;
	}
	
	public JPopupMenu getRightClickMenu() {
		// TODO Auto-generated method stub
		return this.wfControlPanel.getTimeChart().getRightClickMenu();
	}
	
	public void createEmptyChart() {
		super.dataChart = ChartFactory.createXYLineChart(null, null, null, null, 
				PlotOrientation.VERTICAL, true, true, false);
		super.dataChart.setBackgroundPaint(getAVBFacade().getPlotBackgroundColor());
		super.dataPlot = (XYPlot) super.dataChart.getPlot();
		super.dataPlot.getDomainAxis().setVisible(false);
	}
	
	private boolean createDatasetsAndMapToAxes()
	{
		String currentRangeAxisLabel = null;
		int currentDataSetIndex = 0;
		RangeAxisType currentRangeAxisType = null;

		for (int i = 0; i < this.waveforms.length; i++) {
			
			try
			{
				currentRangeAxisLabel = getAVBFacade().getRangeAxisLabel(this.waveforms[i].getAVEntry());
				if(currentRangeAxisLabel == null)
					currentRangeAxisType = RangeAxisType.NORMAL;
				else
					currentRangeAxisType = getAVBFacade().getRangeAxisType(currentRangeAxisLabel);
				

				WaveformXYDataset currentDataSet = 
						new WaveformXYDataset(
								this.waveforms[i], 
								this.dapDialog.getDelay(this.waveforms[i], this.currentValueIndex),
								this.dapDialog.getPeriod(this.waveforms[i], this.currentValueIndex), 
								new ArrayList(), 
								this.currentValueIndex, 
								currentRangeAxisType);
				
				super.dataPlot.setDataset(currentDataSetIndex, currentDataSet);
	
				mapDatasetToRangeAxis(this.waveforms[i].getAVEntry(), currentDataSetIndex);
	
				currentDataSetIndex++;
			}
			catch(Exception e)
			{
				getAVBFacade().displayError("Couldn't create dataset for AV entry " +
						this.waveforms[i].getAVEntry().toString(), e);
			}
		}

		return 	currentDataSetIndex > 0;
	}

	public void displayWaveformsAt(int index)
	{

		int i = 0;
		this.currentValueIndex = index;
		
		super.rangeAxisLabelToAxisPlotIndex.clear();
		getDataChartPanel().getChartRenderingInfo().clear();
		
		super.dataPlot = new XYPlot();
		setPlotFlags();
		
		//set the domain axis
		super.dataPlot.setDomainAxis(new AVCommonRangeAxis(""));
		
		String[] rangeAxesLabels = getAVBFacade().getRangeAxesLabels();
		RangeAxisLocation rALocation = null;
		
		for (i = 0; i < rangeAxesLabels.length; i++) {
			NumberAxis currentRangeAxis = new AVCommonRangeAxis(rangeAxesLabels[i]);
		
			currentRangeAxis.setAutoRangeIncludesZero(false);
	
			this.dataPlot.setRangeAxis(i, currentRangeAxis);
			
			try
			{
				rALocation = getAVBFacade().getRangeAxisLocation(rangeAxesLabels[i]);
				if(rALocation == RangeAxisLocation.LEFT)
					this.dataPlot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
				else if(rALocation == RangeAxisLocation.RIGHT)
					this.dataPlot.setRangeAxisLocation(i, AxisLocation.TOP_OR_RIGHT);
				else
					currentRangeAxis.setVisible(false);
			}
			catch(Exception e)
			{
				//do nothing
			}
			this.rangeAxisLabelToAxisPlotIndex.put(rangeAxesLabels[i], new Integer(i));
		}

		if(createDatasetsAndMapToAxes())
		{			
			for (i = 0; i < super.dataPlot.getRangeAxisCount(); i++) {
				setRangeUsingAVBaseSettings((NumberAxis)super.dataPlot.getRangeAxis(i));
			}
			
			checkIfOnlyOwnRangeAxesUsedAndMakeFirstVisible();
			
			//override legend info
			LegendInfo legendInfo = new LegendInfo();
			legendInfo.setShowAVEName(getAVBFacade().getLegendInfo().getShowAVEName());
			legendInfo.setShowArchiveName(getAVBFacade().getLegendInfo().getShowArchiveName());
			legendInfo.setShowRange(getAVBFacade().getLegendInfo().getShowRange());
			legendInfo.setShowUnits(false);
			//set the legends and renderers
			for (i = 0; i < super.dataPlot.getDatasetCount(); i++) {
				createLegendForDataset(i, legendInfo);
				setRendererForDataset(i, false);
			}
			try
			{
				double timestamp = this.waveforms[0].getTimestampInMsec(this.currentValueIndex);
				
				this.wfControlPanel.drawMarker(timestamp);
				this.wfControlPanel.setTimestamp(timestamp);
			}
			catch(Exception e)
			{
				getAVBFacade().displayError("Can't display initial waveform", e);
			}
		}		
		else
		{
			super.dataPlot.setDataset(null);
			super.dataPlot.setNoDataMessage("No Data Available");
			super.dataPlot.setRenderer(new StandardXYItemRenderer());
		}
		
		createDataChartForDataPlot();
		
		super.dataChart.setTitle(new TextTitle(getAVBFacade().getPlotTitle(), JFreeChart.DEFAULT_TITLE_FONT.deriveFont(15f)));
		
		getDataChartPanel().setChart(super.dataChart);
	}
	
	public void repaint()
	{
		displayWaveformsAt(this.currentValueIndex);
	}
	
	public void displayGraphs(ValuesContainer[] nonNullVCs) throws Exception 
	{
		if(nonNullVCs.length > 0)
			displayTimeRangeFromVC(nonNullVCs[0]);
		else
			this.timeRangeField.setText("");
		
		ArrayList wfVCs = new ArrayList();
		ArrayList nonWfVCs = new ArrayList();
		String selectedTimeAxisLabel = getAVBFacade().getSelectedTimeAxisLabel();
		for(int i=0; i<nonNullVCs.length; i++)
		{
			if(getAVBFacade().getTimeAxisLabel(nonNullVCs[i].getAVEntry()).equals(selectedTimeAxisLabel))
			{
				if(	nonNullVCs[i].isWaveform())
					wfVCs.add(nonNullVCs[i]);
				else
					nonWfVCs.add(nonNullVCs[i]);
			}
		}
		this.waveforms = (ValuesContainer[]) wfVCs.toArray(new ValuesContainer[wfVCs.size()]);

		ValuesContainer[] nonWaveforms = (ValuesContainer[]) nonWfVCs.toArray(new ValuesContainer[nonWfVCs.size()]);
		
		this.dapDialog = new DelayAndPeriodDialog(this, this.waveforms, nonWaveforms);
		
		this.wfControlPanel.displayGraphs(this.waveforms);
	}
	
	//lazy initialization
	public Component getComponent() {
		if(this.mainPanel == null)
		{
			JPanel p = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			JPanel southPanel = new JPanel(new GridBagLayout());
			southPanel.add(this.timeRangeField, gbc);
			
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			p.add(getDataChartPanel(), gbc);
			gbc.gridx = 0;
			gbc.insets = new Insets(5,0,0,0);
			gbc.weighty = 0.0;
			p.add(this.wfControlPanel, gbc);	
			
			this.mainPanel = new JPanel(new BorderLayout());
			this.mainPanel.add(p, BorderLayout.CENTER);
			this.mainPanel.add(southPanel, BorderLayout.SOUTH);
			
		}
		return this.mainPanel;
	}
	
	public String getName() {
		return NAME;
	}
	
	public double getUpperBoundOfDomainAxis(String xAxisLabel) throws IllegalArgumentException {
		return this.wfControlPanel.getTimeChart().getUpperBoundOfTimeAxis(xAxisLabel);
	}
	
	public double getLowerBoundOfDomainAxis(String xAxisLabel) throws IllegalArgumentException {
		return this.wfControlPanel.getTimeChart().getLowerBoundOfTimeAxis(xAxisLabel);
	}
	
	public double getCorrespondingDomainValue(String xAxisLabel, double xCoordinate)
			throws IllegalArgumentException {
		return this.wfControlPanel.getTimeChart().getCorrespondingTimestamp(xAxisLabel, xCoordinate);
	}
	
	public double getCorrespondingRangeValue(String rangeAxisLabel, double yCoordinate) {
		return this.wfControlPanel.getTimeChart().getCorrespondingRangeValue(rangeAxisLabel, yCoordinate);
	}
	
	public Rectangle2D getZoomablePlotArea() 
	{
		return this.wfControlPanel.getTimeChart().getZoomablePlotArea();
	}
	
	public void clear() 
	{
		//stuff to clear before
		super.clear();
		this.wfControlPanel.setSliderEnabled(false);
		this.wfControlPanel.getTimeChart().clear();
	}
	
	public RetrievalMethod getChosenRetrievalMethod()
	{
		for(int i=0; i<super.retrievalMethods.length; i++)
		{
			if(super.retrievalMethods[i].alignsTimestamps())
				return super.retrievalMethods[i];
		}
		return null;
	}
	
	public int getPlotPanelWidth()
	{
		return this.wfControlPanel.getTimeChart().getDataChartPanel().getWidth();
	}
	
	public void setDomainAxisBounds(String timeAxisLabel, double min, double max)
	{
		if(	Double.isNaN(min) || Double.isInfinite(min) || 
			Double.isNaN(max) || Double.isInfinite(max))
				return;
		try 
		{
			//only one time axis
			this.wfControlPanel.getTimeChart().getDataPlot().getDomainAxis().setRange(min, max);
		} 
		catch (Exception e) 
		{
			getAVBFacade().displayError("Couldn't set the new range of the time axis", e);
		}
	}
	
	public double getLowerBoundOfRangeAxis(String rangeAxisLabel) {
		return this.wfControlPanel.getTimeChart().getLowerBoundOfRangeAxis(rangeAxisLabel);
	}

	public double getUpperBoundOfRangeAxis(String rangeAxisLabel) {
		return this.wfControlPanel.getTimeChart().getUpperBoundOfRangeAxis(rangeAxisLabel);
	}

	public void setRangeAxisBounds(String rangeAxisLabel, double min, double max) {
		this.wfControlPanel.getTimeChart().setRangeAxisBounds(rangeAxisLabel, min, max);
	}
	
	public boolean isDomainTime()
	{
		return true;
	}
	
	public String[] getDomainAxesLabels()
	{
		return 	new String[] 
			   {
					this.wfControlPanel.getTimeChart().getDataPlot().getDomainAxis().getLabel()
			   };
	}
}
