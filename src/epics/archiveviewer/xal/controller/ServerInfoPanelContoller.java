/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.FlowLayout;

import epics.archiveviewer.xal.view.ServerInfoPanel;
import epics.archiveviewer.xal.view.components.AVDialog;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerInfoPanelContoller {
	public ServerInfoPanelContoller(AVController avc)
	{
		try
		{
			String s = avc.getAVBase().getClient().getServerInfoText();
			ServerInfoPanel sip = new ServerInfoPanel();
			sip.getTextPad().setText(s);
			new AVDialog(
					sip,
					avc.getMainWindow(),
					"Server Info",
					false,
					true,
					avc.getMainAVPanel().getPlotPluginsWrapperPane(),
					null, FlowLayout.CENTER
				);
					
		}
		catch(Exception e)
		{
			avc.getAVBase().displayError("Can't process server information", e);
		}
	}
}
