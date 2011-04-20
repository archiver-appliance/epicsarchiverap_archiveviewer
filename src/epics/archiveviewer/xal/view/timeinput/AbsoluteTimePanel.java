package epics.archiveviewer.xal.view.timeinput;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * This class is a panel, in which the user can select an absolute time for one
 * one of the time axes
 * 
 * @author Sergei Chevtsov
 */
public class AbsoluteTimePanel extends AVAbstractPanel
{
	/** a constant for the smallest selectable year */
	private static final int YEAR_MIN = 1990;

	/** a constant for the biggest selectable year */
	private static final int YEAR_MAX = 2030;

	/** a constant array of months names */
	private static final String[] MONTHS =
	{
			"January", "February", "March", "April", "May", "June", "July",
			"August", "September", "October", "November", "December"
	};

	/** a constant array of start letters of the names of the week days */
	private static final String[] DAYS_OF_WEEK =
	{
			"S", "M", "T", "W", "T", "F", "S"
	};
	
	/** an instance of the <CODE>java.util.Calendar</CODE> */
	private final Calendar cal;
	
	/** the initial year the calendar is shown for */
	private int initialYear;
	
	/** the table model for calendarTable */
	private final DefaultTableModel tModel;

	//date components

	/** the date label */
	private JLabel dateLabel;

	/** the months combo box */
	private JComboBox monthBox;

	/** the years spinner */
	private JSpinner yearSpinner;

	/** the table that represents a calendar */
	private JTable calendarTable;

	//time componets

	/** the hours label */
	private JLabel hourLabel;

	/** the minutes labels */
	private JLabel minLabel;

	/** the seconds label */
	private JLabel secLabel;

	/** the hours combo box */
	private JComboBox hourBox;

	/** the minutes combo box */
	private JComboBox minBox;

	/** the seocnds combo box */
	private JComboBox secBox;

	/**
	 * Creates a new instance of <CODE>AbsoluteTimePanel</CODE>
	 * 
	 * @param initialDate
	 *            the date the calendar for which is initially displayed
	 */
	public AbsoluteTimePanel(Date initialDate)
	{
		this.cal = Calendar.getInstance();

		if (initialDate != null)
		{
			this.cal.setTime(initialDate);
		}

		this.initialYear = this.cal.get(Calendar.YEAR);
		
		this.tModel = new DefaultTableModel()
		{
			public boolean isCellEditable(int row, int col)
			{
				return false;
			}
		};
		
		init();
	}

	/**
	 * Displays the current value of <CODE>cal </cCODE> in the claendar table
	 * 
	 * @see #cal
	 * @see #calendarTable
	 */
	private void displayCalInTheTable()
	{
		//use the current cal value to display the days; this.cal doesn't change
		//first determine what the 1st of the month is
		Calendar tempCal = Calendar.getInstance();
		tempCal.setTime(this.cal.getTime());
		//set to the date to the first of the month
		tempCal.set(Calendar.DAY_OF_MONTH, 1);

		Integer[][] data = new Integer[6][7];
		//for what ever reason, sunday is 1 and not 0 and so on
		//the first cell of the 42=6*7 that is going to have a date in it
		int cell = tempCal.get(Calendar.DAY_OF_WEEK) - 1;

		int numberOfDaysInMonth = this.cal.getMaximum(Calendar.DAY_OF_MONTH);
		int row = 0;//week
		int i = 0;

		//row, column
		int[] cellToBeSelected = new int[2];

		for (i = 1; i <= numberOfDaysInMonth; i++)
		{
			if ((cell % 7) == 0 && cell > 0)
			{
				row++;
			}
			
			if (i == this.cal.get(Calendar.DAY_OF_MONTH))
			{
				//always select the current cal's DAY_OF_MONTH
				cellToBeSelected[0] = row;
				cellToBeSelected[1] = cell % 7;
			}

			data[row][cell % 7] = new Integer(i);
			cell++;
		}

		this.tModel.setDataVector(data, DAYS_OF_WEEK);

		this.calendarTable.changeSelection(cellToBeSelected[0], cellToBeSelected[1],
				false, false);
	}

	/**
	 * Sets the cal variable according to new values from yearSpinner and
	 * monthBox
	 * 
	 * @see #cal
	 * @see #yearSpinner
	 * @see #monthBox
	 */
	private void setCalUsingNewMonthYearValues()
	{
		Date savedDate = this.cal.getTime();

		try
		{
			this.cal.set(Calendar.YEAR, ((Integer) this.yearSpinner.getValue())
					.intValue());
			this.cal.set(Calendar.MONTH, this.monthBox.getSelectedIndex());
			this.cal.set(Calendar.DAY_OF_MONTH, 1);

			int date = ((Integer) this.tModel.getValueAt(this.calendarTable
					.getSelectedRow(), this.calendarTable.getSelectedColumn()))
					.intValue();
			if(date <= this.cal.getActualMaximum(Calendar.DAY_OF_MONTH)){
				//keep the date
				this.cal.set(Calendar.DAY_OF_MONTH, date);
			}			

			displayCalInTheTable();
		}
		catch (Exception e)
		{
			this.cal.setTime(savedDate);
		}
	}

