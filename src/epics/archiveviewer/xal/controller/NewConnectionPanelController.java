/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.swing.JComboBox;

import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.xal.view.NewConnectionPanel;
import epics.archiveviewer.xal.view.components.AVDialog;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewConnectionPanelController
{
    private final ActionListener commitConnectionParameterListener;
    
    private ActionListener createCommitConnectionParameterListener(final AVController avController, final JComboBox paramsBox)
    {
        return new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                LinkedHashSet parameters = new LinkedHashSet();
                String s = paramsBox.getEditor().getItem().toString();
                if(s != null && s.equals("") == false)
                    parameters.add(s.trim());
                for(int i=0; i<paramsBox.getItemCount(); i++)
                {
                    s = paramsBox.getItemAt(i).toString();
                    parameters.add(s.trim());
                }
                AVBaseConstants.AV_PREFERENCES.put(
                		AVBaseConstants.CONNECTION_PREFS_KEY,
                        AVBaseUtilities.assemble(
                        		(String[]) parameters.toArray(new String[parameters.size()]),
                        		AVBaseConstants.PREFERENCES_VALUES_DELIM
                        	)
                       );
                
                try
                {
                	avController.connect(paramsBox.getSelectedItem().toString(), true);
                }
                catch(Exception e)
                {
                	avController.getAVBase().displayError("Couldn't connect", e);
                }
            }            
        };
    }
    
    private ActionListener createAddEditedParameterToParametersBoxListener(final AVController avController, final JComboBox paramsBox)
    {
        return new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                String newParameter = paramsBox.getEditor().getItem().toString().trim();
                HashSet parameters = new HashSet();
                String s = null;
                for(int i=0; i<paramsBox.getItemCount(); i++)
                {
                    s = paramsBox.getItemAt(i).toString();
                    parameters.add(s.trim());
                }
                
                if(parameters.contains(newParameter) == false)
                    paramsBox.insertItemAt(newParameter, 0);
            }            
        };
    }
    
    public NewConnectionPanelController(AVController avController) throws Exception
    {

        NewConnectionPanel ncp = new NewConnectionPanel();
        
        {
        	String[] connectionParameters = 
        	AVBaseUtilities.tokenize(
        			AVBaseConstants.AV_PREFERENCES.get(AVBaseConstants.CONNECTION_PREFS_KEY, ""),
        			AVBaseConstants.PREFERENCES_VALUES_DELIM
        		);
	        
	        if(connectionParameters != null)
	        {   
		        for(int i=0; i<connectionParameters.length; i++)
		        {
		            ncp.getParametersBox().addItem(connectionParameters[i]);
		        }
		        if(connectionParameters.length > 0)
		            ncp.getParametersBox().setSelectedIndex(0);
	        }
        }
	        
        JComboBox paramsBox = ncp.getParametersBox();
        this.commitConnectionParameterListener = createCommitConnectionParameterListener(avController, paramsBox);
        paramsBox.getEditor().addActionListener(createAddEditedParameterToParametersBoxListener(avController, paramsBox));
        
        new AVDialog(
                ncp,
                avController.getMainWindow(),
                "New Connection",
                true,
                true,
                avController.getMainAVPanel(),
                this.commitConnectionParameterListener, FlowLayout.CENTER
                );
    }
}
