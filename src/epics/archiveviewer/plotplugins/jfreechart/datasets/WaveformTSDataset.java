/*
 * Created on Dec 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.datasets;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.ValuesContainer;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WaveformTSDataset extends AVAbstractDataset
{
	//min [even 0, 2, 4...], max [odd 1,3,5...] for the same time stamp => size = 2 * vc.getNumberOfValues()
	private final double[] valuesToBeActuallyPlotted;
	
	public WaveformTSDataset(ValuesContainer vc, ArrayList ignoredItems, RangeAxisType yAxisType) throws Exception
	{
		super(vc, ignoredItems, yAxisType);
		
		minX = valuesContainer.getTimestampInMsec(0);
		maxX = valuesContainer.getTimestampInMsec(valuesContainer.getNumberOfValues() - 1);

		//if only one timestamp is present
		if (minX == maxX) {
			//expand by a second
			minX -= 1000;
			maxX += 1000;
		}
		
		valuesToBeActuallyPlotted = new double[valuesContainer.getNumberOfValues() * 2];
		
		//set valuesToBePlotted
		if (Number.class.isAssignableFrom(valuesContainer.getDataType())) 
		{
			for (int i = 0; i <valuesContainer.getNumberOfValues(); i++) 
			{
				if (this.valuesContainer.isValid(i) == true) {
					try
					{
						Vector waveform = valuesContainer.getValue(i);
						double temp = -1;
						double currentMinValue = Double.MAX_VALUE;
						double currentMaxValue = -1 * Double.MAX_VALUE;
						for(int j=0; j<waveform.size(); j++)
						{
							temp =  ((Number)waveform.get(j)).doubleValue();
							if(currentMinValue > temp)
								currentMinValue = temp;
							if(currentMaxValue < temp)
								currentMaxValue = temp;
						}
						this.valuesToBeActuallyPlotted[2*i] = currentMinValue;
						this.valuesToBeActuallyPlotted[2*i+1] = currentMaxValue;
					}
					catch(Exception e)
					{
						//do nothing
					}
				}
			}
		} 
	}
	
	public void ignorePlotItem(int itemIndex)
	{
		AVAbstractDataset.ignoreValueCurrentlyPlottedAtNumber(super.sortedIgnoredDataIndices, itemIndex/2);
	}
	
	public int getItemCount(int series) {
		try
		{
			if (series == 0)
				return 2 * (valuesContainer.getNumberOfValues() - getIgnoredIndices().size());
		}
		catch(Exception e)
		{
			//do nothing
		}
		return 0;
	}
	
	public Number getMaximumRangeValue() {
		double maxY = this.valuesContainer.getMaxValidValue();
		if(Double.isNaN(maxY))
			return MAX_IN_CASE_VALUE_DOES_NOT_EXIST;
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
		double minY = this.valuesContainer.getMinValidValue();
		if(Double.isNaN(minY))
			return MIN_IN_CASE_VALUE_DOES_NOT_EXIST;
		if (this.correspondingYAxisType == RangeAxisType.NORMAL)
			return new Double(minY);
		if (this.correspondingYAxisType == RangeAxisType.LOG) {
			if (minY <= 0)
			{
				double minPosY = getSmallestPositiveRangeValue();
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
		double minPosY = this.valuesContainer.getMinPosValidValue();
		if(Double.isNaN(minPosY))
			return MIN_POS_IN_CASE_VALUE_DOES_NOT_EXIST.doubleValue();
		return minPosY;
	}
	
	public Number getX(int series, int item) {
		try
		{
			if (series == 0)
			{		
				int vcValueIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item/2);
				return new Double(valuesContainer.getTimestampInMsec(vcValueIndex));
			}
		}
		catch(Exception e)
		{
			//do nothing
		}
		return null;
	}
	
	public Number getY(int series, int item) {
		try
		{
			if (series == 0)
			{
				int vcValueIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item/2);
				if(	valuesContainer.isValid(vcValueIndex))
				{		
					try
					{
						Number value = new Double(this.valuesToBeActuallyPlotted[vcValueIndex * 2 + item % 2]);
						if (correspondingYAxisType == RangeAxisType.NORMAL)
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
		}
		catch(Exception e)
		{
			//do nothing
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
				int vcValueIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item/2);
				sb.append(valuesContainer.getDisplayLabel(vcValueIndex));
				sb.append("\n(");
				sb.append(FULL_DATE_FORMAT.format(new Date((long) valuesContainer
										.getTimestampInMsec(vcValueIndex))));
				sb.append(")");
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
