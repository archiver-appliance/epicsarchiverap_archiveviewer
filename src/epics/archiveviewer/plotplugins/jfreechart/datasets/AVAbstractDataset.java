/*
 * Created on Aug 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.datasets;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.DomainInfo;
import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.xy.XYDataset;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.AVEntry;
import epics.archiveviewer.LegendInfo;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.ValuesContainer;

/**
 * @author Sergei Chevtsov
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class AVAbstractDataset extends AbstractSeriesDataset implements XYDataset, DomainInfo, RangeInfo 
{	
	//some arbitrary values
	protected static final Number MIN_IN_CASE_VALUE_DOES_NOT_EXIST = new Double(0);
	
	protected static final Number MIN_POS_IN_CASE_VALUE_DOES_NOT_EXIST = new Double(1);
	
	protected static final Number MAX_IN_CASE_VALUE_DOES_NOT_EXIST = new Double(2);
	
	//does NOT necessarily return the value index inside the VC!!!
	protected final static int getActualDataIndexAfterConsideringIgnoredItems(ArrayList sortedIgnoredIndices, int plotItem)
	{
		int itemIndex = plotItem;
		if(sortedIgnoredIndices.isEmpty())
			return itemIndex;
		int ignoredItemIndex = -1;
		int i=0;
		for(i = 0; i<sortedIgnoredIndices.size(); i++)
		{
			ignoredItemIndex = ((Integer)sortedIgnoredIndices.get(i)).intValue();
			if(itemIndex < ignoredItemIndex)
				break;
			else
				itemIndex++;
		}
		return itemIndex;
	}
	
	protected static final void ignoreValueCurrentlyPlottedAtNumber(ArrayList sortedIgnoredIndices, int itemIndex)
	{
		//sorted
		int dataIndex = getActualDataIndexAfterConsideringIgnoredItems(sortedIgnoredIndices, itemIndex);
		int i = 0;
		for(i=0; i<sortedIgnoredIndices.size(); i++)
		{
			if(dataIndex < ((Integer)sortedIgnoredIndices.get(i)).intValue())
			{
				sortedIgnoredIndices.add(i, new Integer(dataIndex));
				return;
			}
		}
		sortedIgnoredIndices.add(new Integer(dataIndex));
	}
	
	public static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	//rather a reminder, actually
	public static final int NR_OF_SERIES = 1;

	public static double LOG(Number x) {
		return Math.log(x.doubleValue());
	}

	//the actual data is here
	protected final ValuesContainer valuesContainer;

	protected final RangeAxisType correspondingYAxisType;
	
	//sorted asc
	protected final ArrayList sortedIgnoredDataIndices;

	protected double minX;

	protected double maxX;
	
	private String legendLabel;

	protected AVAbstractDataset(ValuesContainer vc, ArrayList ignoredIndices, RangeAxisType yAxisType)
			throws Exception {
		if (vc == null || vc.getNumberOfValues() == 0)
			throw new NullPointerException();

		this.valuesContainer = vc;
		
		if(ignoredIndices != null)
			this.sortedIgnoredDataIndices = ignoredIndices;
		else
			this.sortedIgnoredDataIndices = new ArrayList(10);

		this.correspondingYAxisType = yAxisType;
	}
	
	public final RangeAxisType getCorrespondingYAxisType()
	{
		return this.correspondingYAxisType;
	}
	
	public final AVEntry getArchiveEntry() {
		return this.valuesContainer.getAVEntry();
	}
	
	public final ArrayList getIgnoredIndices()
	{
		return this.sortedIgnoredDataIndices;
	}

	public final void createLegendLabel(NumberAxis rangeAxis, LegendInfo li) {
		StringBuffer sb = new StringBuffer();
		
		if(li.getShowAVEName())
		{
			sb.append(this.valuesContainer.getAVEntry().getName());
			sb.append(" ");
		}
		
		if(li.getShowArchiveName())
		{
			sb.append(this.valuesContainer.getAVEntry().getArchiveDirectory().getName());
			sb.append(" ");
		}
		boolean appendBrackets = true;
		if(li.getShowRange())
		{
			appendBrackets = false;
			sb.append("(");
			
			sb.append(this.valuesContainer.getRangeLabel(":"));
			
			if (this.valuesContainer.isDiscrete()) 
				sb.append(" discrete ");
		}		
		if (this.correspondingYAxisType == RangeAxisType.LOG)
		{
			if(appendBrackets)
			{
				appendBrackets = false;
				sb.append("(");
			}
			sb.append(" LOG ");
		}

		if(li.getShowUnits())
		{
			if(appendBrackets)
			{
				appendBrackets = false;
				sb.append("(");
			}
			try
			{
				sb.append(this.valuesContainer.getUnits());
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		
		if(appendBrackets == false)
			sb.append(")");
		
		this.legendLabel = sb.toString();
	}

	public final DomainOrder getDomainOrder() {
		return DomainOrder.ASCENDING;
	}
	
	public abstract void ignorePlotItem(int itemIndex);
	
	public abstract double getSmallestPositiveRangeValue();

	public double getXValue(int series, int item) {
		try
		{
			if (series == 0)
			{			
				return getX(series, item).doubleValue();
			}
		}
		catch(Exception e)
		{
			//do nothing
		}
		return Double.NaN;
	}


	public double getYValue(int series, int item) {
		try 
		{
			if (series == 0)
			{
				return getY(series, item).doubleValue();
			}
		} 
		catch (Exception e) {
			//do nothing
		}
		return Double.NaN;
	}

	public Range getDomainRange() {
		return new Range(this.minX, this.maxX);
	}

	public Number getMaximumDomainValue() {
		return new Double(maxX);
	}

	public Number getMinimumDomainValue() {
		return new Double(minX);
	}

	public Range getValueRange() {
		return new Range(getMinimumRangeValue().doubleValue(),
				getMaximumRangeValue().doubleValue());
	}

	public int getSeriesCount() {
		return NR_OF_SERIES;
	}

	public String getSeriesName(int series) {
		// TODO Auto-generated method stub
		if (series == 0) {
			//create it in real time
			return this.legendLabel;
		}
		return null;
	}
	
	public abstract String getToolTip(int series, int item);
}