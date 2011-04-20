package epics.archiveviewer;

/**
 * Class used for referencing different image file formats.
 * @author Richard Atkinson
 */
public class ImageFileFormat {

    /** Portable Network Graphics - lossless */
    public static ImageFileFormat PNG = new ImageFileFormat("png");

    /** Joint Photographic Experts Group format - lossy */
    public static ImageFileFormat JPEG = new ImageFileFormat("jpeg");
    
    /** an ID */
    private final Object ID;
    
	/**
	 * Constructor; takes an id object
	 * @param id id of this ImageFileFormat
	 */
    protected ImageFileFormat(Object id)
    {
    	this.ID = id;
    }
}
