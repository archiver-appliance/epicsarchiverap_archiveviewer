/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.export;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import epics.archiveviewer.AVEntry;
import epics.archiveviewer.RetrievalMethod;
import epics.archiveviewer.base.fundamental.FormulaGraph;
import epics.archiveviewer.base.fundamental.Graph;
import epics.archiveviewer.base.model.ExportModel;
import epics.archiveviewer.base.model.PlotModel;
import epics.archiveviewer.base.model.listeners.ProgressListener;
import epics.archiveviewer.base.util.AVProgressTask;
import epics.archiveviewer.base.util.TimeParser;
import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.controller.AVController;
import epics.archiveviewer.xal.controller.listeners.AbstractPeriodNumberValuesListener;
import epics.archiveviewer.xal.view.axesconfigurators.TimeAxesConfigurator;
import epics.archiveviewer.xal.view.components.AVDialog;
import epics.archiveviewer.xal.view.export.ExportFeaturesMenu;
import epics.archiveviewer.xal.view.export.ExportOptionsPanel;
import epics.archiveviewer.xal.view.export.MainExportPanel;
import epics.archiveviewer.xal.view.export.OutputPanel;
import epics.archiveviewer.xal.view.timeinput.TimeInputPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportController {
	//100 kB
	private static final int STRING_BUFFER_SIZE = 100 * 2^10;
	
	private final AVController avController;
	private final MainExportPanel mainExportPanel;
	private final ExportFeaturesMenu exportFeaturesMenu;
	private final TimeAxesConfigurator tac;
	private final OutputPanel outputPanel;
	private final AVDialog mainExportDialog;
	private final ExportOptionsPanel exportOptionsPanel;
	
	//either hidden, or null
	private AVDialog exportOptionsDialog;

	private Writer exportWriter;
	
	private WindowListener createExportOutputWindowListener()
	{
		return new WindowAdapter()
		{
			public void windowClosed(WindowEvent e) {
				try
				{
					if(exportWriter != null)
					{
						exportWriter.close();
					}
				}
				catch(Exception ex)
				{
					avController.getAVBase().displayError("Couldn't close export output writer", ex);
				}
				avController.getMainAVPanel().getStatusPanel().getInterruptButton().doClick();
			}
		};
	}
	/*
	 * Added SliderTimePanel
	 * @param tip
	 * @param textField
	 * @return
	 * modified on: 8/1/06
	 * John Lee
	 */
	private ActionListener createTimePanelOKListener(
			final TimeInputPanel tip, final JTextField textField)
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				if(tip.getAbsoluteTP().isVisible())
				{
					textField.setText(tip.getAbsoluteTP().getSelectedTime());
				}
				else if (tip.getRelativeTP().isVisible())
					textField.setText(tip.getRelativeTP().getSelectedTime());
				
				else if (tip.getSliderTP().isVisible())
					textField.setText(tip.getSliderTP().getSelectedTime());
			}
			
		};
	}
	
	private ActionListener createTimePanelButtonListener(final boolean forStartTime)
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
			 	JTextField timeField = null;
			 	JButton locationButton = null;
			 	if(forStartTime)
			 	{
			 		timeField = mainExportPanel.getStartTimeField();
			 		locationButton = mainExportPanel.getStartTimePanelDisplayButton();
			 	}
			 	else
			 	{
			 		timeField = mainExportPanel.getEndTimeField();
			 		locationButton = mainExportPanel.getEndTimePanelDisplayButton();
			 	}
			 	TimeInputPanel tip = 
			 		new TimeInputPanel(mainExportPanel.getStartTimeField().getText(), mainExportPanel.getEndTimeField().getText(), forStartTime);
			 	
			 	
                new AVDialog(
                        tip,
                        avController.getMainWindow(),
                        "Time Input Dialog",
                        false,
                        false,
                        locationButton,
                        createTimePanelOKListener(tip, timeField), 
                        FlowLayout.RIGHT
                        );
			}
			
		};
	}
	private void createStringWriter()
	{
		final Runnable doDisplayData = 
			new Runnable()
			{
				public void run()
				{
					if(exportWriter == null)
						return;
					StringBuffer sb = ((StringWriter)exportWriter).getBuffer();
					
					JTextArea exportOutputArea = outputPanel.getTextArea();
					
					if(outputPanel.getScrollPane().getVerticalScrollBar().isEnabled())
					{
						exportOutputArea.setText(sb.toString());
						try
						{
							exportWriter.close();
						}
						catch(IOException e)
						{
							avController.getAVBase().displayError("Can't close error writer", e);
						}
						exportWriter = null;
					}
					else
					{
						int sbLength = sb.length();		
						
						int lineLength = -1;
						
						try
						{
							lineLength =
								exportOutputArea.getLineEndOffset(sbLength - 1) - 
								exportOutputArea.getLineStartOffset(sbLength - 1);
						}
						catch(Exception e)
						{
							lineLength = 200;
						}
						//either all characters from the beginning or the last couple of them
						int nrOfCharacters = (exportOutputArea.getRows() + 1) * lineLength;
						int firstIndex = Math.max(0, sbLength - nrOfCharacters);
						
						exportOutputArea.setText(sb.substring(firstIndex, sbLength));
					}
				}
			};
			
		this.exportWriter = new StringWriter(STRING_BUFFER_SIZE)
		{			
			public void flush()
			{
				super.flush();
				SwingUtilities.invokeLater(doDisplayData);
			}
		};
	}
	
	private ProgressListener createInterruptListener(final AVProgressTask avp)
	{
		return new ProgressListener(avp)
		{
			public void actionPerformed(ActionEvent e) {
				if(avp.getCurrentValue() >= AVProgressTask.MAX)
				{
					avController.getAVBase().removeProgressListener(this);
					interrupt();
				}
			}
			
			//called when interrupt button is pushed
			//might be that we flushed before we called interrupt()
			public void interrupt()
			{
				outputPanel.getScrollPane().getVerticalScrollBar().setEnabled(true);
				try
				{
					exportWriter.flush();
					exportWriter.close();
				}
				catch(Exception e)
				{
					//do nothing
				}
			}
		};
	}
	
	private ActionListener createSaveOutputToFileListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				String filePath = outputPanel.getFilePathField().getText().trim();
				if(filePath.equals("") == false)
				{
					try
					{
						FileWriter fw = new FileWriter(outputPanel.getFilePathField().getText());
						fw.write(outputPanel.getTextArea().getText());
						fw.flush();
						fw.close();
					}
					catch(Exception ex)
					{
						avController.getAVBase().displayError("Couldn't write output to file", ex);
					}
				}
			}
			
		};
	}
	
	private void handleMainCommit()
	{
		try
		{
			ArrayList pvNames = new ArrayList();
			ArrayList formulaGraphs = new ArrayList();
			
			PlotModel plotModel = avController.getAVBase().getPlotModel();
			
			String selectedAD = 	
				(String) avController.getMainAVPanel().getAVEsPanel().
							getAVEsSelectorPanel().getArchiveDirectoriesSelectionBox().getSelectedItem();
			
			AVEntry[] aves = avController.getAVBase().getPlotModel().getAVEntries();

			int[] selectedRows = null;
			int i=0;
			if(mainExportPanel.getSelectedAVEsOnlyBox().isSelected())
			{
				selectedRows = 
					avController.getMainAVPanel().getAVEsPanel().getAVEsTable().getSelectedRows();
			}
			else
			{
				//do as if everything were selected
				selectedRows = new int[aves.length];
				while(i<selectedRows.length)
				{
					selectedRows[i] = i;
					i++;
				}
			}
			
			Graph g = null;
			int selectedIndex = -1;
			
			for(i=0; i<selectedRows.length; i++)
			{
				selectedIndex = selectedRows[i];
				g = plotModel.getGraph(aves[selectedIndex]);
				if(g instanceof FormulaGraph)
					formulaGraphs.add(g);
				else
					pvNames.add(g.getAVEntry().getName());
			}
			
			String countFieldText = exportOptionsPanel.getCountField().getText();
			int nrValues = -1;
			if(countFieldText.equals(""))
			{
				nrValues = Integer.MAX_VALUE;
			}
			else
				nrValues = Integer.parseInt(countFieldText);
			
			String filename = "";
			AVDialog exportOutputDialog = null;
			if(mainExportPanel.getFilePathField().getText().trim().equals(""))
			{
				createStringWriter();
				outputPanel.getScrollPane().getVerticalScrollBar().setEnabled(false);
				
				exportOutputDialog =
					new AVDialog(
		                outputPanel,
		                avController.getMainWindow(),
		                "Export Output",
		                false,
		                true,
		                avController.getMainAVPanel().getPlotPluginsWrapperPane(),
		                createSaveOutputToFileListener(), FlowLayout.RIGHT
		                );
				exportOutputDialog.addWindowListener(createExportOutputWindowListener());
			}
			else
			{
				//to provide the correct extension of the writer and the filename
				String ext = avController.getAVBase().getExportersRepository().getExporter((String)mainExportPanel.getExporterIdsBox().getSelectedItem()).getExt();
				if (mainExportPanel.getFilePathField().getText().endsWith(ext))
					filename = mainExportPanel.getFilePathField().getText();
				else
					filename = mainExportPanel.getFilePathField().getText()+ext;
				this.exportWriter = new FileWriter(filename);
			}
			
			ExportModel exportModel = new ExportModel(
					avController.getAVBase().getPlotModel().getConnectionParameter(),
					selectedAD,
					(String[]) pvNames.toArray(new String[pvNames.size()]),
					nrValues,
					mainExportPanel.getStartTimeField().getText(),
					mainExportPanel.getEndTimeField().getText(),
					(String) exportOptionsPanel.getMethodBox().getSelectedItem(),
					exportOptionsPanel.getTSFormatField().getText(),
					exportOptionsPanel.getExportStatusBox().isSelected(),
					exportWriter
					);
			// to let the export model to hold the filename
			exportModel.setWriterFilename(mainExportPanel.getFilePathField().getText());
			// to let the export model to hold the count per ave per retrival 
			exportModel.setCountPerAvePerRetrival(exportOptionsPanel.getCountPerRetrival());
			FormulaGraph fg = null;
			for(i=0; i<formulaGraphs.size(); i++)
			{
				fg = (FormulaGraph) formulaGraphs.get(i);
				exportModel.addFormula(fg);
			}
					
			avController.getAVBase().setExportModel(exportModel);
			
			AVProgressTask avp = 
				avController.export((String) mainExportPanel.getExporterIdsBox().getSelectedItem());
			if(this.exportWriter instanceof StringWriter)
			{
				avController.getAVBase().addProgressListener(createInterruptListener(avp));
			}
		}
		catch(Exception ex)
		{
			avController.getAVBase().displayError("Can't process export parameters", ex);
		}
	}
	
	private ActionListener createMainCommitListener()
	{
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				handleMainCommit();
			}
			
		};
	}
	
	private ActionListener createExportFeaturesListener()
	{
        return new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                exportFeaturesMenu.getPopupMenu().show(
                		mainExportPanel.getFeaturesButton(), 0, 0);
            }
        };
	}
	
	private void resetAndCloseExportOptionsDialog()
	{
		if(this.exportOptionsPanel.getMethodBox().getItemCount() > 0)
		{
			this.exportOptionsPanel.getMethodBox().setSelectedIndex(0);
		}
		this.exportOptionsPanel.getCountField().setText("");
		this.exportOptionsPanel.getPeriodField().setText("");
		this.exportOptionsPanel.getTSFormatField().setText("");
		this.exportOptionsPanel.getExportStatusBox().setSelected(true);
		this.exportOptionsPanel.getCountPerRetrivalSlider().setValue(3);
		
		if(this.exportOptionsDialog != null)
		{
			this.exportOptionsDialog.dispose();
			this.exportOptionsDialog = null;
		}
	}
    
    private ActionListener createExportOptionsDisplayListener()
    {
    	return new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	if(exportOptionsDialog == null)
            	{
            		//create a new dialog
	            	exportOptionsDialog = new AVDialog(
	            			exportOptionsPanel,
	            			avController.getMainWindow(),
	            			"Export Options",
	            			false,
	            			true,
	            			mainExportPanel.getFeaturesButton(),
	            			null, -1);
	            	
	            	Point p = mainExportDialog.getLocationOnScreen();
	            	
	            	
	            	exportOptionsDialog.setLocation(
	            			p.x + mainExportDialog.getWidth(),
	            			p.y
	            	);
	            	
	            	exportOptionsDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	            	
	            	exportOptionsDialog.addWindowListener(new WindowAdapter()
        			{
						public void windowClosing(WindowEvent e)
						{
							JOptionPane.showConfirmDialog(
									exportOptionsDialog, 
									"Use the RESET & CLOSE button only to close this dialog.", 
									"Please...", 
									JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.WARNING_MESSAGE);
						}	            		
        			});
            	}
            	else
            		exportOptionsDialog.setVisible(true);
            }
        };
    }
	
    private ActionListener createLoadTimeListener()
    {
		return new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				mainExportPanel.getStartTimeField().setText(tac.getStartTimeField().getText());
				mainExportPanel.getEndTimeField().setText(tac.getEndTimeField().getText());
			}
			
		};    	
    }
    
    private KeyListener createPeriodNrValsListener(JTextField field)
    {
    	return new AbstractPeriodNumberValuesListener(field)
    	{

			protected double getCurrentTimeRangeInSeconds() {
				try
				{
					String startTime = mainExportPanel.getStartTimeField().getText();
					String endTime = mainExportPanel.getEndTimeField().getText();
					
					Date[] dates = TimeParser.parse(startTime, endTime);
					return (dates[1].getTime() - dates[0].getTime())/1000;
				}
				catch(Exception e)
				{
					return 0;
				}
			}
    		
    	};
    }
    
    private WindowListener createMainExportWindowListener()
    {
    	return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e) {
				//hide the options dialog if it is not null
				if(exportOptionsDialog != null)
				{
					//just hide, do not dispose
					exportOptionsDialog.setVisible(false);
				}
			}
		};
    }
    
	public ExportController (AVController avc)
	{
		this.avController = avc;
		this.mainExportPanel = new MainExportPanel();
		this.exportFeaturesMenu = new ExportFeaturesMenu();
		
		this.exportOptionsPanel = new ExportOptionsPanel();	
		//presets
    	RetrievalMethod[] rms = avController.getAVBase().getClient().getRetrievalMethodsForExport();
    	for(int i=0; i<rms.length; i++)
    	{
    		this.exportOptionsPanel.getMethodBox().addItem(rms[i].getName());
    	}
		
    	this.exportOptionsPanel.getExportStatusBox().setSelected(true);
    	
    	this.exportOptionsPanel.getCountField().addKeyListener(
    			createPeriodNrValsListener(this.exportOptionsPanel.getPeriodField())
    			);
    	
    	this.exportOptionsPanel.getPeriodField().addKeyListener(
    			createPeriodNrValsListener(this.exportOptionsPanel.getCountField())
    			);
    	
    	this.exportOptionsPanel.getPeriodField().setToolTipText(AVXALConstants.PERIOD_TOOLTIP);
    	
    	this.exportOptionsPanel.getResetAndCloseButton().addActionListener(
    			new ActionListener()
    			{
					public void actionPerformed(ActionEvent e) {
						resetAndCloseExportOptionsDialog();
					}            				
    			});
		
		this.outputPanel = new OutputPanel();
		this.tac = this.avController.getMainAVPanel().getAxesSettingsPanel().getTimeAxesConfigurator();		
		
		
		int i=0;
		
		String[] exporterIds = this.avController.getAVBase().getExportersRepository().getRegisteredIds();
		for(i=0; i<exporterIds.length; i++)
		{
			this.mainExportPanel.getExporterIdsBox().addItem(exporterIds[i]);
		}
		
		if(exporterIds.length == 1)
			this.mainExportPanel.getExporterIdsBox().setEnabled(false);
		
		this.mainExportPanel.getStartTimeField().setToolTipText(AVXALConstants.TIME_INPUT_TOOLTIP);
		this.mainExportPanel.getEndTimeField().setToolTipText(AVXALConstants.TIME_INPUT_TOOLTIP);
		
		
		//listeners
    	this.exportFeaturesMenu.getLoadTimeBoundsItem().addActionListener(
    			createLoadTimeListener());
    	this.exportFeaturesMenu.getLoadTimeBoundsItem().doClick();
    	
		this.mainExportPanel.getFeaturesButton().addActionListener(
				createExportFeaturesListener());
		
		this.exportFeaturesMenu.getShowMoreOptionsItem().addActionListener(
				createExportOptionsDisplayListener());
		
		
		
		this.mainExportPanel.getStartTimePanelDisplayButton().addActionListener(
				createTimePanelButtonListener(true));
		this.mainExportPanel.getEndTimePanelDisplayButton().addActionListener(
				createTimePanelButtonListener(false));
		
		new FileChooserController(
				this.avController.getAVBase().getExportDirectory(),
				this.mainExportPanel.getFilePathField(),
				this.mainExportPanel.getFileChooserButton()
			);
		
		new FileChooserController(
				this.avController.getAVBase().getExportDirectory(),
				this.outputPanel.getFilePathField(),
				this.outputPanel.getFileChooserButton()
			);
		
		this.mainExportDialog =
			new AVDialog(
                mainExportPanel,
                avController.getMainWindow(),
                "Export",
                false,
                true,
                avController.getMainAVPanel().getPlotPluginsWrapperPane(),
                createMainCommitListener(), FlowLayout.CENTER
                );
		
		this.mainExportDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.mainExportDialog.addWindowListener(createMainExportWindowListener());
	}
	
	public void showExportDialog()
	{
		this.mainExportDialog.setVisible(true);
		if(this.exportOptionsDialog != null)
			this.exportOptionsDialog.setVisible(true);
	}
}
