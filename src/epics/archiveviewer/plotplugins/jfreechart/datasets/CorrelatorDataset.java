/*
 * Created on Dec 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.datasets;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.RangeAxisType;
import epics.archiveviewer.ValuesContainer;
import epics.archiveviewer.base.AVBaseConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CorrelatorDataset extends AVAbstractDataset
{
	private class IndexedValue implements Comparable
	{
		private final int index;
		private final Object value;
		public IndexedValue(int i, Object v)
		{
			this.index = i;
			this.value = v;
		}
		
		public int getIndex()
		{
			return this.index;
		}
		
		public Object getValue()
		{
			return this.value;
		}
		
		public int compareTo(Object o) {
			double otherValue = ((Number) ((IndexedValue) o).getValue()).doubleValue();
			double thisValue = ((Number)this.value).doubleValue();
			
			if(thisValue > otherValue)
				return 1;
			return -1;
		}
	}
	//sorted asc by domain values
	private final ValuesContainer domainValuesContainer;
	private final int[] vcIndices;
	
	public CorrelatorDataset(
			ValuesContainer domainVC, 
			ValuesContainer rangeVC,
			ArrayList ignoredItems, 
			RangeAxisType yAxisType) 
	throws Exception
	{
		super(rangeVC, ignoredItems, yAxisType);
		
		if(domainVC.isWaveform() || rangeVC.isWaveform())
			throw new IllegalArgumentException();
		
		/*
		if(domainVC.isDiscrete())
			throw new IllegalArgumentException("A discrete graph can not be the domain");
		*/
		if(domainVC.getNumberOfValues() != rangeVC.getNumberOfValues())
		{
			try
			{
				FileWriter fw = new FileWriter("/home/serge/stuff1");
				for(int i=0; i<domainVC.getNumberOfValues(); i++)
				{
					fw.write(AVBaseConstants.MAIN_DATE_FORMAT.format(new Date((long)domainVC.getTimestampInMsec(i))) + "\n");
				}
				fw.close();
				
				fw = new FileWriter("/home/serge/stuff2");
				for(int i=0; i<rangeVC.getNumberOfValues(); i++)
				{
					fw.write(AVBaseConstants.MAIN_DATE_FORMAT.format(new Date((long)rangeVC.getTimestampInMsec(i))) + "\n");
				}
				fw.close();
			}
			catch(Exception e)
			{
				//do nothing
			}
			throw new IllegalArgumentException("Number of values from domain and range do not match. Do graphs come from different archives?");
		}
		this.domainValuesContainer = domainVC;
		
		ArrayList validIndexedDomainValues = new ArrayList();
		
		double currentRangeValue = -1;
		
		int i = 0;
		
		for(i=0; i<this.domainValuesContainer.getNumberOfValues(); i++)
		{
			if(	this.domainValuesContainer.isValid(i) &&
				super.valuesContainer.isValid(i))
			{
				validIndexedDomainValues.add(new IndexedValue(i, this.domainValuesContainer.getValue(i).get(0)));
			}
		}
		
		this.vcIndices = new int[validIndexedDomainValues.size()];
		
		IndexedValue[] validIndexedDomainValuesAsArray = 
			(IndexedValue[]) validIndexedDomainValues.toArray(new IndexedValue[validIndexedDomainValues.size()]);
		
		Arrays.sort(validIndexedDomainValuesAsArray);
		
		for(i=0; i<validIndexedDomainValuesAsArray.length; i++)
		{
			this.vcIndices[i] = validIndexedDomainValuesAsArray[i].getIndex();
		}
		
		i=0;
		Number domainValue = null;
		
		while(i<this.vcIndices.length)
		{
			domainValue = (Number) this.domainValuesContainer.getValue(this.vcIndices[i]).get(0);
			if(domainValue != null)
			{
				//no matter if it's valid or not
				super.minX = domainValue.doubleValue();
				break;
			}
			i++;
		}
		
		i= this.vcIndices.length - 1;
		while(i > -1)
		{
			domainValue = (Number) this.domainValuesContainer.getValue(this.vcIndices[i]).get(0);
			if(domainValue != null)
			{
				//no matter if it's valid or not
				super.maxX = domainValue.doubleValue();
				break;
			}
			i--;
		}
		
		//leave minX, maxX if all values are NULL		
	}
	
	public void ignorePlotItem(int itemIndex)
	{
		AVAbstractDataset.ignoreValueCurrentlyPlottedAtNumber(super.sortedIgnoredDataIndices, itemIndex);
	}
	
	public int getItemCount(int series) {
		return this.vcIndices.length - super.getIgnoredIndices().size();
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
		//we don't care if it's invalid
		try
		{
			if (series == 0)
			{			
				int vcIndicesIndex = AVAbstractDataset.getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
				int vcValueIndex = this.vcIndices[vcIndicesIndex];
				return (Number) this.domainValuesContainer.getValue(vcValueIndex).get(0);
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
				int vcIndicesIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
				int vcValueIndex = this.vcIndices[vcIndicesIndex];
				if(	this.domainValuesContainer.isValid(vcValueIndex) &&
					super.valuesContainer.isValid(vcValueIndex))
				{		
					try
					{
						Number value = (Number) super.valuesContainer.getValue(vcValueIndex).get(0);
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
				int vcIndicesIndex = getActualDataIndexAfterConsideringIgnoredItems(super.sortedIgnoredDataIndices, item);
				int vcValueIndex = this.vcIndices[vcIndicesIndex];
				
				//doesn't matter which VC
				sb.append(FULL_DATE_FORMAT.format(new Date((long) this.domainValuesContainer
										.getTimestampInMsec(vcValueIndex))));
				sb.append("\n");
				
				sb.append("(");
				sb.append(this.domainValuesContainer.getDisplayLabel(vcValueIndex));
				sb.append(":");
				sb.append(super.valuesContainer.getDisplayLabel(vcValueIndex));
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
