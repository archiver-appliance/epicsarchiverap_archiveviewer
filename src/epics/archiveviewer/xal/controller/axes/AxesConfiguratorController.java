/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.axes;

import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AxesConfiguratorController {
	
	protected abstract void displaySelectedAxisParameters() throws Exception;
	
	public static final void loadAxesNamesIntoBox(String[] names, JComboBox axesNamesBox)
	{
		//remember the currently selected index and item count
		int previouslySelectedIndex = axesNamesBox.getSelectedIndex();
		int previousItemCount = axesNamesBox.getItemCount();
		
		//items get deselected/selected
		axesNamesBox.removeAllItems();
		
		for(int i=0; i<names.length; i++)
		{
			axesNamesBox.addItem(names[i]);
		}
		
		//now let's do the following:
		//0. if item count increased ( => a new axis was added), select last axis
		//1. else if the previously selected index exists, select it
		//	2. else select first time axis name
		//3. display the parameters
		int currentItemCount = axesNamesBox.getItemCount();
		if(currentItemCount > previousItemCount)
			axesNamesBox.setSelectedIndex(currentItemCount - 1);
		else
		{
			if(currentItemCount > previouslySelectedIndex)
			//try to set a previously selected axis index
				axesNamesBox.setSelectedIndex(previouslySelectedIndex);
			else
				axesNamesBox.setSelectedIndex(0);
		}
	}
	
	public abstract String getSelectedAxisName();
	public abstract JButton getMoreButton();
}
