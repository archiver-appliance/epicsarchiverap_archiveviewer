/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.base.fundamental;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Range {

	public Double min;
	public Double max;
	
	public Range(Double _min, Double _max)
	{
		this.min = _min;
		this.max = _max;
	}
	
	public Range(double _min, double _max)
	{
		this.min = new Double(_min);
		this.max = new Double(_max);
	}
}