	/** Lays out the components */
	protected void addComponents()
	{
		//date panel
		JPanel dateLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		dateLabelPanel.add(this.dateLabel);

		JPanel monthYearPanel = new JPanel(new GridLayout(1, 0));
		monthYearPanel.add(this.monthBox);
		monthYearPanel.add(this.yearSpinner);
		monthYearPanel.add(new JLabel());
		monthYearPanel.add(new JLabel());

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(dateLabelPanel, BorderLayout.NORTH);
		northPanel.add(monthYearPanel, BorderLayout.SOUTH);

		JPanel datePanel = new JPanel(new BorderLayout());
		datePanel.add(northPanel, BorderLayout.NORTH);
		datePanel.add(new JScrollPane(this.calendarTable), BorderLayout.CENTER);

		//time panel
		JPanel hourPanel = new JPanel(new GridLayout(0, 1));
		hourPanel.add(this.hourLabel);
		hourPanel.add(this.hourBox);

		JPanel minPanel = new JPanel(new GridLayout(0, 1));
		minPanel.add(this.minLabel);
		minPanel.add(this.minBox);

		JPanel secPanel = new JPanel(new GridLayout(0, 1));
		secPanel.add(this.secLabel);
		secPanel.add(this.secBox);

		JPanel timePanel = new JPanel(new GridLayout(1, 0));
		timePanel.add(hourPanel);
		timePanel.add(minPanel);
		timePanel.add(secPanel);

		setLayout(new BorderLayout());
		add(datePanel, BorderLayout.NORTH);
		add(timePanel, BorderLayout.SOUTH);
	}

	/** Builds components */
	protected void createComponents()
	{
		//at this point cal contains the current date
		this.dateLabel = new JLabel("Date");

		this.monthBox = new JComboBox(MONTHS);
		this.monthBox.setSelectedIndex(this.cal.get(Calendar.MONTH));
		this.monthBox.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				setCalUsingNewMonthYearValues();
			}
		});

		this.yearSpinner = new JSpinner(new SpinnerNumberModel(this.initialYear,
				YEAR_MIN, YEAR_MAX, 1));
		this.yearSpinner.setEditor(new JSpinner.NumberEditor(this.yearSpinner, "#"));
		this.yearSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setCalUsingNewMonthYearValues();
			}
		});

		this.calendarTable = new JTable(this.tModel);
		this.calendarTable.getColumnModel().setColumnSelectionAllowed(true);
		this.calendarTable.setShowGrid(false);
		this.calendarTable.setCellSelectionEnabled(true);
		this.calendarTable.setPreferredScrollableViewportSize(
				new Dimension(
						calendarTable.getWidth(), calendarTable.getRowHeight() * 6
						)
				);

		displayCalInTheTable();

		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				((DefaultTableCellRenderer) this.calendarTable.getCellRenderer(i, j))
						.setHorizontalAlignment(SwingConstants.CENTER);
			}
		}

		//time components
		this.hourLabel = new JLabel("Hour");
		this.minLabel = new JLabel("Min");
		this.secLabel = new JLabel("Sec");

		this.hourBox = new JComboBox();

		this.minBox = new JComboBox();

		this.secBox = new JComboBox();
		
		int i = 0;

		for (i = 0; i < 24; i++)
		{
			this.hourBox.addItem(new Integer(i));
		}

		for (i = 0; i < 60; i++)
		{
			this.minBox.addItem(new Integer(i));
		}

		for (i = 0; i < 60; i++)
		{
			this.secBox.addItem(new Integer(i));
		}

		this.hourBox.setSelectedIndex(0);
		this.minBox.setSelectedIndex(0);
		this.secBox.setSelectedIndex(0);
	}

	/**
	 * Returns the String representation of the time this <CODE>
	 * AbsoluteTimePanel</CODE> shows (returns "failed" if <CODE>Exception
	 * </CODE> s occur)
	 * 
	 * @return the String representation of the time this <CODE>
	 *         AbsoluteTimePanel</CODE> shows
	 */
	public String getSelectedTime()
	{
		try
		{
			int date = ((Integer) this.tModel.getValueAt(calendarTable
					.getSelectedRow(), calendarTable.getSelectedColumn()))
					.intValue();
			this.cal.set(Calendar.DAY_OF_MONTH, date);

			this.cal.set(Calendar.HOUR_OF_DAY, ((Integer) hourBox.getSelectedItem())
					.intValue());
			this.cal.set(Calendar.MINUTE, ((Integer) minBox.getSelectedItem())
					.intValue());
			this.cal.set(Calendar.SECOND, ((Integer) secBox.getSelectedItem())
					.intValue());
			this.cal.set(Calendar.MILLISECOND, 0);
			
			return AVBaseConstants.MAIN_DATE_FORMAT.format(this.cal.getTime());
		}
		catch (Exception e)
		{
			return "Failed";
		}
	}
}
