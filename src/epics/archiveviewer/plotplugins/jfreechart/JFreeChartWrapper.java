package epics.archiveviewer.plotplugins.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPopupMenu;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.Legend;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Spacer;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.AVEntry;
import epics.archiveviewer.DrawType;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.ImagePersistenceBean;
import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.plotplugins.components.menuitems.AVMenuItem;
import epics.archiveviewer.plotplugins.components.menuitems.IgnoreValueMenuItem;
import epics.archiveviewer.plotplugins.components.menuitems.ShowOwnRangeAxisMenuItem;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVCommonRangeAxis;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVOwnNumberAxis;
import epics.archiveviewer.plotplugins.jfreechart.datasets.AVAbstractDataset;
import epics.archiveviewer.plotplugins.jfreechart.renderer.AVLinesRenderer;
import epics.archiveviewer.plotplugins.jfreechart.renderer.AVScatterRenderer;
import epics.archiveviewer.plotplugins.jfreechart.renderer.AVStepsRenderer;

public abstract class JFreeChartWrapper extends PlotPlugin
{		
	public static String generateUniqueLabel(AVEntry ave)
	{
		//to identify own range axes
		return System.currentTimeMillis() + "#" + ave.toString();
	}
	
	private final ArrayList avMenuItems;
	
	protected final HashMap rangeAxisLabelToAxisPlotIndex;
	
	//may be NULL
	private final AVChartPanel dataChartPanel;
	
	protected boolean isAntiAlias;
	
	protected boolean leaveIgnoredItems;
	
	protected RetrievalMethod[] retrievalMethods;
	
	protected JFreeChart dataChart;
	
	protected XYPlot dataPlot;
	
	private void initAVMenuItems(MouseEvent e)
	{
		AVMenuItem avmItem = null;
		for(int i=0; i<this.avMenuItems.size(); i++)
		{
			avmItem = (AVMenuItem) this.avMenuItems.get(i);
			avmItem.setEntity(this.dataChartPanel.getEntityForPoint(e.getX(), e.getY()));
		}
	}
	
	private void addIgnoreMenuItem()
	{
		IgnoreValueMenuItem ivmItem = new IgnoreValueMenuItem(this);
		this.dataChartPanel.getPopupMenu().add(ivmItem);
		this.avMenuItems.add(ivmItem);
	}
	
	private void addShowOwnRangeAxisMenuItem()
	{
		ShowOwnRangeAxisMenuItem soramItem = new ShowOwnRangeAxisMenuItem(this);
		this.dataChartPanel.getPopupMenu().add(soramItem);
		this.avMenuItems.add(soramItem);
	}
	
	private boolean isRangeAxisUsed(String rALabel)
	{
		try
		{
			ValueAxis axis= this.dataPlot.getRangeAxis(((Integer)this.rangeAxisLabelToAxisPlotIndex.get(rALabel)).intValue());
			for(int i=0; i<this.dataPlot.getDatasetCount(); i++)
			{
				if(this.dataPlot.getRangeAxisForDataset(i) == axis)
					return true;
			}
		}
		catch(Exception e)
		{
			//do nothing
		}
		return false;
	}
	
	protected abstract void createEmptyChart();
	
