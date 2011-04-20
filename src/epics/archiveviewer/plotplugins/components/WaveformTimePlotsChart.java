package epics.archiveviewer.plotplugins.components;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.ui.RectangleEdge;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.plotplugins.JFreeChartForTimePlots;
import epics.archiveviewer.plotplugins.jfreechart.JFreeChartWrapper;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVCommonRangeAxis;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVTimeAxis;
import epics.archiveviewer.plotplugins.jfreechart.datasets.AVAbstractDataset;
import epics.archiveviewer.plotplugins.jfreechart.datasets.NonWaveformTSDataset;
import epics.archiveviewer.plotplugins.jfreechart.datasets.WaveformTSDataset;

public class WaveformTimePlotsChart extends JFreeChartForTimePlots
{
	private ValuesContainer[] valuesContainers;
	
	private boolean createDatasetsAndMapToAxes() throws Exception {
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
				if(this.valuesContainers[i].isWaveform())
					currentDataSet = new WaveformTSDataset(
							this.valuesContainers[i], 
							new ArrayList(), 
							currentRangeAxisType);
				else
					currentDataSet = new NonWaveformTSDataset(
							this.valuesContainers[i], 
							new ArrayList(), 
							currentRangeAxisType);
			} 
			catch (Exception e) 
			{
				//next dataset
				continue;
			}
			
			super.dataPlot.setDataset(currentDataSetIndex, currentDataSet);
			
			super.dataPlot.mapDatasetToDomainAxis(currentDataSetIndex, 0);

			mapDatasetToRangeAxis(this.valuesContainers[i].getAVEntry(), currentDataSetIndex);

			currentDataSetIndex++;
		}

		return 	currentDataSetIndex > 0;
	}

	public WaveformTimePlotsChart(AVBaseFacade avbF) throws Exception
	{	
		super(avbF, null);
	}
	
	public void createEmptyChart()
	{
		super.dataChart = ChartFactory.createXYLineChart(null, null, null, null,
				PlotOrientation.VERTICAL, false, false, false);
		super.dataChart.setBackgroundPaint(getAVBFacade().getPlotBackgroundColor());
		super.dataPlot = (XYPlot) super.dataChart.getPlot();
		setPlotFlags();		
		super.dataPlot.getDomainAxis().setVisible(false);
		super.dataPlot.getRangeAxis().setVisible(false);
	}
	
	public void setPlotFlags()
	{
		super.setPlotFlags();
		super.dataPlot.setDomainGridlinesVisible(false);
		super.dataPlot.setRangeGridlinesVisible(false);
		super.dataPlot.setDomainCrosshairVisible(false);
		super.dataPlot.setRangeCrosshairVisible(false);
	}

	public void displayGraphs(ValuesContainer[] vcs) throws Exception 
	{			
		int i = 0;
		this.valuesContainers = vcs;
		
		super.rangeAxisLabelToAxisPlotIndex.clear();
		getDataChartPanel().getChartRenderingInfo().clear();
		
		super.dataPlot = new XYPlot();
		setPlotFlags();

		//create the domain axes and place them within the plots
		String timeAxesLabel = getAVBFacade().getSelectedTimeAxisLabel();

		DateAxis currentTimeAxis = new AVTimeAxis(timeAxesLabel);

		super.dataPlot.setDomainAxis(currentTimeAxis);

		//another approach to setting the axis ranges can be seen below
		currentTimeAxis.setAutoRange(false);
		
		Date[] dates = getAVBFacade().getTimeAxisBounds(timeAxesLabel);
		currentTimeAxis.setLowerBound(dates[0].getTime());
		currentTimeAxis.setUpperBound(dates[1].getTime());

		currentTimeAxis.setVisible(false);
		
		String[] rangeAxesLabels = getAVBFacade().getRangeAxesLabels();
		
		for (i = 0; i < rangeAxesLabels.length; i++) {
			NumberAxis currentRangeAxis = new AVCommonRangeAxis(rangeAxesLabels[i]);
			
			currentRangeAxis.setVisible(false);	
			currentRangeAxis.setAutoRangeIncludesZero(false);
	
			this.dataPlot.setRangeAxis(i, currentRangeAxis);
			
			this.rangeAxisLabelToAxisPlotIndex.put(rangeAxesLabels[i], new Integer(i));
		}
		
		if(createDatasetsAndMapToAxes())
		{			
			
			for (i = 0; i < super.dataPlot.getRangeAxisCount(); i++) {
				setRangeUsingAVBaseSettings((NumberAxis)super.dataPlot.getRangeAxis(i));
			}
			
			//set the renderers
			for (i = 0; i < super.dataPlot.getDatasetCount(); i++) {
				setRendererForDataset(i, true);
			}
		}		
		else
		{
			super.dataPlot.setDataset(null);
			super.dataPlot.setNoDataMessage("No Waveform Data Available");
			super.dataPlot.setRenderer(new StandardXYItemRenderer());
		}
		
		createDataChartForDataPlot();
		super.dataChart.setLegend(null);
		getDataChartPanel().setChart(super.dataChart);
	}

	public Rectangle2D getZoomablePlotArea() {
		return getDataChartPanel().getScaledDataArea();
	}
	
	public double getCorrespondingTimestamp(String timeAxisLabel, double xCoordinate) {
		try 
		{
			//doesn't matter whether RectangleEge.BOTTOM or .TOP
			return 	super.dataPlot.getDomainAxis().
					java2DToValue(xCoordinate, getZoomablePlotArea(), RectangleEdge.BOTTOM);
		} 
		catch (Exception e) 
		{
			getAVBFacade().displayError("Couldn't transform a coordinate to the corresponding timestamp of the axis " + timeAxisLabel, e);
		}
		return Double.NaN;
	}

	
	public double getUpperBoundOfTimeAxis(String timeAxisLabel) {
		try
		{
			return super.dataPlot.getDomainAxis().getUpperBound();
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't get the upper bound of the time axis " + timeAxisLabel, e);
			return -1;
		}
	}

	public double getLowerBoundOfTimeAxis(String timeAxisLabel) {
		try
		{
			return super.dataPlot.getDomainAxis().getLowerBound();
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't get the lower bound of the time axis " + timeAxisLabel, e);
			return -1;
		}
	}
}
