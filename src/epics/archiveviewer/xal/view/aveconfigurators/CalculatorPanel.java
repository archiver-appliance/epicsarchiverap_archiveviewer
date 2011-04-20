package epics.archiveviewer.xal.view.aveconfigurators;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import epics.archiveviewer.xal.controller.listeners.InsertStringIntoTermListener;
import epics.archiveviewer.xal.controller.listeners.ToggleSignOfATermListener;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * This class is a common calculator panel with keys/buttons for digits and
 * functions
 * 
 * @author Sergei Chevtsov
 */
public class CalculatorPanel extends AVAbstractPanel
{
	private static final int NR_OF_DIGITS = 10;
	/** an array of mathematical constants PI and E */
	private static final String[] constants =
	{
			"e", "pi"
	}; //2

	/** an array of mathematical characters */
	private static final String[] characters =
	{
			".", "+/-", "+", "-", "*", "/", "^", "(", ")", ",", "||", "&&", "==", "!", ">", "<"
	}; //16

	private static final String[] functions =
	{
			"abs", "acos", "asin", "atan", "cos", "log", "sin", "sqrt", "tan", "min", "max", "amean"
	}; //12

	/** the field where the term is displayed */
	private JTextField termField;

	/** an array of buttons for digits */
	private JButton[] digitButtons; //10

	/** an array of buttons for mathematical constants */
	private JButton[] constantButtons; //2

	/** an array of buttons for mathematical characters */
	private JButton[] characterButtons; //13

	/** an array of buttons for mathematical functions */
	private JButton[] functionButtons; //11 => abs goes to the west panel

	/**
	 * Creates a new instance of <CODE>CalculatorPanel</CODE>
	 * 
	 * @param aController
	 *            the <CODE>Controller</CODE> instance
	 */
	public CalculatorPanel()
	{
		init();
		addListeners();
	}

	/** Lays out the componenets */
	protected void addComponents()
	{
		JPanel buttonsPanel = new JPanel(new GridLayout(0, 6));
		buttonsPanel.add(this.digitButtons[1]);
		buttonsPanel.add(this.digitButtons[2]);
		buttonsPanel.add(this.digitButtons[3]);
		buttonsPanel.add(this.characterButtons[2]);
		buttonsPanel.add(this.characterButtons[7]);
		buttonsPanel.add(this.characterButtons[8]);
		//
		buttonsPanel.add(this.digitButtons[4]);
		buttonsPanel.add(this.digitButtons[5]);
		buttonsPanel.add(this.digitButtons[6]);
		buttonsPanel.add(this.characterButtons[3]);
		buttonsPanel.add(this.functionButtons[5]);
		buttonsPanel.add(this.functionButtons[7]);
		//
		buttonsPanel.add(this.digitButtons[7]);
		buttonsPanel.add(this.digitButtons[8]);
		buttonsPanel.add(this.digitButtons[9]);
		buttonsPanel.add(this.characterButtons[4]);
		buttonsPanel.add(this.functionButtons[6]);
		buttonsPanel.add(this.functionButtons[2]);
		//
		buttonsPanel.add(this.digitButtons[0]);
		buttonsPanel.add(this.constantButtons[0]);
		buttonsPanel.add(this.constantButtons[1]);
		buttonsPanel.add(this.characterButtons[5]);
		buttonsPanel.add(this.functionButtons[4]);
		buttonsPanel.add(this.functionButtons[1]);
		//
		buttonsPanel.add(this.functionButtons[0]);
		buttonsPanel.add(this.characterButtons[0]);
		buttonsPanel.add(this.characterButtons[1]);
		buttonsPanel.add(this.characterButtons[6]);
		buttonsPanel.add(this.functionButtons[8]);
		buttonsPanel.add(this.functionButtons[3]);
		//
		buttonsPanel.add(this.characterButtons[12]);
		buttonsPanel.add(this.characterButtons[9]);	
		buttonsPanel.add(this.characterButtons[10]);	
		buttonsPanel.add(this.characterButtons[11]);	
		buttonsPanel.add(this.functionButtons[9]);
		buttonsPanel.add(this.functionButtons[10]);
		//
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(new JLabel());
		buttonsPanel.add(this.characterButtons[13]);
		buttonsPanel.add(this.characterButtons[14]);
		buttonsPanel.add(this.characterButtons[15]);
		buttonsPanel.add(this.functionButtons[11]);			

		setLayout(new BorderLayout());
		add(this.termField, BorderLayout.NORTH);
		add(buttonsPanel, BorderLayout.CENTER);
	}
	
	//as an exception, we set listeners here (for now)
	protected void addListeners()
	{
		int i = 0;
		for (i = 0; i < this.digitButtons.length; i++)
		{
			this.digitButtons[i].addActionListener(new InsertStringIntoTermListener(this.termField, i + ""));
		}

		for (i = 0; i < this.constantButtons.length; i++)
		{
			this.constantButtons[i].addActionListener(new InsertStringIntoTermListener(this.termField, constants[i]));
		}

		for (i = 0; i < this.characterButtons.length; i++)
		{
			if (characters[i].equals("+/-"))
			{
				//extra handling
				this.characterButtons[i].addActionListener(new ToggleSignOfATermListener(this.termField));
			}
			else
			{
				this.characterButtons[i].addActionListener(new InsertStringIntoTermListener(this.termField, characters[i]));
			}
		}

		for (i = 0; i < this.functionButtons.length; i++)
		{
			this.functionButtons[i].addActionListener(new InsertStringIntoTermListener(this.termField, functions[i]));
		}
	}
	
	protected void createComponents()
	{
		this.termField = new JTextField();

		int i = 0;
		
		this.digitButtons = new JButton[NR_OF_DIGITS];
		for (i = 0; i < this.digitButtons.length; i++)
		{
			this.digitButtons[i] = new JButton(i + "");
		}

		this.constantButtons = new JButton[constants.length];
		for (i = 0; i < this.constantButtons.length; i++)
		{
			this.constantButtons[i] = new JButton(constants[i]);
		}

		this.characterButtons = new JButton[characters.length];
		for (i = 0; i < this.characterButtons.length; i++)
		{
			this.characterButtons[i] = new JButton(characters[i]);
		}

		this.functionButtons = new JButton[functions.length];

		for (i = 0; i < this.functionButtons.length; i++)
		{
			functionButtons[i] = new JButton(functions[i]);
		}
	}
	
	public JTextField getTermField()
	{
		return this.termField;
	}
}