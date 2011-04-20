package epics.archiveviewer.xal.view.timeinput;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JTabbedPane;

import epics.archiveviewer.base.util.TimeParser;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

public class TimeInputPanel extends AVAbstractPanel {
	
	private final String startTime;
	private final String endTime;
	private final boolean isForStartTime;

	private RelativeTimePanel relativeTimePanel;

	private AbsoluteTimePanel absoluteTimePanel;
	
	/*
	 * Added sliderTimePanel
	 * modified on: 8/1/06
	 * John Lee
	 */
	private SliderTimePanel sliderTimePanel;
	public TimeInputPanel(String startTimeString, String endTimeString, boolean forStart)
	{		
		this.startTime = startTimeString;
		this.endTime = endTimeString;
		this.isForStartTime = forStart;
		init();
	}
	

	
	/*
	 * Added sliderTimePanel
	 * modified on: 8/1/06
	 * John Lee
	 */
	protected void addComponents() {

		JTabbedPane mainPanel = new JTabbedPane(JTabbedPane.TOP);
		mainPanel.add("Absolute", absoluteTimePanel);
		mainPanel.add("Relative", relativeTimePanel);
		mainPanel.add("Slider", sliderTimePanel);
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.NORTH);
	}
	/*
	 * Added sliderTimePanel
	 * modified on: 8/1/06
	 * John Lee
	 */
	protected void createComponents() {
		Date[] dates = null;
		try 
		{
			dates = TimeParser.parse(this.startTime, this.endTime);
		} 
		catch (Exception e) 
		{
			return;
		}

		//all OK
		if (isForStartTime) {
			//look first at start field, then at end field
			if (this.startTime.equals("") == false) {
				absoluteTimePanel = new AbsoluteTimePanel(dates[0]);
				relativeTimePanel = new RelativeTimePanel(TimeParser
						.getRelativeFields(this.startTime), isForStartTime);
				sliderTimePanel = new SliderTimePanel (dates[0]);
				return;
			} else if (this.endTime.equals("") == false) {
				absoluteTimePanel = new AbsoluteTimePanel(dates[1]);
				relativeTimePanel = new RelativeTimePanel(TimeParser
						.getRelativeFields(this.endTime), isForStartTime);
				sliderTimePanel = new SliderTimePanel (dates[1]);
				return;
			} else {
				absoluteTimePanel = new AbsoluteTimePanel(null);
				relativeTimePanel = new RelativeTimePanel(null, true);
				sliderTimePanel = new SliderTimePanel (null);
				return;
			}
		} else {
			//look first at end field, then at start field
			if (this.endTime.equals("") == false) {
				absoluteTimePanel = new AbsoluteTimePanel(dates[1]);
				relativeTimePanel = new RelativeTimePanel(TimeParser
						.getRelativeFields(this.endTime), isForStartTime);
				sliderTimePanel = new SliderTimePanel (dates[1]);
				return;
			} else if (this.startTime.equals("") == false) {
				absoluteTimePanel = new AbsoluteTimePanel(dates[0]);
				relativeTimePanel = new RelativeTimePanel(TimeParser
						.getRelativeFields(this.startTime), isForStartTime);
				sliderTimePanel = new SliderTimePanel (dates[0]);
				return;
			} else {
				absoluteTimePanel = new AbsoluteTimePanel(null);
				relativeTimePanel = new RelativeTimePanel(null, false);
				sliderTimePanel = new SliderTimePanel (null);
				return;
			}
		}
	}

	public AbsoluteTimePanel getAbsoluteTP()
	{
		return this.absoluteTimePanel;
	}
	
	public RelativeTimePanel getRelativeTP()
	{
		return this.relativeTimePanel;
	}
	/*
	 * Added sliderTimePanel
	 * modified on: 8/1/06
	 * John Lee
	 */
	public SliderTimePanel getSliderTP (){
		return this.sliderTimePanel;
	}
}