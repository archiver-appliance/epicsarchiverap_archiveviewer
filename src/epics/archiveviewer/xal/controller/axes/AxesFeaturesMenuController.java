/*
 * Created on Feb 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.axes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JOptionPane;

import epics.archiveviewer.PlotPlugin;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.fundamental.RangeAxis;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.view.axesconfigurators.AxesFeaturesMenu;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AxesFeaturesMenuController {
	//XO
	private final AxesConfiguratorController axesConfiguratorController;
	private final boolean isForTimeAxes;
	
	private final AVController avController;
	private final PlotModel plotModel;
	
	private void updateAxisRangeFromPlotPlugin(String axisName) throws Exception
	{
		PlotPlugin selectedPlotPlugin = avController.getAVBase().getPlotPluginsRepository().getSelectedPlotPlugin();
		if(this.isForTimeAxes)
		{
			TimeAxis tA = 
				this.plotModel.getTimeAxis(axisName);
			this.plotModel.addTimeAxis(
					new TimeAxis(
						tA.getName(),
						AVBaseConstants.MAIN_DATE_FORMAT.format(
								new Date(
									(long)selectedPlotPlugin.getLowerBoundOfDomainAxis(tA.getName())
								)
							),
						AVBaseConstants.MAIN_DATE_FORMAT.format(
								new Date(
									(long)selectedPlotPlugin.getUpperBoundOfDomainAxis(tA.getName())
								)
							),
						tA.getLocation()
						)
				);
		}
		else
		{
			RangeAxis rA = 
				this.plotModel.getRangeAxis(axisName);
			this.plotModel.addRangeAxis(
					new RangeAxis(
						rA.getName(),
						new Double(selectedPlotPlugin.getLowerBoundOfRangeAxis(rA.getName())),
						new Double(selectedPlotPlugin.getUpperBoundOfRangeAxis(rA.getName())),
						rA.getType(),
						rA.getLocation()
						)
				);
		}
	}
	
	private void resetAxis(String axisName) throws Exception
	{
		if(this.isForTimeAxes)
		{
			TimeAxis tA = 
				this.plotModel.getTimeAxis(axisName);
			this.plotModel.addTimeAxis(
					new TimeAxis(
						tA.getName(),
						AVBaseConstants.DEFAULT_START_TIME,
						AVBaseConstants.DEFAULT_END_TIME,
						tA.getLocation()
						)
				);
		}
		else
		{
			RangeAxis rA = 
				this.plotModel.getRangeAxis(axisName);
			this.plotModel.addRangeAxis(
				new RangeAxis(
						rA.getName(),
						AVBaseConstants.DEFAULT_RANGE_MIN,	
						AVBaseConstants.DEFAULT_RANGE_MAX,
						rA.getType(),
						rA.getLocation()
					)
				);
		}
	}
	
	private ActionListener createAddNewAxisListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String newAxisLabel = null;
				boolean result = false;
				int i=0;
				do
				{
					newAxisLabel = JOptionPane.showInputDialog(
							avController.getMainWindow(),
							"Please, enter a new axis name", 
							"Enter axis name...", 
							JOptionPane.PLAIN_MESSAGE);
					if(newAxisLabel == null)
						return;
					
					Object o = null;
					if(isForTimeAxes)
					{
						o = plotModel.getTimeAxis(newAxisLabel);
					}
					else
					{
						o = plotModel.getRangeAxis(newAxisLabel);
					}
					if(o == null)
						break;
					i++;
				}
				while(i<1000);
				
				try
				{
					if(isForTimeAxes)
					{
						plotModel.addTimeAxis(
								new TimeAxis(
									newAxisLabel,
									AVBaseConstants.DEFAULT_START_TIME,
									AVBaseConstants.DEFAULT_END_TIME,
									AVBaseConstants.DEFAULT_TIME_AXIS_LOCATION
									)
								);
						plotModel.fireTimeAxesUpdated();
					}
					else
					{
						plotModel.addRangeAxis(
								new RangeAxis(
									newAxisLabel,
									AVBaseConstants.DEFAULT_RANGE_MIN,
									AVBaseConstants.DEFAULT_RANGE_MAX,
									AVBaseConstants.DEFAULT_RANGE_AXIS_TYPE,
									AVBaseConstants.DEFAULT_RANGE_AXIS_LOCATION
									)
								);
						plotModel.fireRangeAxesUpdated();
					}
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't add the axis", ex);
				}
			}
		};
	}
	
	private ActionListener createRemoveSelectedAxisListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String selectedAxisLabel = axesConfiguratorController.getSelectedAxisName();
					if(isForTimeAxes)
					{
						plotModel.removeTimeAxis(selectedAxisLabel);
						plotModel.fireTimeAxesUpdated();
					}
					else
					{	
						plotModel.removeRangeAxis(selectedAxisLabel);
						plotModel.fireRangeAxesUpdated();
					}
					
					JOptionPane.showMessageDialog(
							avController.getMainWindow(), 
							"Please, update your graph axes settings!",
							"Axis was removed",
							JOptionPane.PLAIN_MESSAGE);
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't remove selected axis", ex);
				}
				
			}
		};
	}
	
	private ActionListener createRenameSelectedAxisListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String newAxisLabel = null;
				boolean result = false;
				int i=0;
				do
				{
					newAxisLabel = JOptionPane.showInputDialog(
							avController.getMainWindow(),
							"Please, enter a new axis name", 
							"Enter axis name...", 
							JOptionPane.PLAIN_MESSAGE);
					if(newAxisLabel == null)
						return;
					Object o = null;
					if(isForTimeAxes)
						o = plotModel.getTimeAxis(newAxisLabel);
					else
						o = plotModel.getRangeAxis(newAxisLabel);
					
					if(o == null)
						break;
					
					i++;
				}
				while(i<1000);
				
				try
				{
					String selectedAxisLabel = axesConfiguratorController.getSelectedAxisName();
					if(isForTimeAxes)
					{
						plotModel.changeTimeAxisName(selectedAxisLabel, newAxisLabel);
						plotModel.fireTimeAxesUpdated();
					}
					else
					{
						plotModel.changeRangeAxisName(selectedAxisLabel, newAxisLabel);
						plotModel.fireRangeAxesUpdated();
					}
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't rename selected axis", ex);
				}
			}
		};
	}
	
	private ActionListener createUpdateSelectedAxisListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String selectedAxisName = axesConfiguratorController.getSelectedAxisName();
				try
				{
					updateAxisRangeFromPlotPlugin(selectedAxisName);
					if(isForTimeAxes)
					{
						plotModel.fireTimeAxesUpdated();
					}
					else
						plotModel.fireRangeAxesUpdated();
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't update selected axis", ex);
				}
				
				
			}
		};
	}
	
	private ActionListener createUpdateAllAxesListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String[] axisNames = null;
				if(isForTimeAxes)
				{
					axisNames = plotModel.getTimeAxesNames();
				}
				else
				{
					axisNames = plotModel.getRangeAxesNames();
				}
				
				for(int i=0; i<axisNames.length; i++)
				{
					try
					{
						updateAxisRangeFromPlotPlugin(axisNames[i]);
					}
					catch(Exception ex)
					{
						avController.getAVBase().displayError("Can't update axis " + axisNames[i], ex);
					}
				}
				
				if(isForTimeAxes)
				{
					plotModel.fireTimeAxesUpdated();
				}
				else
					plotModel.fireRangeAxesUpdated();
			}
		};
	}
	
	private ActionListener createResetSelectedAxisListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String selectedAxisName = axesConfiguratorController.getSelectedAxisName();
				try
				{
					resetAxis(selectedAxisName);
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't reset selected axis", ex);
				}
				
				if(isForTimeAxes)
				{
					plotModel.fireTimeAxesUpdated();
				}
				else
					plotModel.fireRangeAxesUpdated();
				
			}
		};
	}
	
	private ActionListener createResetAllAxesListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String[] axisNames = null;
				if(isForTimeAxes)
				{
					axisNames = plotModel.getTimeAxesNames();
				}
				else
				{
					axisNames = plotModel.getRangeAxesNames();
				}
				
				for(int i=0; i<axisNames.length; i++)
				{
					try
					{
						resetAxis(axisNames[i]);
					}
					catch(Exception ex)
					{
						avController.getAVBase().displayError("Can't reset axis " + axisNames[i], ex);
					}
				}		
				if(isForTimeAxes)
				{
					plotModel.fireTimeAxesUpdated();
				}
				else
					plotModel.fireRangeAxesUpdated();
			}
		};
	}
	//pass ether a TimeAxesConfigurator or a RangeAxesConfigurator
	public AxesFeaturesMenuController(AVController avc, AxesConfiguratorController acc)
	{
		this.avController = avc;
		this.plotModel = this.avController.getAVBase().getPlotModel();
		this.axesConfiguratorController = acc;
		this.isForTimeAxes = (this.axesConfiguratorController instanceof TimeAxesConfiguratorController);
		
		AxesFeaturesMenu afMenu = new AxesFeaturesMenu();
	
		
		afMenu.getAddNewAxisItem().addActionListener(
				createAddNewAxisListener());
		
		afMenu.getRemoveSelectedAxisItem().addActionListener(
				createRemoveSelectedAxisListener());
		
		afMenu.getRenameSelectedAxisItem().addActionListener(
				createRenameSelectedAxisListener());
		
		afMenu.getUpdateSelectedAxisItem().addActionListener(
				createUpdateSelectedAxisListener());
		
		afMenu.getUpdateAllAxesItem().addActionListener(
				createUpdateAllAxesListener());
		
		afMenu.getResetSelectedAxisItem().addActionListener(
				createResetSelectedAxisListener());
		
		afMenu.getResetAllAxesItem().addActionListener(
				createResetAllAxesListener());
		
		afMenu.getPopupMenu().show(this.axesConfiguratorController.getMoreButton(), 0, 0);
	}
}
