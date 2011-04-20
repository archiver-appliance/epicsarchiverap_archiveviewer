package epics.archiveviewer;

/**
 * Interface for feedback to the user; when progress value >= 100, task stops
 * @author serge
 */
public interface ProgressTask 
{
	/** 0 since progress is measured in percent */
	public static final int MIN = 0;
	/** 100 since progress is measured in percent*/
	public static final int MAX = 100;
	
    /**
     * Sets new progress value (must be in percent, ie more than 0; if greater than or equal 100,
     * the task stops) and a short description of the current status
     * @param v the progress value in percent (more than 0)
     * @param s a short description of the current status
     */
    public void setProgressParameters(int v, String s);
    
    /**
     * Interrupts the actual implementation
     */
    public void interrupt();
	
    /**
     * Returns true if task was interrupted; false, otherwise
     * @return true if task was interrupted; false, otherwise
     */
	public boolean interrupted();
}
