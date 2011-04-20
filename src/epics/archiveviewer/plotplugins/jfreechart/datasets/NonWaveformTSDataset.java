/*
 * Created on Dec 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.datasets;

import java.util.ArrayList;
import java.util.Date;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.ValuesContainer;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NonWaveformTSDataset extends AVAbstractDataset{
	
	public NonWaveformTSDataset(ValuesContainer vc, ArrayList ignoredItems, RangeAxisType yAxisType)
	throws Exception
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
	}
	
	public void ignorePlotItem(int itemIndex)
	{
		AVAbstractDataset.ignoreValueCurrentlyPlottedAtNumber(super.sortedIgnoredDataIndices, itemIndex);
	}
	
	public int getItemCount(int series) {
		try
		{
			if (series == 0)
				return valuesContainer.getNumberOfValues() - getIgnoredIndices().size();
		}
		catch(Exception e)
		{
			//do nothing
		}
		return 0;
	}
	
	public Number getX(int series, int item) {
		try
		{
			if (series == 0)
			{			
				int vcValueIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
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
				int vcValueIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
				if(	valuesContainer.isValid(vcValueIndex))
				{		
					try
					{
						Number value = (Number) valuesContainer.getValue(vcValueIndex).get(0);
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
	
	public String getToolTip(int series, int item) {
		if (series == 0) {
			try
			{
				AVEntry ave = super.valuesContainer.getAVEntry();
				StringBuffer sb = new StringBuffer(ave.getName());
				sb.append("\n");
				sb.append(ave.getArchiveDirectory().getName());
				sb.append("\n");
				int vcValueIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
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
