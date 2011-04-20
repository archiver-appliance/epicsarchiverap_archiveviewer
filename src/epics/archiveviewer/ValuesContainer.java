package epics.archiveviewer;

import java.util.Vector;

/**
 * Encapsulates information on data for each AV entry; each data sample is considered a Vector per default
 * @author serge
 */
public interface ValuesContainer
{
	/**
	 * Returns the AV entry to which this values container belongs
	 * @return the AV entry
	 */
	public AVEntry getAVEntry();

	/**
	 * Returns the number of data samples in this values container (might be different from requested nr of values);
	 * note: each data sample might be a vector with more than 1 values
	 * @return the number of data samples
	 * @throws Exception
	 */
	public int getNumberOfValues() throws Exception;

	/**
	 * Returns the timestamp of the data sample at the specified index
	 * @param index the index of the data sample
	 * @return the timestamp of the data sample at the specified index
	 * @throws Exception
	 */
	public double getTimestampInMsec(int index) throws Exception;

	/**
	 * Returns the units of the retrieved data
	 * @return the units of the retrieved data
	 * @throws Exception
	 */
	public String getUnits() throws Exception;

	/**
	 * Returns the length of the data sample vector (1 if non-waveform, >=2 if waveform)
	 * @return the length of the data sample vector (1 if non-waveform, >=2 if waveform)
	 * @throws Exception
	 */
	public int getDimension() throws Exception;

	/**
	 * Returns the display label for the data sample at the specified index (could be used 
	 * inside plot plugin tooltips)
	 * @param index the index of the data sample
	 * @return the display label for the data sample at the specified index 
	 * @throws Exception
	 */
	public String getDisplayLabel(int index) throws Exception;
	
	/**
	 * Returns string representation of the item inside the data sample at the specified index 
	 * @param index the index of the data sample 
	 * @param item the index of the item inside the data sample
	 * @return string representation of the item inside the data sample (which is a vector per default)
	 * at the specified index returns
	 * @throws Exception
	 */
	public String valueToString(int index, int item) throws Exception;	

	/**
	 * Returns the status/severity of the data sample at the specified index
	 * @param index the index of the data sample
	 * @return the status/severity of the data sample at the specified index
	 */
	public String getStatus(int index);
	
	/**
	 * Returns true if the data samples are discrete, ie have a meaningful string representation (states etc.);
	 * false if data is purely numerical
	 * @return true if the data samples are discrete, ie have a meaningful string representation (states etc.);
	 * false if data is purely numerical
	 */
	public boolean isDiscrete();

	/**
	 * Returns true if the data sample at the specified index is valid; false otherwise
	 * @param index index of the data sample
	 * @return true if the data sample at the specified index is valid; false otherwise
	 * @throws Exception
	 */
	public boolean isValid(int index) throws Exception;

	/**
	 * Returns the data sample at the specified index
	 * @param index the index of the data sample
	 * @return the data sample at the specified index
	 * @throws Exception
	 */
	public Vector getValue(int index) throws Exception;		
	
	/**
	 * Returns true if data samples are waveforms; false otherwise
	 * (convenience method for getDimension() > 1)
	 * @return true if data samples are waveforms; false otherwise
	 */
	public boolean isWaveform();

	/**
	 * Returns Java type of actual values
	 * @return Java type of actual values
	 */
	public Class getDataType();
	
	/**
	 * Returns precision for numeric data
	 * @return precision for numeric data
	 */
	public int getPrecision();

	/** 
	 * Clears this values container
	 */
	public void clear();
	
	/**
	 * Returns the smallest valid value, or NaN if there is none
	 * @return the smallest valid value, or NaN if there is none
	 */
	public double getMinValidValue();
	
	/**
	 * Returns the smallest positive valid value, or NaN if there is none
	 * @return the smallest positive valid value, or NaN if there is none
	 */
	public double getMinPosValidValue();
	
	/**
	 * Returns the greatest valid value, or NaN if there is none
	 * @return the greatest valid value, or NaN if there is none
	 */
	public double getMaxValidValue();
	
	/**
	 * Returns the string representation of the range of data in this values container; min, max are
	 * separated by specified string 
	 * @param separator separator between min and mex
	 * @return the string representation of the range of data in this values container
	 */
	public String getRangeLabel(String separator);
}
