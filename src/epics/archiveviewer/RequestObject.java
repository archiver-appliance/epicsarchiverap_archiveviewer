package epics.archiveviewer;


/**
 * Encapsulates parameters of a request for data from the server
 * @author Sergei Chevtsov
 * @version 14-Sep-2009 Bob Hall.  Added includeSparcified.
 */
public class RequestObject extends Object
{
	/** the retrieval method*/
    private RetrievalMethod method;
    /** nr of values*/
    private int requestedNrOfValues;
    /** start time*/
    private double startTimeInMsecs;
    /** end time*/
    private double endTimeInMsecs;

    private String sparsificationOperator;
    
    /**
     * The spreadsheet export requires some special handling in the archiver appliance. 
     * We need to tell the appliance client code that this is indeed a spreadsheet export.
     */
    private String exporterID = null;
    
    /**
     * Default constructor
     *
     */
    public RequestObject()
    {
        
    }
    
    /**
     * Constructor; initializes all parameters
     * @param startTime start time of the request in msecs
     * @param endTime end time of the request in msecs
     * @param rm the retrieval method
     * @param nrOfValues request number of values
     */
    public RequestObject(double startTime, double endTime, RetrievalMethod rm, int nrOfValues,
        String sparsificationOperator)
    {
        this.startTimeInMsecs = startTime;
        this.endTimeInMsecs = endTime;
        this.method = rm;
        this.requestedNrOfValues = nrOfValues;
        this.sparsificationOperator = sparsificationOperator;
    }
	/**
	 * Sets the retrieval method
	 * @param method retrieval method
	 */
    public void setMethod(RetrievalMethod method)
    {
        this.method = method;
    }
    /**
     * Sets nr of values for this request
     * @param requestedNrOfValues The requestedNrOfValues to set.
     */
    public void setRequestedNrOfValues(int requestedNrOfValues)
    {
        this.requestedNrOfValues = requestedNrOfValues;
    }
    
	/**
	 * Sets the request range
	 * @param startInMsecs start time in msecs
	 * @param endInMsecs end time in msecs
	 */
    public void setRange(double startInMsecs, double endInMsecs)
    {
        this.startTimeInMsecs = startInMsecs;
        this.endTimeInMsecs = endInMsecs;
    }

	 /**
	  * Returns end time of this request
	  * @return end time of this request
	  */
    public double getEndTimeInMsecs()
    {
        return this.endTimeInMsecs;
    }
    
    /**
     * Returns the method of this request
     * @return the method.
     */
    public RetrievalMethod getMethod()
    {
        return method;
    }
    
    /**
     * Returns number of values of this request
     * @return the requested number of values
     */
    public int getRequestedNrOfValues()
    {
        return requestedNrOfValues;
    }
    /**
     * Returns the start time of this request
     * @return the start time of this request
     */
    public double getStartTimeInMsecs()
    {
        return startTimeInMsecs;
    }

    /***
     * Returns true if o is a RequestObject and has the same parameters as this RequestObject;
     * false otherwise
     * @return true if o is a RequestObject and has the same parameters as this RequestObject;
     * false otherwise
     */
    public boolean equals(Object o)
    {
        try
        {
            if(o instanceof RequestObject)
	        {
	            RequestObject other = (RequestObject) o;
	            return
	            	(	
	        	        (this.method == null && other.getMethod().equals(null)) ||
	        	        (this.method.equals(other.getMethod()))
	            	)	
	            	&&
	            	this.requestedNrOfValues == other.getRequestedNrOfValues() &&
	            	this.startTimeInMsecs == other.getStartTimeInMsecs() &&
	            	this.endTimeInMsecs == other.getEndTimeInMsecs();
	        }
        }
        catch(Exception e)
        {
            //do nothing
        }
        return false;
    }
    
    /**
     * Returns the hash code
     * @return hash code
     */
    public int hashCode()
    {
    	int hashCode = (int) (this.endTimeInMsecs + this.startTimeInMsecs + this.requestedNrOfValues);
    	if(this.method != null)
    		hashCode += method.getKey().hashCode();
    	return hashCode;    	
    }

	public String getExporterID() {
		return exporterID;
	}

	public void setExporterID(String exporterID) {
		this.exporterID = exporterID;
	}

	/**
	 * @return the sparsificationOperator
	 */
	public String getSparsificationOperator() {
		return sparsificationOperator;
	}

	/**
	 * @param sparsificationOperator the sparsificationOperator to set
	 */
	public void setSparsificationOperator(String sparsificationOperator) {
		this.sparsificationOperator = sparsificationOperator;
	}
}
