package epics.archiveviewer.xal.view.timeinput;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;


/**
 * This class is the relative time panel, where the user can select relative
 * time bounds for the time axes
 * 
 * @author Sergei Chevtsov
 */
public class RelativeTimePanel extends AVAbstractPanel
{
	/**
	 * a flag indicating for which bound (start or end ) of the time axis this
	 * <CODE>RelativeTimePanel</CODE> is
	 */
	private final boolean isForStartTime;

	/**
	 * an array of initial values for year, month, day, hours, minutes, and
	 * seconds
	 */
	private final int[] initialValues;

	/** the years combo box */
	private JComboBox yearBox;

	/** the months combo box */
	private JComboBox monthBox;

	/** the days combo box */
	private JComboBox dayBox;

	/** the hours combo box */
	private JComboBox hourBox;

	/** the minutes combo box */
	private JComboBox minBox;

	/** the seconds combo box */
	private JComboBox secBox;

	/** the year label */
	private JLabel yearLabel;

	/** the month label */
	private JLabel monthLabel;

	/** the day label */
	private JLabel dayLabel;

	/** the hour label */
	private JLabel hourLabel;

	/** the minute label */
	private JLabel minLabel;

	/** the seconds label */
	private JLabel secLabel;

	/** the label for the abolute part of the relative time <CODE>String</CODE> */
	private JLabel absolutePartLabel;

	private JTextField absolutePartField;

	public RelativeTimePanel(int[] initVals, boolean forStartTime)
	{
		if (	initVals == null || 
				initVals.length != 6
		)	
		{
			this.initialValues = new int[6];

			for (int i = 0; i < 6; i++)
			{
				this.initialValues[i] = 0;
			}
		}
		else
			this.initialValues = initVals;

		this.isForStartTime = forStartTime;

		init();
	}

	/** Lays out the components */
	protected void addComponents()
	{
		JPanel relativePartPanel = new JPanel();
		relativePartPanel.setLayout(new GridLayout(0, 3));
		relativePartPanel.add(this.yearLabel);
		relativePartPanel.add(this.monthLabel);
		relativePartPanel.add(this.dayLabel);
		relativePartPanel.add(this.yearBox);
		relativePartPanel.add(this.monthBox);
		relativePartPanel.add(this.dayBox);

		relativePartPanel.add(this.hourLabel);
		relativePartPanel.add(this.minLabel);
		relativePartPanel.add(this.secLabel);
		relativePartPanel.add(this.hourBox);
		relativePartPanel.add(this.minBox);
		relativePartPanel.add(this.secBox);

		JPanel absolutePartPanel = new JPanel(new BorderLayout());
		absolutePartPanel.add(this.absolutePartLabel, BorderLayout.NORTH);
		absolutePartPanel.add(this.absolutePartField, BorderLayout.SOUTH);

		JPanel p = new JPanel(new BorderLayout(0, 10));
		p.add(relativePartPanel, BorderLayout.NORTH);
		p.add(absolutePartPanel, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
	}

	/** Builds the components */
	protected void createComponents()
	{
		this.yearLabel = new JLabel("Year");
		this.monthLabel = new JLabel("Month");
		this.dayLabel = new JLabel("Day");
		this.hourLabel = new JLabel("Hour");
		this.minLabel = new JLabel("Min");
		this.secLabel = new JLabel("Sec");

		this.absolutePartLabel = new JLabel("Absolute part");
		this.absolutePartField = new JTextField(10);

		this.yearBox = new JComboBox();
		this.monthBox = new JComboBox();
		this.dayBox = new JComboBox();
		this.hourBox = new JComboBox();
		this.minBox = new JComboBox();
		this.secBox = new JComboBox();

		int i = 0;

		for (i = (this.isForStartTime ? 0 : 14); i >= -14; i--)
		{
			String item = new String();

			if (i > 0)
			{
				item = "+";
			}

			this.yearBox.addItem(item + i);
		}

		for (i = (this.isForStartTime ? 0 : 11); i >= -11; i--)
		{
			String item = new String();

			if (i > 0)
			{
				item = "+";
			}

			this.monthBox.addItem(item + i);
		}

		for (i = (this.isForStartTime ? 0 : 30); i >= -30; i--)
		{
			String item = new String();

			if (i > 0)
			{
				item = "+";
			}

			this.dayBox.addItem(item + i);
		}

		for (i = (this.isForStartTime ? 0 : 23); i >= -23; i--)
		{
			String item = new String();

			if (i > 0)
			{
				item = "+";
			}

			this.hourBox.addItem(item + i);
		}

		for (i = (this.isForStartTime ? 0 : 59); i >= -59; i--)
		{
			String item = new String();

			if (i > 0)
			{
				item = "+";
			}

			this.minBox.addItem(item + i);
		}

		for (i = (this.isForStartTime ? 0 : 59); i >= -59; i--)
		{
			String item = new String();

			if (i > 0)
			{
				item = "+";
			}

			this.secBox.addItem(item + i);
		}

		String item = new String();

		item = ((this.initialValues[0] > 0) ? "+" : "") + this.initialValues[0];
		this.yearBox.setSelectedItem(item);

		item = ((this.initialValues[1] > 0) ? "+" : "") + this.initialValues[1];
		this.monthBox.setSelectedItem(item);

		item = ((this.initialValues[2] > 0) ? "+" : "") + this.initialValues[2];
		this.dayBox.setSelectedItem(item);

		item = ((this.initialValues[3] > 0) ? "+" : "") + this.initialValues[3];
		this.hourBox.setSelectedItem(item);

		item = ((this.initialValues[4] > 0) ? "+" : "") + this.initialValues[4];
		this.minBox.setSelectedItem(item);

		item = ((this.initialValues[5] > 0) ? "+" : "") + this.initialValues[5];
		this.secBox.setSelectedItem(item);
	}

	/**
	 * Returns a <CODE>String</CODE> containing the selected relative time
	 * 
	 * @return a <CODE>String</CODE> containing the selected relative time
	 */
	public String getSelectedTime()
	{
		String time = "";
		String item = new String();

		item = this.yearBox.getSelectedItem().toString();

		if (item.equals("0") == false)
		{
			time += (item + "y ");
		}

		item = this.monthBox.getSelectedItem().toString();

		if (item.equals("0") == false)
		{
			time += (item + "M ");
		}

		item = this.dayBox.getSelectedItem().toString();

		if (item.equals("0") == false)
		{
			time += (item + "d ");
		}

		item = this.hourBox.getSelectedItem().toString();

		if (item.equals("0") == false)
		{
			time += (item + "H ");
		}

		item = this.minBox.getSelectedItem().toString();

		if (item.equals("0") == false)
		{
			time += (item + "m ");
		}

		item = this.secBox.getSelectedItem().toString();

		if (item.equals("0") == false)
		{
			time += (item + "s ");
		}

		time += this.absolutePartField.getText();

		if ((time == null) || time.equals(""))
		{
			time = "now";
		}

		return time;
	}
}
