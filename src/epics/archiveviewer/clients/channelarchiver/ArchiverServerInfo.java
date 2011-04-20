package epics.archiveviewer.clients.channelarchiver;

import java.util.HashMap;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import epics.archiveviewer.AVBaseFacade;

/**
 * Encapsulates relevant information about the archiver server
 * 
 * @author Craig McChesney, Sergei Chevtsov
 */
public class ArchiverServerInfo {

	// Instance Variables ======================================================

	/** the version of the archiver server */
	private final int version;

	/** the description of the archiver server */
	private final String description;

	/** the states which the archiver server can indicate */
	private final String[] states;

	/**
	 * maps the available severity key numbers to the <CODE>SeverityInfo
	 * </CODE> object
	 */
	private final HashMap severities;


	// Constructors ============================================================

	/**
	 * Creates a new <CODE>ArchiveServerInfo</CODE> object
	 * 
	 * @param ver
	 *            see {@link #version version}
	 * @param aDescription
	 *            see {@link #description description}
	 * @param methods
	 *            see {@link #methods methods}
	 * @param states
	 *            see {@link #states states}
	 * @param severities
	 *            see {@link #severities severities}
	 */
	protected ArchiverServerInfo(int ver, String aDescription,
			String[] states, HashMap severities) {
		version = ver;
		description = aDescription;
		this.states = states;
		this.severities = severities;
	}

	// Accessing ===============================================================

	/**
	 * Returns the version of this <CODE>ArchiveServerInfo</CODE>
	 * 
	 * @return the version of this <CODE>ArchiveServerInfo</CODE>
	 * @see #version
	 */
	protected int getVersion() {
		return version;
	}

	/**
	 * Returns the description of this <CODE>ArchiveServerInfo</CODE>
	 * 
	 * @return the description of this <CODE>ArchiveServerInfo</CODE>
	 * @see #description
	 */
	protected String getDescription() {
		return description;
	}

	/**
	 * Returns the states of this <CODE>ArchiveServerInfo</CODE>
	 * 
	 * @return the states of this <CODE>ArchiveServerInfo</CODE>
	 * @see #states
	 */
	protected String[] getStates() {
		return states;
	}

	/**
	 * Returns the severities of this <CODE>ArchiveServerInfo</CODE>
	 * 
	 * @return the severities of this <CODE>ArchiveServerInfo</CODE>
	 * @see #severities
	 */
	protected HashMap getSeverities() {
		return severities;
	}

	/**
	 * Returns the <CODE>SeverityInfo</CODE> object identified by the
	 * specified number. If there is no severity with the specified key, null is
	 * returned
	 * 
	 * @param number
	 *            the key of the severity
	 * @return the <CODE>SeverityInfo</CODE> object identified by the
	 *         specified number
	 */
	protected SeverityInfo getSeverity(int number) {
		return (SeverityInfo) severities.get(new Integer(number));
	}
}