	protected void setPlotFlags()
	{
		this.dataPlot.setBackgroundPaint(getAVBFacade().getPlotBackgroundColor());
		this.dataPlot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 0.0, 2.0, 0.0, 2.0));
		this.dataPlot.setOutlinePaint(null);
	}
	
	public JFreeChartWrapper(AVBaseFacade avbFacade, ImagePersistenceBean ngpip) throws Exception
	{	
		super(avbFacade, ngpip);
		
		this.avMenuItems = new ArrayList();
		
		this.rangeAxisLabelToAxisPlotIndex = new HashMap();
		
		createEmptyChart();
		
		if(getPersistenceParameters() == null)
		{

			this.dataChartPanel = new AVChartPanel(this.dataChart);
			this.dataChartPanel.getChartRenderingInfo().setEntityCollection(new AVEntityCollection());
			this.dataChartPanel.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
		            if (e.isPopupTrigger())
		            	initAVMenuItems(e);
				}
				
				public void mouseReleased(MouseEvent e)
				{
					if (e.isPopupTrigger())
						initAVMenuItems(e);
				}
			});
			
			this.dataChartPanel.getPopupMenu().addSeparator();
		
			addShowOwnRangeAxisMenuItem();
			addIgnoreMenuItem();
		}
		else
			this.dataChartPanel = null;
	}
	
	public AVChartPanel getDataChartPanel()
	{
		return this.dataChartPanel;
	}
	
	public XYPlot getDataPlot()
	{
		return this.dataPlot;
	}
	
	public void createDataChartForDataPlot() {
		this.dataChart = new JFreeChart("", null, this.dataPlot, true);
		this.dataChart.setAntiAlias(this.isAntiAlias);
		this.dataChart.setBackgroundPaint(getAVBFacade().getPlotBackgroundColor());

		Legend legend = this.dataChart.getLegend();

		if (legend instanceof StandardLegend) {
			StandardLegend sL = (StandardLegend) legend;
			sL.setDisplaySeriesShapes(true);
			sL.setOutlinePaint(getAVBFacade().getPlotBackgroundColor());
		}
		legend.setAnchor(Legend.NORTH);
	}
	
	//rangeAxisLabel might be NULL
	public void mapDatasetToRangeAxis(AVEntry ave, int dataSetIndex) throws Exception
	{
		String rangeAxisLabel = getAVBFacade().getRangeAxisLabel(ave);
		int currentRangeAxisIndex = -1;
		if(rangeAxisLabel == null)
		{
			rangeAxisLabel = generateUniqueLabel(ave);
			NumberAxis yAxis = new AVOwnNumberAxis(rangeAxisLabel, getAVBFacade().getColor(ave));
			
			yAxis.setAutoRange(true);
			yAxis.setAutoRangeIncludesZero(false);
			yAxis.setVisible(false);
			
			currentRangeAxisIndex = this.dataPlot.getRangeAxisCount();
			this.dataPlot.setRangeAxis(currentRangeAxisIndex, yAxis);
			this.rangeAxisLabelToAxisPlotIndex.put(rangeAxisLabel, new Integer(currentRangeAxisIndex));
			this.dataPlot.setRangeAxisLocation(currentRangeAxisIndex, AxisLocation.TOP_OR_LEFT);
		}
		else
		{
			currentRangeAxisIndex = ((Integer) this.rangeAxisLabelToAxisPlotIndex
				.get(rangeAxisLabel)).intValue();
		}
		
		this.dataPlot.mapDatasetToRangeAxis(dataSetIndex, currentRangeAxisIndex);
	}
	
	public NumberAxis getRangeAxis(String label)
	{
		int axisIndex = ((Integer) this.rangeAxisLabelToAxisPlotIndex.get(label)).intValue();
		return (NumberAxis) this.dataPlot.getRangeAxis(axisIndex);
	}
	
	public double getSmallestPositiveValue(String rangeAxisLabel)
	{
		double minPosY = Double.MAX_VALUE;
		int datasetIndex = -1;
		AVAbstractDataset avd = null;
		for(int i=0; i<this.dataPlot.getDatasetCount(); i++)
		{
			avd = (AVAbstractDataset) this.dataPlot.getDataset(i);
			try
			{
				if(getAVBFacade().getRangeAxisLabel(avd.getArchiveEntry()).equals(rangeAxisLabel))
				{
					if(minPosY > avd.getSmallestPositiveRangeValue())
						minPosY = avd.getSmallestPositiveRangeValue();
				}
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		if(minPosY == Double.MAX_VALUE)
			//so that log(0.1) will be set as minimum, log(1.05) as maximum
			minPosY = 0.1;
		return minPosY;
	}

	public void setRangeUsingAVBaseSettings(final NumberAxis rangeAxis) 
	{		
		if(rangeAxis instanceof AVOwnNumberAxis)
			return;
		
		try {
			RangeAxisType thisYAxisType = getAVBFacade().getRangeAxisType(rangeAxis.getLabel());
			
			Double bounds[] = getAVBFacade().getRangeAxisBounds(rangeAxis.getLabel());
			if (bounds[0] != null) {
				double min = bounds[0].doubleValue();
				if(thisYAxisType == RangeAxisType.LOG)
				{
					if(min > 0)
						min = Math.log(min);
					else
						min = Math.log(getSmallestPositiveValue(rangeAxis.getLabel()));
				}
				rangeAxis.setLowerBound(min);
			}

			if (bounds[1] != null) {
				double max = bounds[1].doubleValue();
				if(thisYAxisType == RangeAxisType.LOG)
					max = Math.log(max);
				rangeAxis.setUpperBound(max);
			}
			
			//own range axes can not be logarithmic
			if(	rangeAxis instanceof AVCommonRangeAxis &&
				getAVBFacade().getRangeAxisType(rangeAxis.getLabel()) == RangeAxisType.LOG)
				((AVCommonRangeAxis)rangeAxis).setLogarithmic();
		} 
		catch (Exception e) 
		{
			getAVBFacade().displayError("Could not set the range of the y axis " + rangeAxis.getLabel(), e);
		}
	}
	
	public void createLegendForDataset(int index, LegendInfo legendInfo) 
	{
		AVAbstractDataset avd = (AVAbstractDataset) this.dataPlot.getDataset(index);
		if(avd == null)
			return;
		try
		{
			avd.createLegendLabel((NumberAxis)this.dataPlot.getRangeAxisForDataset(index), legendInfo);
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't create legend for AV entry " + avd.getArchiveEntry().toString(), e);
		}
	}

	public void setRendererForDataset(int index, boolean drawLastLineTillTheEdge) 
	{
		AVAbstractDataset avd = (AVAbstractDataset) this.dataPlot.getDataset(index);
		if(avd == null)
			return;
		
		XYItemRenderer r = null;
		AVEntry ae = avd.getArchiveEntry();
		DrawType drawType = getAVBFacade().getDrawType(ae);
		if(drawType == DrawType.LINES)	
			r = new AVLinesRenderer(drawLastLineTillTheEdge);
		else if(drawType == DrawType.SCATTER)
			r = new AVScatterRenderer();
		else
			r = new AVStepsRenderer(drawLastLineTillTheEdge);

		r.setPaint(getAVBFacade().getColor(ae));
		r.setShape(getAVBFacade().getShape(ae));
		r.setStroke(new BasicStroke(getAVBFacade().getDrawWidth(ae)));
		
		this.dataPlot.setRenderer(index, r);
	}
	
	public JPopupMenu getRightClickMenu()
	{
		return this.dataChartPanel.getPopupMenu();
	}

	public double getCorrespondingRangeValue(String rangeAxisLabel, double yCoordinate) {
		try {
			int rangeAxisIndex = ((Integer) this.rangeAxisLabelToAxisPlotIndex.get(rangeAxisLabel)).intValue();
			
			NumberAxis yAxis = (NumberAxis) this.dataPlot.getRangeAxis(rangeAxisIndex);
			//doesn't matter RectangleEde.LEFT or RectangleEdge.RIGHT
			double plotValue = yAxis.java2DToValue(yCoordinate, this.dataChartPanel.getScaledDataArea(), RectangleEdge.LEFT);
			
			if(	yAxis instanceof AVCommonRangeAxis && 
				((AVCommonRangeAxis)yAxis).isLogarithmic() == true)
				return Math.exp(plotValue);
			
			return plotValue;
		}
		catch (Exception e) 
		{
			getAVBFacade().displayError("Couldn't transform a coordinate to the corresponding value of the range axis " + rangeAxisLabel, e);
		}		
		return Double.NaN;
	}

	public double getUpperBoundOfRangeAxis(String rangeAxisLabel) {
		try
		{
			int rangeAxisIndex = ((Integer) this.rangeAxisLabelToAxisPlotIndex.get(rangeAxisLabel)).intValue();
			
			NumberAxis yAxis = (NumberAxis) this.dataPlot.getRangeAxis(rangeAxisIndex);
			
			if(yAxis instanceof AVCommonRangeAxis && isRangeAxisUsed(rangeAxisLabel) == false)
				return Double.NaN;
			
			double plotValue = yAxis.getUpperBound();
			
			if(	yAxis instanceof AVCommonRangeAxis && 
					((AVCommonRangeAxis)yAxis).isLogarithmic() == true)
				return Math.exp(plotValue);
			
			return plotValue;
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't get the upper bound of the range axis " + rangeAxisLabel, e);
		}
		return Double.NaN;
	}

	public double getLowerBoundOfRangeAxis(String rangeAxisLabel) {
		try
		{
			int rangeAxisIndex = ((Integer) this.rangeAxisLabelToAxisPlotIndex.get(rangeAxisLabel)).intValue();
			
			NumberAxis yAxis = (NumberAxis) this.dataPlot.getRangeAxis(rangeAxisIndex);
			
			if(yAxis instanceof AVCommonRangeAxis && isRangeAxisUsed(rangeAxisLabel) == false)
				return Double.NaN;
			
			double plotValue = yAxis.getLowerBound();
			
			if(	yAxis instanceof AVCommonRangeAxis && 
				((AVCommonRangeAxis)yAxis).isLogarithmic() == true)
				return Math.exp(plotValue);
			return plotValue;
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't get the lower bound of the range axis " + rangeAxisLabel, e);
		}
		return Double.NaN;
	}

	public void clear() {
		this.rangeAxisLabelToAxisPlotIndex.clear();
	
		this.dataPlot = null;
		
		this.dataChartPanel.getChartRenderingInfo().clear();
		
		createEmptyChart();
		
		this.dataChartPanel.setChart(this.dataChart);
	}
	
	//x and y are coordinates on the screen
	public void ignoreItemAt(XYItemEntity entity)
	{	
		if(entity != null)
		{
			AVAbstractDataset avd = (AVAbstractDataset)entity.getDataset();
			
			avd.ignorePlotItem(entity.getItem());
			
			this.dataChart.fireChartChanged();
		}
	}
	
	public int getDatasetIndex(XYDataset d)
	{
		for(int i=0; i<this.dataPlot.getDatasetCount(); i++)
		{
			if(this.dataPlot.getDataset(i) == d)
				return i;
		}
		return -1;
	}
	
	public void setOwnRangeAxisVisible(String ownRangeAxisLabel)
	{
		try
		{
			ValueAxis yAxis = null;
			for(int i=0; i<this.dataPlot.getRangeAxisCount(); i++)
			{
				yAxis = this.dataPlot.getRangeAxis(i);
				if(yAxis instanceof AVOwnNumberAxis)
				{
					yAxis.setVisible(yAxis.getLabel().equals(ownRangeAxisLabel));	
				}
				//else leave in peace
			}
		}
		catch(Exception e)
		{
			//do nothing
		}
	}
	
	public void checkIfOnlyOwnRangeAxesUsedAndMakeFirstVisible()
	{
		int firstOwnRangeAxisIndex = -1;
		ValueAxis yAxis = null;
		for(int i=0; i<this.dataPlot.getRangeAxisCount(); i++)
		{
			yAxis = this.dataPlot.getRangeAxis(i);
			if(yAxis instanceof AVOwnNumberAxis) 
			{
				if(firstOwnRangeAxisIndex < 0)
					firstOwnRangeAxisIndex = i;	
			}
			else if(yAxis.isVisible())
				return;				
		}
		if(firstOwnRangeAxisIndex >=0 )
			this.dataPlot.getRangeAxis(firstOwnRangeAxisIndex).setVisible(true);
	}
	
	
	public void setAvailableRetrievalMethods(RetrievalMethod[] rms)
	{
		this.retrievalMethods = rms;
	}
	
	public void setRangeAxisBounds(String rangeAxisLabel, double min, double max)
	{
		if(	Double.isNaN(min) || Double.isInfinite(min) || 
			Double.isNaN(max) || Double.isInfinite(max))
			return;
		try
		{
			Integer rangeAxisIndex = (Integer) this.rangeAxisLabelToAxisPlotIndex.get(rangeAxisLabel);
			
			NumberAxis yAxis = (NumberAxis) this.dataPlot.getRangeAxis(rangeAxisIndex.intValue());
			
			double plotValue = yAxis.getLowerBound();
			
			if(	yAxis instanceof AVCommonRangeAxis && 
				((AVCommonRangeAxis)yAxis).isLogarithmic())
				yAxis.setRange(Math.log(min), Math.log(max));
			else
				yAxis.setRange(min, max);
				
		}
		catch(Exception e)
		{
			getAVBFacade().displayError("Couldn't set new range of the range axis " + rangeAxisLabel, e);
		}
	}
	
	public void setAntiAlias(boolean flag)
	{
	    this.isAntiAlias = flag;
	}
	
	public void setLeaveIgnoredItems(boolean flag)
	{
	    this.leaveIgnoredItems = flag;
	}
	
	public boolean leaveIgnoredItems()
	{
	    return this.leaveIgnoredItems;
	}
}
