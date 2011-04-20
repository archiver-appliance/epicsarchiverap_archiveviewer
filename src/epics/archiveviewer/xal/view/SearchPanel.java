/*
 * Created on Feb 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;
import epics.archiveviewer.xal.view.tables.MatchingAVEsTable;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPanel extends AVAbstractPanel
{	
	private static final int DEFAULT_NR_VISIBLE_PVS = 15;
	
	private JRadioButton globPatternButton;
	private JRadioButton regExButton;	
	private JTextField inputField;
	private JCheckBox caseSensitiveBox;
	private JCheckBox selectArchivesBox;
	private JList archivesList;
	private JScrollPane archivesListScrollPane;
	private JButton goButton;
	private MatchingAVEsTable matchesTable;
	private JButton addButton;
	private JButton clearButton;
	
	public SearchPanel()
	{
		init();
		addListeners();
	}

	protected void addComponents() {
		
		JPanel patternButtonsPanel = new JPanel(new BorderLayout());
		patternButtonsPanel.add(this.globPatternButton, BorderLayout.WEST);
		patternButtonsPanel.add(this.regExButton, BorderLayout.CENTER);
		
		this.archivesListScrollPane.setViewportView(this.archivesList);
		JPanel archivesListPanel = new JPanel(new BorderLayout());
		archivesListPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
		archivesListPanel.add(this.archivesListScrollPane, BorderLayout.NORTH);
		
		JPanel selectArchivesPanel = new JPanel(new BorderLayout());
		selectArchivesPanel.add(this.selectArchivesBox, BorderLayout.NORTH);
		selectArchivesPanel.add(archivesListPanel, BorderLayout.CENTER);
	
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 10, 0, 10);
		JPanel goButtonPanel = new JPanel(new GridBagLayout());
		goButtonPanel.add(this.goButton, gbc);
		
		JPanel caseSensitiveBoxPanel = new JPanel(new BorderLayout());
		caseSensitiveBoxPanel.add(this.caseSensitiveBox, BorderLayout.WEST);
		
		JPanel searchStringPanel = new JPanel(new BorderLayout());
		searchStringPanel.add(patternButtonsPanel, BorderLayout.NORTH);
		searchStringPanel.add(this.inputField, BorderLayout.CENTER);
		
		JPanel searchParametersPanel = new JPanel(new BorderLayout());
		searchParametersPanel.add(searchStringPanel, BorderLayout.NORTH);
		searchParametersPanel.add(selectArchivesPanel, BorderLayout.CENTER);
		searchParametersPanel.add(caseSensitiveBoxPanel, BorderLayout.SOUTH);
		searchParametersPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 5));
		
		JPanel searchParametersAndGoPanel = new JPanel(new BorderLayout());
		searchParametersAndGoPanel.add(searchParametersPanel, BorderLayout.CENTER);
		searchParametersAndGoPanel.add(goButtonPanel, BorderLayout.EAST);
		
		JPanel addClearButtonsPanel = new JPanel(new GridLayout(1,0));
		addClearButtonsPanel.add(this.addButton);
		addClearButtonsPanel.add(this.clearButton);
		
		JPanel addClearButtonsPanel2 = new JPanel(new BorderLayout());
		addClearButtonsPanel2.add(addClearButtonsPanel, BorderLayout.EAST);
		addClearButtonsPanel2.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		setLayout(new BorderLayout());
		this.add(searchParametersAndGoPanel, BorderLayout.WEST);
		this.add(new JScrollPane(this.matchesTable), BorderLayout.CENTER);
		this.add(addClearButtonsPanel2, BorderLayout.SOUTH);
	}
	
	//another exception
	protected void addListeners()
	{
		this.inputField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				goButton.setEnabled(!inputField.getText().equals(""));
			}
		});
		
		this.globPatternButton.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				caseSensitiveBox.setEnabled(true);
				caseSensitiveBox.setSelected(false);
			}
		});
		
		this.regExButton.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				caseSensitiveBox.setEnabled(false);
				caseSensitiveBox.setSelected(true);
			}
		});

		this.selectArchivesBox.addActionListener(new AbstractAction()
		{

			public void actionPerformed(ActionEvent e) {
				archivesList.setEnabled(selectArchivesBox.isSelected());		
			}
	
		});
	}
	
	protected void createComponents() {
		this.globPatternButton = new JRadioButton("Glob Pattern", true);
		this.regExButton = new JRadioButton("Regular Expression", false);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.globPatternButton);
		bg.add(this.regExButton);		
		
		this.inputField = new JTextField(15);
		this.caseSensitiveBox = new JCheckBox("Case Sensitive", false);
		this.selectArchivesBox = new JCheckBox("Select Archives...", false);
		
		this.archivesList = new JList();
		this.archivesList.setEnabled(false);
		
		this.archivesListScrollPane = new JScrollPane();
		
		this.goButton = new JButton("Go");
		this.goButton.setEnabled(false);
		
		this.matchesTable = new MatchingAVEsTable();
		
		this.addButton = new JButton("Add");
		this.clearButton = new JButton("Clear");
	}
	
	public MatchingAVEsTable getMatchingAVEsTable()
	{
		return this.matchesTable;
	}
	
	public JButton getGoButton()
	{
		return this.goButton;
	}
	
	public JTextField getInputField()
	{
		return this.inputField;
	}
	
	public JRadioButton getRegExButton()
	{
		return this.regExButton;
	}
	
	public JRadioButton getGlobButton()
	{
		return this.globPatternButton;
	}
	
	public JCheckBox getCaseSensitiveBox()
	{
		return this.caseSensitiveBox;
	}
	
	public JCheckBox getSelectArchivesBox()
	{
		return this.selectArchivesBox;
	}
	
	public JList getArchivesList()
	{
		return this.archivesList;
	}
	
	public JScrollPane getArchivesListScrollPane()
	{
		return this.archivesListScrollPane;
	}
	
	public JButton getAddButton()
	{
		return this.addButton;
	}
	
	public JButton getClearButton()
	{
		return this.clearButton;
	}
}
