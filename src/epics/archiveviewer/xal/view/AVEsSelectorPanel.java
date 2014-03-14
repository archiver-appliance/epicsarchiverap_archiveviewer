/*
 * Created on Feb 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEsSelectorPanel extends AVAbstractPanel
{
	private JComboBox directoriesSelectionBox;

        private JLabel densityLabel;
	private JComboBox densitySelectionBox;

	private String sparsificationOperators[] = {"lastFill", "firstSample", "lastSample", "mean", "median", "min", "max", "Raw"};

	private JButton searchButton;
	private JTextField inputField;
	private JButton newFormulaButton;
	private JButton addButton;
	private JButton removeButton;
	private JButton clearButton;
	
	public AVEsSelectorPanel(){
		super.init();
	}
	
	protected void addComponents(){
	
		GridLayout gl = new GridLayout(1,0); 
		gl.setHgap(5);
		JPanel buttonsPanel = new JPanel(gl);
		buttonsPanel.add(this.newFormulaButton);
		buttonsPanel.add(this.addButton);
		buttonsPanel.add(this.removeButton);
		buttonsPanel.add(this.clearButton);
		
		JPanel buttonsPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonsPanel2.add(buttonsPanel);
		
		JPanel buttonsPanel3 = new JPanel(new BorderLayout());
		buttonsPanel3.add(buttonsPanel2, BorderLayout.NORTH);

		JPanel densityPanel = new JPanel(new BorderLayout(4, 0));
		densityPanel.add(this.densityLabel, BorderLayout.WEST);
		densityPanel.add(this.densitySelectionBox, BorderLayout.EAST);
		
		JPanel searchButtonPanel = new JPanel(new BorderLayout());
		searchButtonPanel.add(this.searchButton, BorderLayout.WEST);

		JPanel densitySearchButtonPanel = new JPanel(new BorderLayout(10, 0));
		densitySearchButtonPanel.add(densityPanel, BorderLayout.WEST);
		densitySearchButtonPanel.add(searchButtonPanel, BorderLayout.EAST);
		
		JPanel directoriesDensitySearchButtonPanel = new JPanel(new BorderLayout(5, 0));
		directoriesDensitySearchButtonPanel.add(this.directoriesSelectionBox, BorderLayout.WEST);
		directoriesDensitySearchButtonPanel.add(densitySearchButtonPanel, BorderLayout.EAST);
		 
		JPanel inputAndDirectoriesPanel = new JPanel(new BorderLayout(0, 5));
		inputAndDirectoriesPanel.add(directoriesDensitySearchButtonPanel, BorderLayout.NORTH);
		inputAndDirectoriesPanel.add(this.inputField,BorderLayout.CENTER);
		
		inputAndDirectoriesPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	

		this.setLayout(new BorderLayout(5,0));
		
		this.add(inputAndDirectoriesPanel, BorderLayout.NORTH);
		this.add(buttonsPanel3, BorderLayout.CENTER);
	} 
	
	protected void createComponents(){
		this.directoriesSelectionBox = new JComboBox();

		this.densityLabel = new JLabel("Apply operator");
		this.densitySelectionBox = new JComboBox(sparsificationOperators);
		this.densitySelectionBox.setSelectedIndex(0);

		this.inputField = new JTextField(10);
		this.searchButton = new JButton("search");
		this.newFormulaButton = new JButton("new formula");
		this.addButton = new JButton("add");
		this.removeButton = new JButton("remove"); 
		this.clearButton = new JButton("clear");
	}

	public JComboBox getArchiveDirectoriesSelectionBox()
	{
		return this.directoriesSelectionBox;
	}

	public JComboBox getDensitySelectionBox()
	{
		return this.densitySelectionBox;
	}
	
	public String getDensitySelection() { 
		return this.sparsificationOperators[this.densitySelectionBox.getSelectedIndex()];
	}
	
	public JButton getSearchButton()
	{
		return this.searchButton;
	}
	
	public JButton getNewFormulaButton()
	{
		return this.newFormulaButton;
	}
	
	public JTextField getInputField()
	{
		return this.inputField;
	}
	
	public JButton getAddButton()
	{
		return this.addButton;
	}
	
	public JButton getRemoveButton()
	{
		return this.removeButton;
	}
	
	public JButton getClearButton()
	{
		return this.clearButton;
	}
}
 
