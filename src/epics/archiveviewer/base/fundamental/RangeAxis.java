package epics.archiveviewer.base.fundamental;

import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.RangeAxisType;

/**
 * This class encapsulates the parameters of a range axis
 * 
 * @author Sergei Chevtsov
 */
public class RangeAxis extends Axis
{
	//may be NULL
	private final Double min;
	private final Double max;
	private final RangeAxisType type;
	private final RangeAxisLocation location;

	public RangeAxis(
			String name,
			Double _min, 
			Double _max,
			RangeAxisType _type,
			RangeAxisLocation _location) throws Exception
	{
		super(name);
		this.min = _min;
		this.max = _max;
		this.type = _type;
		this.location = _location;
	}
	
	public Double getMax() {
		return this.max;
	}
	
	public Double getMin() {
		return this.min;
	}
	
	public RangeAxisType getType() {
		return this.type;
	}
	
	public RangeAxisLocation getLocation() {
		return this.location;
	}
}