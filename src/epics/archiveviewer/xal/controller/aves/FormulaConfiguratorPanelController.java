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
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.FormulaParameter;
import epics.archiveviewer.base.fundamental.TimeAxis;
import epics.archiveviewer.base.util.TimeParser;
import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.listeners.AbstractPeriodNumberValuesListener;
import epics.archiveviewer.xal.view.aveconfigurators.AdditionalConfiguratorPanelForFormulas;
import epics.archiveviewer.xal.view.aveconfigurators.FormulaConfiguratorPanel;
import epics.archiveviewer.xal.view.components.AVDialog;
import epics.archiveviewer.xal.view.tables.ArgumentsAndAVEsTable;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FormulaConfiguratorPanelController
{
	private final AVController avController;
	private final FormulaConfiguratorPanel fcp;
	
	private ActionListener createCommitListener(
			final CommonGraphConfiguratorPanelController cgcpController,
			final FormulaGraph fg)
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				cgcpController.storeConfiguratorParameters(fg);
		
				try
				{
					ArgumentsAndAVEsTable table = fcp.getArgumentsInputPanel().getArgumentsAndAVEsTable();

					FormulaParameter[] params = new FormulaParameter[table.getRowCount()];
					String aveName = null;
					for(int i=0; i<params.length; i++)
					{
						aveName = table.getValueAt(i, ArgumentsAndAVEsTable.AVE_COLUMN_INDEX).toString();
						params[i] = new FormulaParameter(
								table.getValueAt(i, ArgumentsAndAVEsTable.ARG_COLUMN_INDEX).toString(),
								aveName,
								avController.getAVBase().getPlotModel().isArgAVEFormula(aveName)
								);
					}
					
					String term = fcp.getCalculatorPanel().getTermField().getText();
					
					AdditionalConfiguratorPanelForFormulas acp = fcp.getAdditionalConfiguratorPanel();
					
					fg.setFormulaParametersAndTerm(params, term, true);
					fg.setRequestedNumberOfValues(Integer.parseInt(acp.getMaxCountField().getText()));
					fg.setRetrievalMethodName(acp.getMethodBox().getSelectedItem().toString());
					
					avController.getAVBase().getPlotModel().addGraph(fg);
					avController.getAVBase().getPlotModel().fireAVEsUpdated();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			
		};
	}
	
	private KeyListener createPeriodNrValsListener(JTextField field)
	{
		return new AbstractPeriodNumberValuesListener(field)
		{

			protected double getCurrentTimeRangeInSeconds() {
				String timeAxisName = (String) fcp.getCommonConfiguratorPanel().getTimeAxisBox().getSelectedItem();
				TimeAxis tA = avController.getAVBase().getPlotModel().getTimeAxis(timeAxisName);
				try
				{
					Date[] dates = TimeParser.parse(tA.getStartTime(), tA.getEndTime());
					return (dates[1].getTime() - dates[0].getTime())/1000;
				}
				catch(Exception e)
				{
					return 0;
				}
			}
			
		};
	}
	
	
	public FormulaConfiguratorPanelController(AVController avc, FormulaGraph formulaGraph)
	throws Exception
	{
		this.avController = avc;
    	this.fcp = new FormulaConfiguratorPanel();
    	
    	this.fcp.
			getHeaderPanel().
				getAVENameLabel().setText(formulaGraph.getAVEntry().getName());
    	
    	//set up
    	String adName = formulaGraph.getAVEntry().getArchiveDirectory().getName();
    	
    	this.fcp.
    		getHeaderPanel().
    			getDirectoryLabel().setText(adName);
    	
    	//all potential argument aves
    	HashSet aveNames = new HashSet();
    	AVEntry[] aves = avController.getAVBase().getPlotModel().getAVEntries();
		
    	int i=0;
    	for(i=0; i<aves.length; i++)
    	{
    		if(aves[i].getName().equals(formulaGraph.getAVEntry().getName()) == false)
    			aveNames.add(aves[i].getName());
    	}

    	new ArgumentsInputPanelController(
    			(String[])aveNames.toArray(new String[aveNames.size()]), 
    			this.fcp, 
    			formulaGraph);
    	
    	this.fcp.getCalculatorPanel().getTermField().setText(formulaGraph.getTerm());
    	
    
    	JComboBox box = this.fcp.
					    	getAdditionalConfiguratorPanel().
					    		getMethodBox();
    	
    	RetrievalMethod[] rms = avController.getAVBase().getClient().getRetrievalMethodsForCalculation();
    	for(i=0; i<rms.length; i++)
    	{
    		box.addItem(rms[i].getName());
    	}
    	box.setSelectedItem(formulaGraph.getRetrievalMethodName());
    	
    	
    	AdditionalConfiguratorPanelForFormulas additionalPanel = this.fcp.getAdditionalConfiguratorPanel();
    
    	additionalPanel.getMaxCountField().addKeyListener(
    			createPeriodNrValsListener(additionalPanel.getPeriodField())
    			);
    	
    	additionalPanel.getPeriodField().addKeyListener(
    			createPeriodNrValsListener(additionalPanel.getMaxCountField())
    			);
    	
    	additionalPanel.getPeriodField().setToolTipText(AVXALConstants.PERIOD_TOOLTIP);
    	
    	additionalPanel.getMaxCountField().setText(Integer.toString(formulaGraph.getRequestedNumberOfValues()));
    	
    	CommonGraphConfiguratorPanelController cgcpController =
    		new CommonGraphConfiguratorPanelController(avController, this.fcp.getCommonConfiguratorPanel(), formulaGraph);
    	
        new AVDialog(
                this.fcp,
                avController.getMainWindow(),
                "Formula Configurator",
                true,
                true,
                avController.getMainAVPanel().getPlotPluginsWrapperPane(),
                createCommitListener(
            			cgcpController,
            			formulaGraph), FlowLayout.RIGHT
                );
	}
}
