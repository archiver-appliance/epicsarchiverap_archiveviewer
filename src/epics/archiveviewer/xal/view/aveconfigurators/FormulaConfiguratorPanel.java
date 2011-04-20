/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.aveconfigurators;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FormulaConfiguratorPanel extends AVAbstractPanel
{
	private AVEConfiguratorHeaderPanel headerPanel;
	private ArgumentsInputPanel argumentsInputPanel;
	private AdditionalConfiguratorPanelForFormulas additionalConfiguratorPanel;
	private CalculatorPanel calculatorPanel;
	private CommonGraphConfiguratorPanel commonConfiguratorPanel;
	
	public FormulaConfiguratorPanel()
	{
		init();
	}
	
	protected void addComponents() {
		JPanel additionalConfiguratorPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		additionalConfiguratorPanel2.add(this.additionalConfiguratorPanel);
		
		JPanel additionalConfiguratorPanel3 = new JPanel(new BorderLayout());
		additionalConfiguratorPanel3.add(additionalConfiguratorPanel2, BorderLayout.NORTH);
		
		JPanel configuratorPanel = new JPanel(new BorderLayout(0,10));
		configuratorPanel.add(additionalConfiguratorPanel3, BorderLayout.NORTH);
		configuratorPanel.add(this.commonConfiguratorPanel, BorderLayout.SOUTH);
		
		JPanel configuratorPanel2 = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		configuratorPanel2.add(configuratorPanel, gbc);
		
		JPanel calculatorPanel2 = new JPanel(new BorderLayout());
		calculatorPanel2.add(this.calculatorPanel, BorderLayout.SOUTH);
		
		JPanel argumentsAndCalculatorPanel = new JPanel(new BorderLayout(0, 10));
		argumentsAndCalculatorPanel.add(this.argumentsInputPanel, BorderLayout.NORTH);
		argumentsAndCalculatorPanel.add(calculatorPanel2, BorderLayout.SOUTH);
		
		JPanel argumentsAndCalculatorPanel2 = new JPanel(new BorderLayout());
		argumentsAndCalculatorPanel2.add(argumentsAndCalculatorPanel, BorderLayout.NORTH);
		
		setLayout(new BorderLayout(20, 0));
		add(this.headerPanel, BorderLayout.NORTH);
		add(argumentsAndCalculatorPanel2, BorderLayout.CENTER);
		add(configuratorPanel2, BorderLayout.EAST);
	}

	protected void createComponents() {
		this.headerPanel = new AVEConfiguratorHeaderPanel();
		this.argumentsInputPanel = new ArgumentsInputPanel();
		this.additionalConfiguratorPanel = new AdditionalConfiguratorPanelForFormulas();
		this.calculatorPanel = new CalculatorPanel();
		this.commonConfiguratorPanel = new CommonGraphConfiguratorPanel();
	}
	
	public AVEConfiguratorHeaderPanel getHeaderPanel()
	{
		return this.headerPanel;
	}
	
	public ArgumentsInputPanel getArgumentsInputPanel()
	{
		return this.argumentsInputPanel;
	}
	
	public AdditionalConfiguratorPanelForFormulas getAdditionalConfiguratorPanel()
	{
		return this.additionalConfiguratorPanel;
	}
	
	public CommonGraphConfiguratorPanel getCommonConfiguratorPanel()
	{
		return this.commonConfiguratorPanel;
	}
	
	public CalculatorPanel getCalculatorPanel()
	{
		return this.calculatorPanel;
	}
}
