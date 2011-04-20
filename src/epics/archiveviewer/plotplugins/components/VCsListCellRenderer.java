/*
 * Created on Jan 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.components;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import epics.archiveviewer.ValuesContainer;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VCsListCellRenderer extends DefaultListCellRenderer
{
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus)
	{
		if(value != null)
		{
			try
			{
				ValuesContainer vc = (ValuesContainer) value;
				setToolTipText(vc.getAVEntry().getArchiveDirectory().toString());					
				value = vc.getAVEntry().getName();
			}
			catch(Exception e)
			{
				value = null;
			}
		}
		
		return super.getListCellRendererComponent(list,
				value, index, isSelected,
				cellHasFocus);	
	}
}
