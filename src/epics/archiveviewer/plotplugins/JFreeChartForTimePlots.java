package epics.archiveviewer.plotplugins;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.ui.RectangleEdge;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.ImagePersistenceBean;
import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.plotplugins.jfreechart.JFreeChartPlotImageGenerator;
import epics.archiveviewer.plotplugins.jfreechart.JFreeChartWrapper;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVCommonRangeAxis;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVTimeAxis;
import epics.archiveviewer.plotplugins.jfreechart.datasets.AVAbstractDataset;
import epics.archiveviewer.plotplugins.jfreechart.datasets.NonWaveformTSDataset;
import epics.archiveviewer.plotplugins.jfreechart.datasets.WaveformTSDataset;

public class JFreeChartForTimePlots extends JFreeChartWrapper
{

	public static final String BASIC_DATE_FORMAT_STRING = "MM-dd-yy";
	
	public static final String NAME = "JFreeChart For Time Plots";
	
	public static String DESCRIPTION;
	
	static
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NAME);
		sb.append("\n");
		sb.append("This plugin plots PVs against several time axes.\n" +
				"Please, consult the manual for further information.");
		DESCRIPTION = sb.toString();
	}
	
	private final HashMap vcToDatasetPlotIndex;

	private final HashMap timeAxisLabelToAxisPlotIndex;

	private ValuesContainer[] valuesContainers;

	private void mapDatasetToTimeAxis(int dataSetIndex,	String timeAxisLabel)
	{
		Integer currentTimeAxisIndex = (Integer) this.timeAxisLabelToAxisPlotIndex.get(timeAxisLabel);
		dataPlot.mapDatasetToDomainAxis(dataSetIndex, currentTimeAxisIndex.intValue());
	}

	private boolean createDatasetsAndMapToAxes(HashMap vcToListOfIgnoredValueIndices) throws Exception {
		String currentTimeAxisLabel = null;
		String currentRangeAxisLabel = null;
		int currentDataSetIndex = 0;
		RangeAxisType currentRangeAxisType = null;

		for (int i = 0; i < this.valuesContainers.length; i++) {
			
			currentTimeAxisLabel = getAVBFacade().getTimeAxisLabel(this.valuesContainers[i].getAVEntry());
			currentRangeAxisLabel = getAVBFacade().getRangeAxisLabel(this.valuesContainers[i].getAVEntry());

			if(currentRangeAxisLabel == null)
				currentRangeAxisType = RangeAxisType.NORMAL;
			else
				currentRangeAxisType = getAVBFacade().getRangeAxisType(currentRangeAxisLabel);
			AVAbstractDataset currentDataSet = null;
			try 
			{
				ArrayList aL = (ArrayList) vcToListOfIgnoredValueIndices.get(this.valuesContainers[i]);
				if(this.valuesContainers[i].isWaveform())
					currentDataSet = new WaveformTSDataset(this.valuesContainers[i], aL, currentRangeAxisType);
				else
					currentDataSet = new NonWaveformTSDataset(this.valuesContainers[i], aL, currentRangeAxisType);
			} 
			catch (Exception e) {
				//next dataset
				continue;
			}
			
			super.dataPlot.setDataset(currentDataSetIndex, currentDataSet);

			mapDatasetToTimeAxis(currentDataSetIndex, currentTimeAxisLabel);
			mapDatasetToRangeAxis(this.valuesContainers[i].getAVEntry(), currentDataSetIndex);

			this.vcToDatasetPlotIndex.put(this.valuesContainers[i], new Integer(currentDataSetIndex));

			currentDataSetIndex++;
		}

		return 	!this.vcToDatasetPlotIndex.isEmpty();
	}
	
	private void setTicks(DateAxis timeAxis)
	{
		//in msecs
		double range = timeAxis.getUpperBound() - timeAxis.getLowerBound();
		//chosen using psychological tricks (as help, we assume we can fit at most 10 ticks)
		final int PARAMETER = 5;
		
		StringBuffer dfSb = new StringBuffer(BASIC_DATE_FORMAT_STRING);
		if(range <  PARAMETER * 24 * 60 * 60 * 1000)
		{
			//if range < PARAMETER days, display hours
			dfSb.append(" HH");
			if(range < PARAMETER * 60 * 60 * 1000)
			{
				//if range < PARAMETER hours, display minutes
				dfSb.append(":mm");
				if(range < PARAMETER * 60 * 1000)
				{
					//if range < PARAMETER minutes, display seconds
					dfSb.append(":ss");
					if(range < PARAMETER * 1000)
						//if range < PARAMETER seconds, display milliseconds
						dfSb.append(".SSS");
				}
			}
			else
				dfSb.append("'h'");
		}
		
		timeAxis.setDateFormatOverride(new SimpleDateFormat(dfSb.toString()));
	}
	
	public JFreeChartForTimePlots(AVBaseFacade avbFacade, ImagePersistenceBean ngpip) throws Exception
	{
		super(avbFacade, ngpip);
		this.vcToDatasetPlotIndex = new HashMap();
		this.timeAxisLabelToAxisPlotIndex = new HashMap();	
	}
	
	public void createEmptyChart() {
		super.dataChart = ChartFactory.createXYLineChart(null, "Time", "EGU", null, 
				PlotOrientation.VERTICAL, true, true, false);
		super.dataChart.setBackgroundPaint(getAVBFacade().getPlotBackgroundColor());
		super.dataPlot = (XYPlot) super.dataChart.getPlot();
		setPlotFlags();
	}


	public void displayGraphs(ValuesContainer[] vcs) throws Exception 
	{			
		int i = 0;
		this.valuesContainers = vcs;
		
		HashMap vcToListOfIgnoredValueIndices = new HashMap();
		//if necessary, save ignored value indices
		if(	this.valuesContainers.length > 0 &&
			this.dataPlot != null &&
			leaveIgnoredItems() == true &&
			this.vcToDatasetPlotIndex.isEmpty() == false)
		{
			Object dataset = null;
			Integer datasetIndex = null;
			for(i=0; i<this.valuesContainers.length; i++)
			{
				datasetIndex = (Integer) this.vcToDatasetPlotIndex.get(this.valuesContainers[i]);
				if(datasetIndex != null)
				{
					dataset = this.dataPlot.getDataset(datasetIndex.intValue());
					if(dataset instanceof AVAbstractDataset)
						vcToListOfIgnoredValueIndices.put(this.valuesContainers[i], ((AVAbstractDataset)dataset).getIgnoredIndices());
				}
			}				
		}

		this.vcToDatasetPlotIndex.clear();
		this.timeAxisLabelToAxisPlotIndex.clear();
		super.rangeAxisLabelToAxisPlotIndex.clear();
		if(getPersistenceParameters() == null)
			getDataChartPanel().getChartRenderingInfo().clear();
		
		super.dataPlot = new XYPlot();
		setPlotFlags();

		//create the domain axes and place them within the plots
		String[] timeAxesLabels = getAVBFacade().getTimeAxesLabels();
		
		TimeAxisLocation tALocation = null;
		Date[] dates = null;

		for (i = 0; i < timeAxesLabels.length; i++) {
			DateAxis currentTimeAxis = new AVTimeAxis(timeAxesLabels[i]);

			super.dataPlot.setDomainAxis(i, currentTimeAxis);

			this.timeAxisLabelToAxisPlotIndex.put(timeAxesLabels[i], new Integer(i));

			//another approach to setting the axis ranges can be seen below
			currentTimeAxis.setAutoRange(false);
			dates = getAVBFacade().getTimeAxisBounds(timeAxesLabels[i]);
			currentTimeAxis.setLowerBound(dates[0].getTime());
			currentTimeAxis.setUpperBound(dates[1].getTime());

			tALocation = getAVBFacade().getTimeAxisLocation(timeAxesLabels[i]);
			
			if(tALocation == TimeAxisLocation.TOP)
				super.dataPlot.setDomainAxisLocation(i, AxisLocation.TOP_OR_LEFT);
			else if(tALocation == TimeAxisLocation.BOTTOM)
				super.dataPlot.setDomainAxisLocation(i, AxisLocation.BOTTOM_OR_RIGHT);
			else
				currentTimeAxis.setVisible(false);
		}

		String[] rangeAxesLabels = getAVBFacade().getRangeAxesLabels();
		RangeAxisLocation rALocation = null;
		
		for (i = 0; i < rangeAxesLabels.length; i++) {
			
			NumberAxis currentRangeAxis = new AVCommonRangeAxis(rangeAxesLabels[i]);
	
			currentRangeAxis.setAutoRangeIncludesZero(false);
	
			this.dataPlot.setRangeAxis(i, currentRangeAxis);
			
			rALocation = getAVBFacade().getRangeAxisLocation(rangeAxesLabels[i]);
			if(rALocation == RangeAxisLocation.LEFT)
				this.dataPlot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
			else if(rALocation == RangeAxisLocation.RIGHT)
				this.dataPlot.setRangeAxisLocation(i, AxisLocation.TOP_OR_RIGHT);
			else
				currentRangeAxis.setVisible(false);
			
			this.rangeAxisLabelToAxisPlotIndex.put(rangeAxesLabels[i], new Integer(i));
		}		
		
		if(createDatasetsAndMapToAxes(vcToListOfIgnoredValueIndices))
		{			
			for	(i = 0; i<super.dataPlot.getDomainAxisCount(); i++)
			{
				setTicks((DateAxis)super.dataPlot.getDomainAxis(i));
			}
			
			for (i = 0; i < super.dataPlot.getRangeAxisCount(); i++) {
				setRangeUsingAVBaseSettings((NumberAxis)super.dataPlot.getRangeAxis(i));
			}
			
			checkIfOnlyOwnRangeAxesUsedAndMakeFirstVisible();
			
			//set the legends and renderers
			for (i = 0; i < super.dataPlot.getDatasetCount(); i++) {
				createLegendForDataset(i, getAVBFacade().getLegendInfo());
				setRendererForDataset(i, true);
			}
		}		
		else
		{
			super.dataPlot.setDataset(null);
			super.dataPlot.setNoDataMessage("No Data Available");
			super.dataPlot.setRenderer(new StandardXYItemRenderer());
		}
		
		createDataChartForDataPlot();

		super.dataChart.setTitle(getAVBFacade().getPlotTitle());
		
		if(getPersistenceParameters() != null)
		{
			ImagePersistenceBean ngpip = getPersistenceParameters();
			try
			{
				BufferedImage bI = ngpip.getBufferedImage();
				JFreeChartPlotImageGenerator.generate(
	        			bI,
	        			super.dataChart,
	        			new Dimension(bI.getWidth(), bI.getHeight()),
	        			new Insets(0,0,0,0),
	        			null,
	        			new ChartRenderingInfo()
	        			);
				String jfreeIF = null;
				if(ngpip.getImageFormat() == epics.archiveviewer.ImageFileFormat.JPEG)
					jfreeIF = ImageFormat.JPEG;
				else
					jfreeIF = ImageFormat.PNG;
				
				EncoderUtil.writeBufferedImage(bI, jfreeIF, new FileOutputStream(ngpip.getFile()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else 
			getDataChartPanel().setChart(super.dataChart);
	}

	public Rectangle2D getZoomablePlotArea() {
		//taken from ChartPanel.java
		return getDataChartPanel().getScaledDataArea();
	}

	public Component getComponent() {
		return getDataChartPanel();
	}
	
	public double getCorrespondingDomainValue(String timeAxisLabel, double xCoordinate) {
		try 
		{
			int timeAxisIndex = ((Integer) this.timeAxisLabelToAxisPlotIndex.get(timeAxisLabel)).intValue();
			
			//doesn't matter whether RectangleEge.BOTTOM or .TOP
			return 	super.dataPlot.getDomainAxis(timeAxisIndex).
					java2DToValue(xCoordinate, getZoomablePlotArea(), RectangleEdge.BOTTOM);
		} 
		catch (Exception e) 
		{
			getAVBFacade().displayError(
					"Couldn't transform a coordinate to the corresponding timestamp of the axis " + timeAxisLabel, e);
		}
		return Double.NaN;
	}

	public double getUpperBoundOfDomainAxis(String timeAxisLabel) {
		try
		{
			int timeAxisIndex = ((Integer) this.timeAxisLabelToAxisPlotIndex.get(timeAxisLabel))
					.intValue();
			return super.dataPlot.getDomainAxis(timeAxisIndex).getUpperBound();
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't get the upper bound of the time axis " + timeAxisLabel, e);
			return -1;
		}
	}

	public double getLowerBoundOfDomainAxis(String timeAxisLabel) {
		try
		{
			int timeAxisIndex = ((Integer) this.timeAxisLabelToAxisPlotIndex.get(timeAxisLabel))
					.intValue();
			return super.dataPlot.getDomainAxis(timeAxisIndex).getLowerBound();
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't get the lower bound of the time axis " + timeAxisLabel, e);
			return -1;
		}
	}

	/** Clears the entire chart */
	public void clear() {
		this.vcToDatasetPlotIndex.clear();
		this.timeAxisLabelToAxisPlotIndex.clear();
		
		super.clear();
	}
	
	public String getName()
	{
		return NAME;
	}
	
	public RetrievalMethod getChosenRetrievalMethod()
	{
		for(int i=0; i<super.retrievalMethods.length; i++)
		{
			if(super.retrievalMethods[i].reducesResolution() == true)
					return super.retrievalMethods[i];
		}
		return null;
	}
	
	public int getPlotPanelWidth()
	{
		return getDataChartPanel().getWidth();
	}
	
	public void setDomainAxisBounds(String timeAxisLabel, double min, double max)
	{
		if(	Double.isNaN(min) || Double.isInfinite(min) || 
			Double.isNaN(max) || Double.isInfinite(max))
				return;
		try 
		{
			int timeAxisIndex = ((Integer) this.timeAxisLabelToAxisPlotIndex.get(timeAxisLabel)).intValue();
			
			//doesn't matter whether RectangleEge.BOTTOM or .TOP
			super.dataPlot.getDomainAxis(timeAxisIndex).setRange(min, max);
		} 
		catch (Exception e) 
		{
			getAVBFacade().displayError("Couldn't set new range of time axis " + timeAxisLabel, e);
		}
	}
	
	public boolean isDomainTime()
	{
		return true;
	}
	
	public String[] getDomainAxesLabels()
	{
		int s = this.timeAxisLabelToAxisPlotIndex.size();
		return (String[])  this.timeAxisLabelToAxisPlotIndex.keySet().toArray(new String[s]);
	}
}
