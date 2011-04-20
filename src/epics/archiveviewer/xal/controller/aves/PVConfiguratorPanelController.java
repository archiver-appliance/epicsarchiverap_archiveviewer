/*
 * Created on Feb 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import epics.archiveviewer.base.fundamental.PVGraph;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.view.aveconfigurators.PVConfiguratorPanel;
import epics.archiveviewer.xal.view.components.AVDialog;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PVConfiguratorPanelController {
	
	private ActionListener createCommitListener(
			final AVController avController, 
			final CommonGraphConfiguratorPanelController cgcpController,
			final PVGraph pvg)
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				cgcpController.storeConfiguratorParameters(pvg);
		
				try
				{
					avController.getAVBase().getPlotModel().addGraph(pvg);
					avController.getAVBase().getPlotModel().fireAVEsUpdated();
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Can't save the graph configuration", ex);
				}
			}
			
		};
	}
	
	public PVConfiguratorPanelController(AVController avController, PVGraph pvGraph)
	throws Exception
	{
    	PVConfiguratorPanel pcp = new PVConfiguratorPanel();
    	//set up
    	String adName = pvGraph.getAVEntry().getArchiveDirectory().getName();
    	
    	pcp.getHeaderPanel().getAVENameLabel().setText(pvGraph.getAVEntry().getName());
    	pcp.getHeaderPanel().getDirectoryLabel().setText(adName);
    
    	CommonGraphConfiguratorPanelController cgcpController =
    		new CommonGraphConfiguratorPanelController(avController, pcp.getCommonConfiguratorPanel(), pvGraph);
    	
    	
        new AVDialog(
                pcp,
                avController.getMainWindow(),
                "PV Configurator",
                true,
                true,
                avController.getMainAVPanel().getPlotPluginsWrapperPane(),
                createCommitListener(avController,cgcpController, pvGraph), FlowLayout.CENTER
                );
	}

}
