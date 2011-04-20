package epics.archiveviewer.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class parses <CODE>String</CODE> s that represent absolute and
 * relative start and end times of a <CODE>TimeAxis</CODE>. The following
 * table explains the semantics of absolute and relative times by showing what
 * the base times (for "start" and "end" times respectively) in each cases are.
 * Please, note that a positive, relative start time never makes sense. <br>
 * <table border="1">
 * <tr><td/>
 * <td><B>Absolute start </B></td>
 * <td><B>Relative Neg start </B></td>
 * </tr>
 * <tr>
 * <td><B>Absolute end </B></td>
 * <td>-/-</td>
 * <td>"End time"/-</td>
 * </tr>
 * <tr>
 * <td><B>Relative Pos end </B></td>
 * <td>-/"Start time"</td>
 * <td>"now"/"Start time"</td>
 * </tr>
 * <tr>
 * <td><B>Relative Neg end </B></td>
 * <td>-/"now"</td>
 * <td>"now"/"now"</td>
 * </tr>
 * </table> Don't forget that relative time <CODE>String</CODE> are allowed to
 * contain absolute time (but not date) parts, e.g. both "-1d 8:00" and "+1M
 * 08:00" are valid entries <br>
 * The allowed formats for absolute time <CODE>String</CODE> s are:
 * <p>
 * MM/dd/yyyy HH:mm:ss.SSS <br>
 * MM/dd/yyyy HH:mm:ss <br>
 * MM/dd/yyyy HH:mm.SSS <br>
 * MM/dd/yyyy HH.SSS <br>
 * MM/dd/yyyy.SSS <br>
 * MM/dd/yyyy <br>
 * </p>
 * 
 * @author Sergei chevtsov
 */
public class TimeParser
{
	/** The accepted date formats for absolute times */
	private static final DateFormat[] dateFormats;

	static
	{
		//this order is chosen because DateFormat.parse(String time) might not
		// use the whole string
		dateFormats = new SimpleDateFormat[6];
		dateFormats[0] = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		dateFormats[1] = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		dateFormats[2] = new SimpleDateFormat("MM/dd/yyyy HH:mm.SSS");
		dateFormats[3] = new SimpleDateFormat("MM/dd/yyyy HH.SSS");
		dateFormats[4] = new SimpleDateFormat("MM/dd/yyyy.SSS");
		dateFormats[5] = new SimpleDateFormat("MM/dd/yyyy");
	}

	/** A constant for the relative time token "year" */
	private static final char YEAR_TOKEN = 'y';

	/** A constant for the relative time token "month" */
	private static final char MONTH_TOKEN = 'M';

	/** A constant for the relative time token "day" */
	private static final char DAY_TOKEN = 'd';

	/** A constant for the relative time token "hour" */
	private static final char HOUR_TOKEN = 'H';

	/** A constant for the relative time token "minute" */
	private static final char MINUTE_TOKEN = 'm';

	/** A constant for the relative time token "second" */
	private static final char SECOND_TOKEN = 's';

	/**
	 * an array of relative time tokens in DESCENDING order in respect to their
	 * duration; such order allows to easily determin if the relative string is
	 * positive or negative
	 */
	private static final char[] RELATIVE_TIME_TOKENS = new char[]
	{
			YEAR_TOKEN, MONTH_TOKEN, DAY_TOKEN, HOUR_TOKEN, MINUTE_TOKEN,
			SECOND_TOKEN
	};

	/**
	 * a constant indicating that a <CODE>String</CODE> is an absolute time
	 * string
	 */
	private static final int ABSOLUTE_TYPE = 0;

	/**
	 * a constant indicating that a <CODE>String</CODE> is a positive,
	 * relative time string
	 */
	private static final int RELATIVE_POSITIVE_TYPE = 1;

	/**
	 * a constant indicating that a <CODE>String</CODE> is a negative,
	 * relative time string
	 */
	private static final int RELATIVE_NEGATIVE_TYPE = 2;

