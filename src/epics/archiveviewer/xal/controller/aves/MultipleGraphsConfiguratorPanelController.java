/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import epics.archiveviewer.DrawType;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.listeners.WidthSliderListener;
import epics.archiveviewer.xal.controller.util.AVXALUtilities;
import epics.archiveviewer.xal.view.aveconfigurators.CommonGraphConfiguratorPanel;
import epics.archiveviewer.xal.view.components.AVDialog;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultipleGraphsConfiguratorPanelController {
	
	private final AVController avController;
	private final CommonGraphConfiguratorPanel cgcPanel;
	
	private MouseListener createVisibilityEnabler()
	{
	    return new MouseAdapter()
	    {
            public void mouseReleased(MouseEvent arg0)
            {
                cgcPanel.getVisibilityCheckBox().setEnabled(true);
                cgcPanel.getVisibilityCheckBox().setSelected(true);
                cgcPanel.getVisibilityCheckBox().removeMouseListener(this);
            }
	    };
	}
	
	private MouseListener createWidthEnabler()
	{
	    return new MouseAdapter()
	    {
            public void mouseReleased(MouseEvent arg0)
            {
                cgcPanel.getWidthSlider().setEnabled(true);
                cgcPanel.getWidthSlider().addChangeListener(new WidthSliderListener(cgcPanel));
                cgcPanel.getWidthLabel().setEnabled(true);
                cgcPanel.getWidthLabel().removeMouseListener(this);
                cgcPanel.getWidthSlider().removeMouseListener(this);
            }
	    };
	}
	
	private ActionListener createCommitListener(final Graph[] selectedGraphs)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				String timeAxisName = (String) cgcPanel.getTimeAxisBox().getSelectedItem();
				String rangeAxisName = (String) cgcPanel.getRangeAxisBox().getSelectedItem();
				String drawType = (String) cgcPanel.getDrawTypeBox().getSelectedItem();
				Boolean visibility = null;
				if(cgcPanel.getVisibilityCheckBox().isEnabled())
					visibility = new Boolean(cgcPanel.getVisibilityCheckBox().isSelected());
				
				Float width = null;
				if(cgcPanel.getWidthSlider().isEnabled())
				{
					width = new Float(
							AVXALUtilities.percentToDrawWidth(
									cgcPanel.getWidthSlider().getValue()
									)
								);
				}
				
				PlotModel plotModel = avController.getAVBase().getPlotModel();
				for(int i=0; i<selectedGraphs.length; i++)
				{
					try
					{
						if(timeAxisName.equals(AVXALConstants.NO_CHANGE_LABEL) == false)
							selectedGraphs[i].setTimeAxisLabel(timeAxisName);
						if(rangeAxisName.equals(AVXALConstants.NO_CHANGE_LABEL) == false)
							selectedGraphs[i].setRangeAxisLabel(rangeAxisName);
						if(drawType.equals(AVXALConstants.NO_CHANGE_LABEL) == false)
							selectedGraphs[i].setDrawType(DrawType.getDrawType(drawType));
						if(visibility != null)
							selectedGraphs[i].setVisible(visibility.booleanValue());
						if(width != null)
							selectedGraphs[i].setDrawWidth(width.floatValue());
						
						plotModel.addGraph(selectedGraphs[i]);
					}
					catch(Exception ex)
					{
						avController.getAVBase().displayError("Can't save the graph configuration for AV entry " +
								selectedGraphs[i].getAVEntry().getName(), ex);
					}
				}
				plotModel.fireAVEsUpdated();
			}
		};
	}
	
	public MultipleGraphsConfiguratorPanelController(AVController avc, Graph[] selectedGraphs)
	{
		this.avController = avc;
		this.cgcPanel = new CommonGraphConfiguratorPanel();
		
		try
		{
			new CommonGraphConfiguratorPanelController(this.avController, this.cgcPanel, null);
			this.cgcPanel.getTimeAxisBox().insertItemAt(AVXALConstants.NO_CHANGE_LABEL, 0);
			this.cgcPanel.getTimeAxisBox().setSelectedIndex(0);
			this.cgcPanel.getRangeAxisBox().insertItemAt(AVXALConstants.NO_CHANGE_LABEL, 0);
			this.cgcPanel.getRangeAxisBox().setSelectedIndex(0);
			this.cgcPanel.getDrawTypeBox().insertItemAt(AVXALConstants.NO_CHANGE_LABEL, 0);
			this.cgcPanel.getDrawTypeBox().setSelectedIndex(0);
			this.cgcPanel.getAVEColorButton().setEnabled(false);
			this.cgcPanel.getVisibilityCheckBox().setEnabled(false);
			this.cgcPanel.getWidthSlider().setEnabled(false);
			this.cgcPanel.getWidthLabel().setEnabled(false);
			
			this.cgcPanel.getVisibilityCheckBox().addMouseListener(
			        createVisibilityEnabler());
			
			MouseListener widthEnabler = createWidthEnabler();
			this.cgcPanel.getWidthLabel().addMouseListener(widthEnabler);
			this.cgcPanel.getWidthSlider().addMouseListener(widthEnabler);
		}
		catch(Exception e)
		{
			this.avController.getAVBase().displayError("Can't display the multiple graphs configurator", e);
			return;
		}

		//listeners
		
		AVDialog d = new AVDialog(
				this.cgcPanel,
				this.avController.getMainWindow(),
				"Multiple Graphs Configurator",
				false,
				true,
				this.avController.getMainAVPanel().getPlotPluginsWrapperPane(),
				createCommitListener(selectedGraphs), FlowLayout.CENTER
				);
		//d.setResizable(false);
	}
}
