/*
 * Created on Dec 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.ui.RectangleEdge;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.ImagePersistenceBean;
import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.plotplugins.components.CorrelatorInputPanel;
import epics.archiveviewer.plotplugins.jfreechart.JFreeChartWrapper;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVCommonRangeAxis;
import epics.archiveviewer.plotplugins.jfreechart.axes.AVOwnNumberAxis;
import epics.archiveviewer.plotplugins.jfreechart.datasets.CorrelatorDataset;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JFreeChartCorrelator extends JFreeChartWrapper
{
	public static final String NAME = "JFreeChart For Correlations";
	
	public static String DESCRIPTION;
	
	static
	{
		StringBuffer sb = new StringBuffer();
		sb.append(NAME);
		sb.append("\n");
		sb.append("This plugin plots non-waveform, non-discrete PVs/formulas against each other. " +
				"The user must select exactly one PV/formula to be the domain, the rest of PVs/formulas " +
				"are plotted as its range.\n" +
				"Please, consult the manual for further information.");
		DESCRIPTION = sb.toString();
	}
	
	private final CorrelatorInputPanel inputPanel;
	
	private final JLabel timeRangeLabel;
	
	private ValuesContainer domainValuesContainer;
	
	private ValuesContainer[] rangeValuesContainers;
	
	private JPanel mainPanel;	
	
	private void setTimeRangeFromVC(ValuesContainer vc) throws Exception
	{
		Date start = new Date((long) vc.getTimestampInMsec(0));
		Date end = new Date((long) vc.getTimestampInMsec(vc.getNumberOfValues() - 1));
		this.timeRangeLabel.setText(
				AVBaseConstants.MAIN_DATE_FORMAT.format(start) +
				" - " +
				AVBaseConstants.MAIN_DATE_FORMAT.format(end)
				);
	}
	
	private boolean createDatasetsAndMapToAxes()
	{
		String currentRangeAxisLabel = null;
		int currentDataSetIndex = 0;
		RangeAxisType currentRangeAxisType = null;

		for (int i = 0; i < this.rangeValuesContainers.length; i++) {
			
			try
			{
				currentRangeAxisLabel = getAVBFacade().getRangeAxisLabel(this.rangeValuesContainers[i].getAVEntry());

				if(currentRangeAxisLabel == null)
					currentRangeAxisType = RangeAxisType.NORMAL;
				else
					currentRangeAxisType = getAVBFacade().getRangeAxisType(currentRangeAxisLabel);
				
				CorrelatorDataset currentDataSet = 
						new CorrelatorDataset(
								this.domainValuesContainer, 
								this.rangeValuesContainers[i], 
								new ArrayList(),
								currentRangeAxisType);
				
				super.dataPlot.setDataset(currentDataSetIndex, currentDataSet);
	
				mapDatasetToRangeAxis(this.rangeValuesContainers[i].getAVEntry(), currentDataSetIndex);
	
				currentDataSetIndex++;
			}
			catch(Exception e)
			{
				getAVBFacade().displayError("Couldn't create dataset for AV entry " +
						this.rangeValuesContainers[i].getAVEntry().toString(), e);
			}
		}

		return 	currentDataSetIndex > 0;
	}
	
	protected void createEmptyChart() {
		super.dataChart = ChartFactory.createXYLineChart(null, "EGU", "EGU", null, 
				PlotOrientation.VERTICAL, true, true, false);
		super.dataChart.setBackgroundPaint(getAVBFacade().getPlotBackgroundColor());
		super.dataPlot = (XYPlot) super.dataChart.getPlot();
		setPlotFlags();
	}
	
	public JFreeChartCorrelator(AVBaseFacade avbFacade, ImagePersistenceBean ngpip)
	throws Exception
	{
		super(avbFacade, ngpip);
		if(ngpip != null)
			throw new Exception("Plot plugin " + JFreeChartCorrelator.class.getName() + " can draw on screen only");
		getDataChartPanel().setVerticalZoom(true);
		getDataChartPanel().setHorizontalZoom(true);
		this.inputPanel = new CorrelatorInputPanel(this);
		this.timeRangeLabel = new JLabel();
	}
	
	public void displayCorrelatedNondiscreteVCs(ValuesContainer domainVC, ValuesContainer[] rangeVCs) throws Exception
	{
		int i = 0;
		this.domainValuesContainer = domainVC;
		this.rangeValuesContainers = rangeVCs;

		super.rangeAxisLabelToAxisPlotIndex.clear();
		getDataChartPanel().getChartRenderingInfo().clear();
		
		super.dataPlot = new XYPlot();
		setPlotFlags();

		//create the domain axes and place them within the plots

		NumberAxis currentDomainAxis = new AVOwnNumberAxis(
				"", 
				getAVBFacade().getColor(this.domainValuesContainer.getAVEntry()));

		currentDomainAxis.setAutoRangeIncludesZero(false);

		super.dataPlot.setDomainAxis(currentDomainAxis);

		String[] rangeAxesLabels = getAVBFacade().getRangeAxesLabels();
		
		RangeAxisLocation rALocation = null;
		
		for (i = 0; i < rangeAxesLabels.length; i++) 
		{
			NumberAxis currentRangeAxis  = new AVCommonRangeAxis(rangeAxesLabels[i]);
	
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
		
		if(createDatasetsAndMapToAxes())
		{			
			for (i = 0; i < super.dataPlot.getRangeAxisCount(); i++) {
				setRangeUsingAVBaseSettings((NumberAxis)super.dataPlot.getRangeAxis(i));
			}
			
			checkIfOnlyOwnRangeAxesUsedAndMakeFirstVisible();
			
			//set the legends and renderers
			for (i = 0; i < super.dataPlot.getDatasetCount(); i++) {
				createLegendForDataset(i, getAVBFacade().getLegendInfo());
				setRendererForDataset(i, false);
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
		
		getDataChartPanel().setChart(super.dataChart);
	}
	
	public void displayGraphs(ValuesContainer[] nonNullVCs) throws Exception {
		if(nonNullVCs.length > 0)
			setTimeRangeFromVC(nonNullVCs[0]);
		
		ArrayList vcsToCorrelate = new ArrayList();
		
		String selectedTimeAxisLabel = getAVBFacade().getSelectedTimeAxisLabel();
		for(int i=0; i<nonNullVCs.length; i++)
		{
			if(	nonNullVCs[i].isWaveform() == false && 
				getAVBFacade().getTimeAxisLabel(nonNullVCs[i].getAVEntry()).equals(selectedTimeAxisLabel))
				vcsToCorrelate.add(nonNullVCs[i]);
		}
		
		this.inputPanel.loadVCs(vcsToCorrelate);
	}
	
	public RetrievalMethod getChosenRetrievalMethod() throws NullPointerException {
		for(int i=0; i<super.retrievalMethods.length; i++)
		{
			if(super.retrievalMethods[i].alignsTimestamps())
				return super.retrievalMethods[i];
		}
		return null;
	}
	
	public Component getComponent() {
		if(this.mainPanel == null)
		{
			JPanel inputPanel2 = new JPanel(new BorderLayout());
			inputPanel2.add(this.inputPanel, BorderLayout.EAST);
			
			JPanel timeRangePanel = new JPanel(new BorderLayout());
			timeRangePanel.add(this.timeRangeLabel, BorderLayout.WEST);
			
			GridLayout g = new GridLayout(1,0);
			g.setHgap(10);		
			JPanel southPanel = new JPanel(g);
			southPanel.add(inputPanel2);
			southPanel.add(timeRangePanel);
			
			this.mainPanel = new JPanel(new BorderLayout());
			this.mainPanel.add(getDataChartPanel(), BorderLayout.CENTER);
			this.mainPanel.add(southPanel, BorderLayout.SOUTH);
		}
		return this.mainPanel;
	}
	
	public double getCorrespondingDomainValue(String xAxisLabel, double xCoordinate) throws IllegalArgumentException 
	{
		return 	super.dataPlot.getDomainAxis().java2DToValue(xCoordinate, getZoomablePlotArea(), RectangleEdge.BOTTOM);
	}
	
	public double getLowerBoundOfDomainAxis(String xAxisLabel) throws IllegalArgumentException {
		return super.dataPlot.getDomainAxis().getLowerBound();
	}
	
	public String getName() {
		return NAME;
	}
	
	public int getPlotPanelWidth() {
		return getDataChartPanel().getWidth();
	}
	
	public double getUpperBoundOfDomainAxis(String xAxisLabel) throws IllegalArgumentException 
	{
		return super.dataPlot.getDomainAxis().getUpperBound();
	}
	
	public Rectangle2D getZoomablePlotArea() {
		//N/A
		return null;
	}
	
	public void clear()
	{
		super.clear();
		this.inputPanel.clear();
	}
	
	public void setDomainAxisBounds(String domainAxisLabel, double min, double max)
	{
		if(	Double.isNaN(min) || Double.isInfinite(min) || 
			Double.isNaN(max) || Double.isInfinite(max))
				return;
		try 
		{
			//only one domain axis
			super.dataPlot.getDomainAxis().setRange(min, max);
		} 
		catch (Exception e) 
		{
			getAVBFacade().displayError("Couldn't set the new range of the domain axis", e);
		}
	}
	
	public boolean isDomainTime()
	{
		return false;
	}
	
	public String[] getDomainAxesLabels()
	{
		return 	new String[] 
			   {
				super.dataPlot.getDomainAxis().getLabel()
			   };
	}
}
