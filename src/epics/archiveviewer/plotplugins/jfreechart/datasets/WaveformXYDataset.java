/*
 * Created on Dec 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.datasets;

import java.util.ArrayList;
import java.util.Vector;

import epics.archiveviewer.AVBaseFacade;
import epics.archiveviewer.AVEntry;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.ValuesContainer;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WaveformXYDataset extends AVAbstractDataset
{
	private final Vector wfValues;
	
	//needed for delayVC, periodVC, and tooltip
	private final int vcValueIndex;
	private final boolean isWaveformValid;
	
	//0, if no delayVC specified
	private final double delay;
	//1, if no periodVC specified
	private final double period;
	
	private double minY;
	private double maxY;
	private double minPosY;
	
	public WaveformXYDataset(
			ValuesContainer wf, double _delay, double _period,
			ArrayList ignoredItems, int vcItem, RangeAxisType yAxisType)
	throws Exception
	{
		super(wf, ignoredItems, yAxisType);
		
		if(wf.isWaveform() == false)
			throw new IllegalArgumentException();
		
		if(_delay < 0)
			throw new IllegalArgumentException("Delay must not be negative");
		this.delay = _delay;
		
		if(_period <= 0)
			throw new IllegalArgumentException("Period must not be negative, nor 0");
		
		period = _period;

		this.vcValueIndex = vcItem;
		this.wfValues = valuesContainer.getValue(vcItem);
		
		super.minX = 0;
		super.maxX = this.delay + this.period * (this.wfValues.size() - 1);
		
		this.isWaveformValid = valuesContainer.isValid(vcItem);
		if(this.isWaveformValid == false)
			return;
		
		//calculate minY, minPosY and maxY
		if (Number.class.isAssignableFrom(valuesContainer.getDataType())) {
			minY = Double.MAX_VALUE;
			minPosY = Double.MAX_VALUE;
			maxY = -1 * Double.MAX_VALUE;

			double currentValue = -1;

			for (int i = 0; i <this.wfValues.size(); i++) 
			{
					currentValue = ((Number) this.wfValues.get(i)).doubleValue();
					if (currentValue > maxY)
						maxY = currentValue;
					if (currentValue < minY)
						minY = currentValue;
					if (currentValue > 0 && currentValue < minPosY)
						minPosY = currentValue;
			}
		} 
		else 
		{
			//random values
			minY = MIN_IN_CASE_VALUE_DOES_NOT_EXIST.doubleValue();
			minPosY = MIN_POS_IN_CASE_VALUE_DOES_NOT_EXIST.doubleValue();
			maxY = MAX_IN_CASE_VALUE_DOES_NOT_EXIST.doubleValue();
		}
	}
	
	public void ignorePlotItem(int itemIndex)
	{
		AVAbstractDataset.ignoreValueCurrentlyPlottedAtNumber(super.sortedIgnoredDataIndices, itemIndex);
	}
	
	public int getItemCount(int series) {
		if (series == 0)
			return this.wfValues.size() - getIgnoredIndices().size();
		return 0;
	}
	
	public Number getMaximumRangeValue() {
		if (this.correspondingYAxisType == RangeAxisType.NORMAL)
			return new Double(maxY);
		if (this.correspondingYAxisType == RangeAxisType.LOG) {
			if (maxY <= 0)
				return new Double(0);
			return new Double(Math.log(maxY));
		}
		return null;
	}

	public Number getMinimumRangeValue() {
		if (this.correspondingYAxisType == RangeAxisType.NORMAL)
			return new Double(minY);
		if (this.correspondingYAxisType == RangeAxisType.LOG) {
			if (minY <= 0)
			{
				minPosY = getSmallestPositiveRangeValue();
				if(minPosY == Double.MAX_VALUE)
					return new Double(0);
				else 
					return new Double(Math.log(minPosY));
			}
			return new Double(Math.log(minY));
		}
		return null;
	}

	public double getSmallestPositiveRangeValue() {
		return this.minPosY;
	}
	
	public Number getX(int series, int item) {
		if (series == 0)
		{			
			int dataIndex = AVAbstractDataset.getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
			
			return new Double(this.delay + dataIndex * period);
		}
		return null;
	}
	
	public Number getY(int series, int item) {
		if (series == 0)
		{
			int wfItemIndex = AVAbstractDataset.getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
			if(	isWaveformValid)
			{		
				try
				{
					Number value = (Number) this.wfValues.get(wfItemIndex);
					if (correspondingYAxisType == RangeAxisType.NORMAL)
						//no waveforms
						return value;
					if (correspondingYAxisType == RangeAxisType.LOG &&
						value.doubleValue() > 0) 
							return new Double(LOG(value));
				}
				catch(Exception e)
				{
					//do nothing
				}
			}
		}
		return new Double(Double.NaN);
	}
	
	public String getToolTip(int series, int item) {
		if (series == 0) {
			try
			{
				AVEntry ave = super.valuesContainer.getAVEntry();
				StringBuffer sb = new StringBuffer(ave.getName());
				sb.append("\n");
				sb.append(ave.getArchiveDirectory().getName());
				sb.append("\n");
				int wfItemIndex = AVAbstractDataset.getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
				sb.append("[");
				sb.append(wfItemIndex);
				sb.append("] => ");
				sb.append(valuesContainer.valueToString(this.vcValueIndex, wfItemIndex));
				return sb.toString();
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		return null;
	}
}
