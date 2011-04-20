/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.fundamental.Range;
import epics.archiveviewer.base.model.PlotPluginsRepository;
import epics.archiveviewer.xal.view.plotplugins.PlotManipulationButtonsPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotManipulationButtonsController {
	
	private final static int DO_NOTHING = -1;
	private final static int DECREASE_RANGE = 0;
	private final static int INCREASE_RANGE = 1;
	private final static int DOUBLE_RANGE = 2;
	private final static int HALVE_RANGE = 3;
	
	private final AVController avController;
	private final AVBase avBase;
	private final PlotPluginsRepository ppsRepository;
	private final PlotPlugin plotPlugin;
	private final PlotManipulationButtonsPanel pmbPanel;
	private final boolean isDomainTime;
	
	private void decreaseRange(Range range)
	{
		//newMax := min
		//newMin := min - (max - min)
		Double newMax = range.min;
		double newMin = 2 * range.min.doubleValue() - range.max.doubleValue();
		range.max = newMax;
		range.min = new Double(newMin);
	}
	
	private void increaseRange(Range range)
	{
		//newMin := max
		//newMax := max + (max - min)
		Double newMin = range.max;
		double newMax = 2 * range.max.doubleValue() - range.min.doubleValue(); 
		range.min = newMin;
		range.max = new Double(newMax);
	}
	
	private void doubleRange(Range range)
	{
		//newMin := min - (max - min)/2
		//newMax := max + (max - min)/2
		double d = (range.max.doubleValue() - range.min.doubleValue())/2;
		double newMin = range.min.doubleValue() - d;
		double newMax = range.max.doubleValue() + d;
		range.min = new Double(newMin);
		range.max = new Double(newMax);
	}
	
	private void halveRange(Range range)
	{
		//newMin:= min + (max - min)/4
		//newMax:= max - (max - min)/4
		double d = (range.max.doubleValue() - range.min.doubleValue())/4;
		double newMin = range.min.doubleValue() + d;
		double newMax = range.max.doubleValue() - d;
		range.min = new Double(newMin);
		range.max = new Double(newMax);
	}
	
	
	private void manipulateAxes(int timeAxisMode, int rangeAxisMode) throws Exception
	{
		String[] domainAxisNames = this.plotPlugin.getDomainAxesLabels();
		HashMap domainAxisNamesAndRanges = new HashMap();
		
		Range range = null;
		
		for(int i=0; i<domainAxisNames.length; i++)
		{
			range = new Range(
					this.plotPlugin.getLowerBoundOfDomainAxis(domainAxisNames[i]),
					this.plotPlugin.getUpperBoundOfDomainAxis(domainAxisNames[i])
					);
			switch(timeAxisMode)
			{
				case DECREASE_RANGE:
					decreaseRange(range);
					break;
				case INCREASE_RANGE:
					increaseRange(range);
					break;
				case DOUBLE_RANGE:
					doubleRange(range);
					break;
				case HALVE_RANGE:
					halveRange(range);
					break;
			}
			
			if(this.isDomainTime)
			{
				//put into axes manager
				try
				{
					domainAxisNamesAndRanges.put(domainAxisNames[i], range);					
				}
				catch(Exception e)
				{
					avBase.displayError("Can't process the interval of the time axis " + domainAxisNames[i], e);
				}
			}
			else
			{
				//forget about history
				this.plotPlugin.setDomainAxisBounds(
						domainAxisNames[i], 
						range.min.doubleValue(),
						range.max.doubleValue());
			}
		}
		
		String[] rangeAxisNames = this.avBase.getPlotModel().getRangeAxesNames();
		HashMap rangeAxisNamesAndRanges = new HashMap();
		
		for(int i=0; i<rangeAxisNames.length; i++)
		{
			range = new Range(
					this.plotPlugin.getLowerBoundOfRangeAxis(rangeAxisNames[i]),
					this.plotPlugin.getUpperBoundOfRangeAxis(rangeAxisNames[i])
					);
			switch(rangeAxisMode)
			{
				case DECREASE_RANGE:
					decreaseRange(range);
					break;
				case INCREASE_RANGE:
					increaseRange(range);
					break;
				case DOUBLE_RANGE:
					doubleRange(range);
					break;
				case HALVE_RANGE:
					halveRange(range);
			}
			if(this.isDomainTime)
			{
				//put into axes manager
				try
				{
					rangeAxisNamesAndRanges.put(rangeAxisNames[i], range);					
				}
				catch(Exception e)
				{
					avBase.displayError("Can't process the interval of the range axis " + rangeAxisNames[i], e);
				}
			}
			else
				this.plotPlugin.setRangeAxisBounds(
					rangeAxisNames[i],
					range.min.doubleValue(),
					range.max.doubleValue());
		}
			
			
		if(this.isDomainTime)
		{
			this.avBase.getAxesIntervalsManager().addIntervals(
					this.plotPlugin, domainAxisNamesAndRanges, rangeAxisNamesAndRanges);
			this.avController.plot();
		}
	}
	
	private ActionListener createAxesManipulationListener(final int timeAxisMode, final int rangeAxisMode)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				try
				{
					manipulateAxes(timeAxisMode, rangeAxisMode);
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't manipulate axes ranges", ex);
				}
			}
		};
	}
	
	private ActionListener createAntiAliasListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				plotPlugin.setAntiAlias(pmbPanel.getAntiAliasItem().isSelected());
			}
			
		};
	}
	
	private ActionListener createLeaveIgnoredItemsListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				plotPlugin.setLeaveIgnoredItems(pmbPanel.getLeaveIgnoredItem().isSelected());
			}
			
		};
	}
	
	private ActionListener createMoreButtonListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				pmbPanel.getAdditionalParametersMenu().show(
						pmbPanel.getMoreButton(), 
						0, 0);
			}
			
		};
	}
	
	public PlotManipulationButtonsController(
			AVController avc, PlotManipulationButtonsPanel pmbp, PlotPlugin pp)
	{
		this.avController = avc;
		this.avBase = this.avController.getAVBase();
		this.pmbPanel = pmbp;
		this.ppsRepository = this.avBase.getPlotPluginsRepository();
		this.plotPlugin = pp;
		this.isDomainTime = this.plotPlugin.isDomainTime();
		
		this.pmbPanel.getGoLeftButton().addActionListener(
				createAxesManipulationListener(DECREASE_RANGE, DO_NOTHING));
		this.pmbPanel.getGoRightButton().addActionListener(
				createAxesManipulationListener(INCREASE_RANGE, DO_NOTHING));
		this.pmbPanel.getGoUpButton().addActionListener(
				createAxesManipulationListener(DO_NOTHING, INCREASE_RANGE));
		this.pmbPanel.getGoDownButton().addActionListener(
				createAxesManipulationListener(DO_NOTHING, DECREASE_RANGE));
		this.pmbPanel.getZoomInHorizontallyButton().addActionListener(
				createAxesManipulationListener(HALVE_RANGE, DO_NOTHING));
		this.pmbPanel.getZoomOutHorizontallyButton().addActionListener(
				createAxesManipulationListener(DOUBLE_RANGE, DO_NOTHING));
		this.pmbPanel.getZoomInVerticallyButton().addActionListener(
				createAxesManipulationListener(DO_NOTHING, HALVE_RANGE));
		this.pmbPanel.getZoomOutVerticallyButton().addActionListener(
				createAxesManipulationListener(DO_NOTHING, DOUBLE_RANGE));
		this.pmbPanel.getAntiAliasItem().addActionListener(
				createAntiAliasListener());
		this.pmbPanel.getLeaveIgnoredItem().addActionListener(
				createLeaveIgnoredItemsListener());
		this.pmbPanel.getMoreButton().addActionListener(
				createMoreButtonListener());
	}
}