	/**
	 * Returns the numberic value of the specified token in the specified time
	 * string. If there is no such token, returns 0
	 * 
	 * @param time
	 *            the time string to process (doesn't change)
	 * @param token
	 *            the desired token which value is to be determined
	 * @throws NumberFormatException
	 *             if the token has a non-numeric value
	 * @return the numberic value of the specified token in the specified time
	 *         string.
	 */
	private static int getValueOfToken(final String time, char token)
			throws NumberFormatException
	{
		int index = time.indexOf(token);

		if (index >= 0)
		{
			String number = "";
			index--;

			while ((index >= 0) && Character.isDigit(time.charAt(index)))
			{
				number = time.substring(index, index + 1) + number;
				index--;
			}

			int result = Integer.parseInt(number);

			if ((index >= 0) && time.substring(index, index + 1).equals("-"))
			{
				result = -1 * result;
			}

			return result;
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Returns the type of the specified time string. To determine the type, it
	 * is only looked at which of the relative time tokens are present in the
	 * string
	 * 
	 * @param time
	 *            the time string (doesn't change)
	 * @return the type of the specified time string
	 */
	private static int getTimeType(final String time)
	{
		for (int i = 0; i < RELATIVE_TIME_TOKENS.length; i++)
		{
			int type = getValueOfToken(time, RELATIVE_TIME_TOKENS[i]);

			if (type > 0)
			{
				return RELATIVE_POSITIVE_TYPE;
			}
			else if (type < 0)
			{
				return RELATIVE_NEGATIVE_TYPE;
			}
		}

		return ABSOLUTE_TYPE;
	}

	/**
	 * First, determines the values of each of the relative time tokens inside
	 * the specified time string. Then uses the base time of the specified
	 * calendar to calculate the corresponding Date. Next, but only if the time
	 * string contains an absolute part, adjusts this Date accordingly Finally,
	 * returns the calculated date.
	 * 
	 * @param _time
	 *            the time string to be parsed parse as a relative one (doesn't
	 *            change)
	 * @param baseCal
	 *            this <CODE>Calendar</CODE> contains the base time (see
	 *            {@link TimeParser above}) CAUTION!!! It is changed in the
	 *            process!
	 * @return the <CODE>Date</CODE> corresponding to the specified relative
	 *         time string
	 * @throws ParseException
	 *             if, among others, the specified string contains no relative
	 *             time tokens
	 */
	private static Date parseRelative(final String _time, Calendar baseCal)
			throws ParseException
	{
		int[] relTimeField = new int[6];
		int lastIndexOfRelPart = -1;

		for (int i = 0; i < RELATIVE_TIME_TOKENS.length; i++)
		{
			relTimeField[i] = getValueOfToken(_time, RELATIVE_TIME_TOKENS[i]);

			if (relTimeField[i] != 0)
			{
				lastIndexOfRelPart = _time.indexOf(RELATIVE_TIME_TOKENS[i]);
			}
		}

		if (lastIndexOfRelPart == -1)
		{
			throw new ParseException(_time
					+ " contains no relative time tokens", 0);
		}

		baseCal
				.set(Calendar.YEAR, baseCal.get(Calendar.YEAR)
						+ relTimeField[0]);
		baseCal.set(Calendar.MONTH, baseCal.get(Calendar.MONTH)
				+ relTimeField[1]);
		baseCal.set(Calendar.DAY_OF_YEAR, baseCal.get(Calendar.DAY_OF_YEAR)
				+ relTimeField[2]);
		baseCal.set(Calendar.HOUR_OF_DAY, baseCal.get(Calendar.HOUR_OF_DAY)
				+ relTimeField[3]);
		baseCal.set(Calendar.MINUTE, baseCal.get(Calendar.MINUTE)
				+ relTimeField[4]);
		baseCal.set(Calendar.SECOND, baseCal.get(Calendar.SECOND)
				+ relTimeField[5]);

		String absPart = _time.substring(lastIndexOfRelPart + 1).trim();

		if (absPart.equals("") == false)
		{
			return parseAbsolute(baseCal, _time, lastIndexOfRelPart + 1);
		}
		else
		{
			return baseCal.getTime();
		}
	}

	/**
	 * Returns the <CODE>Date</CODE> of the specified calendar (if cal is
	 * NULL, the current time calendar is used) after its values are adjusted to
	 * the date and time values of the absolute part of the specified string
	 * that starts with specified index. E.g. the calendar time is "01/01/2004
	 * 07:00:20", _time is "8:00", startIndex is 0, then the date "01/01/2004
	 * 08:00:00" is returned
	 * 
	 * @param cal
	 *            the calendar that needs adjustion, or NULL
	 * @param _time
	 *            the time string
	 * @param startIndex
	 *            the start index of the absolute part of the specified time
	 *            string
	 * @return the <CODE>Date</CODE> of the specified calendar (if cal is
	 *         NULL, the current time calendar is used) after its values are
	 *         adjusted to the date and time values of the absolute part of the
	 *         specified string that starts with specified index
	 * @throws ParseException
	 *             if the specified part of the time string can not be parsed
	 *             correctly
	 */
	private static Date parseAbsolute(Calendar cal, final String _time,
			int startIndex) throws ParseException
	{
		if (cal == null)
		{
			cal = Calendar.getInstance();
		}

		String time = _time.substring(startIndex).trim();

		if (time.equals("now") == true)
		{
			return cal.getTime();
		}

		//prepare the string
		//when no year was entered, add the current year
		//when no date (i.e. both month and day of month) weren't mentioned
		// either, add those
		//when no msec were entered, add 000
		//search for the 1st "/"
		int index1 = time.indexOf("/");

		if (index1 == -1)
		{
			//neither date, not year were entered
			time = dateFormats[5].format(cal.getTime()) + " " + time;
		}
		else
		{
			//search for the 2nd "/"
			int index2 = time.indexOf("/", index1 + 1);

			if (index2 == -1)
			{
				//the date was entered but not the year
				//example 02/02 8:00, index1 == 2
				//substring(incl, excl)
				time = time.substring(0, index1 + 3) + "/"
						+ cal.get(Calendar.YEAR) + time.substring(index1 + 3);
			}
		}

		//msec
		index1 = time.indexOf(".");

		if (index1 == -1)
		{
			time = time + ".000";
		}

		//now parse the time string using one of the date formats
		Date d = new Date();

		for (int i = 0; i < dateFormats.length; i++)
		{
			try
			{
				d = dateFormats[i].parse(time);

				//if no exception occured
				return d;
			}
			catch (Exception e)
			{
			}
		}

		//none of the dateformats fit
		throw new ParseException("Wrong absolute time format", 0);
	}

	/**
	 * Returns a six-element int array of values for each relative time token
	 * (the order: year, month, day, hour, min, second) (=> Needed to initialize
	 * the RelativeTimePanel)
	 * 
	 * @param _time
	 *            the relative time string
	 * @return a six-element int array of values for each relative time token
	 *         (the order: year, month, day, hour, min, second)
	 */
	public static int[] getRelativeFields(final String _time)
	{
		int[] relTimeField = new int[6];

		for (int i = 0; i < RELATIVE_TIME_TOKENS.length; i++)
		{
			relTimeField[i] = getValueOfToken(_time, RELATIVE_TIME_TOKENS[i]);
		}

		return relTimeField;
	}

	/**
	 * Returns a two-elements- <CODE>Date</CODE> -array. The first element is
	 * the <CODE>Date</CODE> that corresponds to the specified startTime, the
	 * second element is the <CODE>Date</CODE> that corresponds to the
	 * spcified endTime (both can be NULL)
	 * 
	 * @param startTime
	 *            the start time string to parse
	 * @param endTime
	 *            the end time string to parse
	 * @return a two-elements- <CODE>Date</CODE> -array. The first element is
	 *         the <CODE>Date</CODE> that corresponds to the specified
	 *         startTime, the second element is the <CODE>Date</CODE> that
	 *         corresponds to the spcified endTime (both can be NULL)
	 * @see TimeParser
	 * @throws NumberFormatException
	 *             if one of the relative time tokens has a non-numeric value
	 * @throws ParseException
	 *             if a string can't be parsed
	 */
	public static Date[] parse(String startTime, String endTime)
			throws NumberFormatException, ParseException
	{
		//get the types
		int startTimeType = getTimeType(startTime);

		if (startTimeType == RELATIVE_POSITIVE_TYPE)
		{
			throw new ParseException("the specified start time " + startTime
					+ " is relative, positive", 0);
		}

		int endTimeType = -1;

		if (endTime.equals("now") == true)
		{
			endTimeType = ABSOLUTE_TYPE;
		}
		else
		{
			endTimeType = getTimeType(endTime);
		}

		Date[] result = new Date[2];

		//now handle the 6 cases as they are shown in the table above
		if ((startTimeType == ABSOLUTE_TYPE) && (endTimeType == ABSOLUTE_TYPE))
		{
			result[0] = parseAbsolute(null, startTime, 0);
			result[1] = parseAbsolute(null, endTime, 0);
		}
		else if ((startTimeType == ABSOLUTE_TYPE)
				&& (endTimeType == RELATIVE_NEGATIVE_TYPE))
		{
			result[0] = parseAbsolute(null, startTime, 0);

			Calendar cal = Calendar.getInstance();
			result[1] = parseRelative(endTime, cal);
		}
		else if ((startTimeType == ABSOLUTE_TYPE)
				&& (endTimeType == RELATIVE_POSITIVE_TYPE))
		{
			result[0] = parseAbsolute(null, startTime, 0);

			Calendar cal = Calendar.getInstance();
			cal.setTime(result[0]);
			result[1] = parseRelative(endTime, cal);
		}
		else if ((startTimeType == RELATIVE_NEGATIVE_TYPE)
				&& (endTimeType == ABSOLUTE_TYPE))
		{
			result[1] = parseAbsolute(null, endTime, 0);

			Calendar cal = Calendar.getInstance();
			cal.setTime(result[1]);
			result[0] = parseRelative(startTime, cal);
		}
		else if ((startTimeType == RELATIVE_NEGATIVE_TYPE)
				&& (endTimeType == RELATIVE_NEGATIVE_TYPE))
		{
			Calendar cal = Calendar.getInstance();
			result[0] = parseRelative(startTime, cal);
			cal = Calendar.getInstance();
			result[1] = parseRelative(endTime, cal);
		}
		else if ((startTimeType == RELATIVE_NEGATIVE_TYPE)
				&& (endTimeType == RELATIVE_POSITIVE_TYPE))
		{
			Calendar cal = Calendar.getInstance();
			result[0] = parseRelative(startTime, cal);
			cal.setTime(result[0]);
			result[1] = parseRelative(endTime, cal);
		}

		return result;
	}
}