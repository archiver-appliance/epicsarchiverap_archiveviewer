package epics.archiveviewer.base.fundamental;

import java.util.Date;

import epics.archiveviewer.RangeAxisLocation;
import epics.archiveviewer.TimeAxisLocation;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.util.TimeParser;

/**
 * This class encapsulates the parameters of a range axis
 * 
 * @author Sergei Chevtsov
 */
public class TimeAxis extends Axis
{
	private String startTime;
	private String endTime;
	private final TimeAxisLocation location;
	
	public TimeAxis(
			String name,
			String start,
			String end,
			TimeAxisLocation _location)
	throws Exception
	{
		super(name);
		this.startTime = start;
		this.endTime = end;
		this.location = _location;
	}
	
	public void resolveRelativeTimes() throws Exception
	{
		Date[] dates = TimeParser.parse(this.startTime, this.endTime);
		this.startTime = AVBaseConstants.MAIN_DATE_FORMAT.format(dates[0]);
		this.endTime = AVBaseConstants.MAIN_DATE_FORMAT.format(dates[1]);
	}
	
	public String getEndTime() {
		return this.endTime;
	}
	
	public String getStartTime() {
		return this.startTime;
	}
	
	public TimeAxisLocation getLocation() {
		return this.location;
	}
}