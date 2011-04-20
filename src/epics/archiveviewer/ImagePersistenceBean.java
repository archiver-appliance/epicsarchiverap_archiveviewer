
package epics.archiveviewer;

import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Encapsulates parameters needed by plot plugins to directly create a file image of the plot
 * @author serge
 */
public final class ImagePersistenceBean {
	
	/**
	 * an image of the desired size
	 */
	private BufferedImage bufferedImage;
	/**
	 * the image file format
	 */
	private ImageFileFormat imageFormat;
	/**
	 * the file the image will be saved as 
	 */
	private File file;
	
	/** An empty constructor */
	public ImagePersistenceBean()
	{
		//empty constructor
	}
	
	/**
	 * A constructor that sets all parameters
	 * @param bI the BufferedImage that is going to be drawn onto
	 * @param iF the image file format
	 * @param f the file the image will be saved as 
	 */
	public ImagePersistenceBean(BufferedImage bI, ImageFileFormat iF, File f)
	{
		this.bufferedImage = bI;
		this.imageFormat = iF;
		this.file = f;
	}

	/**
	 * Returns the BufferedImage the plot is drawn onto
	 * @return the BufferedImage the plot is drawn onto
	 */
	public BufferedImage getBufferedImage() {
		return this.bufferedImage;
	}
	
	/**
	 * Sets the BufferedImage the plot is drawn onto
	 * @param bI new BufferedImage
	 */
	public void setBufferedImage(BufferedImage bI) {
		this.bufferedImage = bI;
	}
	
	/**
	 * Returns the file which the image will be saved as 
	 * @return the file which the image will be saved as 
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * Sets the file which the image will be saved as
	 * @param f the new file
	 */
	public void setFile(File f) {
		this.file = f;
	}
	
	/**
	 * Returns the image file format
	 * @return the image file format
	 */
	public ImageFileFormat getImageFormat() {
		return this.imageFormat;
	}
	
	/**
	 * Sets the image file format
	 * @param iF the new image file format
	 */
	public void setImageFormat(ImageFileFormat iF) {
		this.imageFormat = iF;
	}
}
