/**
 * 
 */
package epics.archiveviewer.xal.view.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author chevtsov
 *
 */
public class PreferencesPanel extends AVAbstractPanel
{	
	private static final int GAP_SIZE = 20;
	
	private PlotPluginsLoaderPanel plotPluginsLoaderPanel;
	private LegendConfiguratorPanel legendConfiguratorPanel;
	private OtherPlotSettingsPanel otherPlotSettingsPanel;
	
	private JLabel categoriesLabel;
	private JComboBox categoriesBox;
	private JPanel inputPanel;

	
	//Constructor
	public PreferencesPanel()
	{
		init();
	}

	/**
	 * @see epics.archiveviewer.xal.view.components.AVAbstractPanel#createComponents()
	 */
	protected void createComponents()
	{
		this.plotPluginsLoaderPanel = new PlotPluginsLoaderPanel();
		this.legendConfiguratorPanel = new LegendConfiguratorPanel();
		this.otherPlotSettingsPanel = new OtherPlotSettingsPanel();
		
		this.categoriesLabel = new JLabel("Categories");
		this.categoriesBox = new JComboBox();
		this.inputPanel = new JPanel();
		this.inputPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	/**
	 * @see epics.archiveviewer.xal.view.components.AVAbstractPanel#addComponents()
	 */
	protected void addComponents()
	{	
		JPanel categoriesPanel = new JPanel(new BorderLayout(GAP_SIZE, 0));
		categoriesPanel.add(this.categoriesLabel, BorderLayout.WEST);
		categoriesPanel.add(this.categoriesBox, BorderLayout.CENTER);
		
		JPanel categoriesPanel2 = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(GAP_SIZE, GAP_SIZE, GAP_SIZE, GAP_SIZE);
		categoriesPanel2.add(categoriesPanel, gbc);
		
		JPanel inputPanel2 = new JPanel(new BorderLayout());
		inputPanel2.add(this.inputPanel, BorderLayout.CENTER);
		inputPanel2.setBorder(BorderFactory.createEmptyBorder(0,GAP_SIZE,GAP_SIZE,GAP_SIZE));
		
		setLayout(new BorderLayout());
		add(categoriesPanel2, BorderLayout.NORTH);
		add(inputPanel2, BorderLayout.CENTER);
	}

	public JComboBox getCategoriesBox()
	{
		return this.categoriesBox;
	}
	
	public JPanel getContentPanel()
	{
		return this.inputPanel;
	}

	public LegendConfiguratorPanel getLegendConfiguratorPanel()
	{
		return this.legendConfiguratorPanel;
	}

	public OtherPlotSettingsPanel getOtherPlotSettingsPanel()
	{
		return this.otherPlotSettingsPanel;
	}

	public PlotPluginsLoaderPanel getPlotPluginsLoaderPanel()
	{
		return this.plotPluginsLoaderPanel;
	}	
}
