package epics.archiveviewer;

/**
 * Encapsulates additional information about an AVEntry; may not always be available
 * @author serge
 */
public final class AVEntryInfo {
	
	/**	the name of the actual PV*/
	private String pvName;
	
	/** the start time of the archiving*/
	private double archivingStartTime;
	
	/** the end time of the archiving*/
	private double archivingEndTime;
	
	/**
	 * Returns the end time of archived data
	 * @return the end time of archived data
	 */
	public double getArchivingEndTime() {
		return this.archivingEndTime;
	}
	
	/**
	 * Returns the start time of archived data
	 * @return the start time of archived data
	 */
	public double getArchivingStartTime() {
		return this.archivingStartTime;
	}
	
	/**
	 * Sets the start and end time of the archiving
	 * @param startTime start time
	 * @param endTime end time
	 */
	public void setArchivingTimes(double startTime, double endTime) {
		this.archivingStartTime = startTime;
		this.archivingEndTime = endTime;
	}
	
	/**
	 * Returns the name of the PV (might be different from the name of the AV entry, see CZAR)
	 * @return the name of the PV
	 */
	public String getPVName() {
		return this.pvName;
	}
	
	/**
	 * Sets the name of the PV
	 * @param p the name of the PV
	 */
	public void setPVName(String p) {
		this.pvName = p;
	}
}
