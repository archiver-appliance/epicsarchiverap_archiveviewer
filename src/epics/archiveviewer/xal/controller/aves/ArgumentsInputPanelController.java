/*
 * Created on Feb 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.aves;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.xal.controller.listeners.RemoveSelectedTableRowsListener;
import epics.archiveviewer.xal.view.aveconfigurators.ArgumentsInputPanel;
import epics.archiveviewer.xal.view.aveconfigurators.FormulaConfiguratorPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArgumentsInputPanelController {
	private final ArgsAndAVEsTableController argsAndAVEsTableController;
	
	private void addSelectedAVENamesFromListToArgsAndAVEsTable(ArgumentsInputPanel aip)
	{
		Object[] aveNames = aip.getAVEsList().getSelectedValues();
		for(int i=0; i<aveNames.length; i++)
		{
			argsAndAVEsTableController.addAVENameAndArg(aveNames[i].toString(), null);
		}
	}
	
	private ActionListener createAddAVENamesListener(final ArgumentsInputPanel aip)
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				addSelectedAVENamesFromListToArgsAndAVEsTable(aip);
			}	
		};
	}
	
	private MouseListener createAddAVENamesOnDoubleClickListener(final ArgumentsInputPanel aip)
	{
		return new MouseAdapter()
		{
			private boolean elementAlreadyClicked = false;

			private int currentIndex = -1;

			public void mousePressed(MouseEvent e)
			{
				if ((currentIndex == aip.getAVEsList().locationToIndex(e.getPoint()))
						&& elementAlreadyClicked)
				{
					addSelectedAVENamesFromListToArgsAndAVEsTable(aip);

					//reset
					currentIndex = -1;
					elementAlreadyClicked = false;
				}
				else
				{
					currentIndex = aip.getAVEsList().getSelectedIndex();
					elementAlreadyClicked = true;
				}
			}
		};
	}
	
	public ArgumentsInputPanelController(String[] aveNames, FormulaConfiguratorPanel fcp, FormulaGraph fg)
	throws Exception
	{
		this.argsAndAVEsTableController = new ArgsAndAVEsTableController(fcp, fg);
		
		ArgumentsInputPanel aip = fcp.getArgumentsInputPanel();
	
		aip.getAVEsList().setListData(aveNames);
		
		aip.getAVEsList().addMouseListener(createAddAVENamesOnDoubleClickListener(aip));	
		aip.getAddButton().addActionListener(createAddAVENamesListener(aip));		
		aip.getRemoveButton().addActionListener(
				new RemoveSelectedTableRowsListener(aip.getArgumentsAndAVEsTable()));
	}
}
