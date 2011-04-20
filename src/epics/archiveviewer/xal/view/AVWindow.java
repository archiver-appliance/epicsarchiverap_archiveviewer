/*
 * Created on Nov 16, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import epics.archiveviewer.base.AVBase;
import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.util.AVBaseUtilities;
import epics.archiveviewer.xal.AVXALConstants;
import epics.archiveviewer.xal.controller.AVController;
import gov.sns.application.ImageCaptureManager;
import gov.sns.application.XalWindow;

import java.awt.Component;
import java.io.File;
import java.sql.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author serge
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AVWindow extends XalWindow
{
	private JFileChooser snapshotFileChooser;
	
	private void displayFileChooser(AVBase avBase, Component componentToCapture)
	{
		int status = this.snapshotFileChooser
				.showSaveDialog(componentToCapture);
		//no need to check if the file exists, since the time is pretty much unique
		if (status == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = this.snapshotFileChooser.getSelectedFile();
			if ( selectedFile.exists() ) 
			{
				int confirm = ImageCaptureManager.displayConfirmDialog(
						this, 
						"Overwrite Confirmation", 
						"The selected file:  " + selectedFile.getName() + " already exists! \n Overwrite selection?");
				if ( confirm == NO_OPTION ) 
				{
					// offer a new selection
					displayFileChooser(avBase, componentToCapture);
					return;
				}
			}
			try
			{
				AVBaseUtilities.saveSnapshotAsPNG(
						componentToCapture,
						selectedFile);
			}
			catch (Exception ex)
			{
				avBase.displayError("Couldn't save the snapshot", ex);
			}
		}
	}

	/** Creates a new instance of WindowAdaptor */
	public AVWindow(AVController aDocument, MainAVPanel mainAVPanel)
	{
		super(aDocument);
		getContentPane().add(mainAVPanel);
		pack();
		setSize(AVXALConstants.DEFAULT_WINDOW_SIZE);
                setLocation(0, 20);
	}

	public boolean usesToolbar()
	{
		return false;
	}
	
	//Don't ask for saving changes.
	public boolean userPermitsCloseWithUnsavedChanges() {
		return true;
    }

	// mostly taken from the super class
	/**
	 * Sets the default file name to the current time string
	 */
	public void captureAsImage()
	{
		AVBase avBase = ((AVController) super.document).getAVBase();
		if (this.snapshotFileChooser == null)
		{
			this.snapshotFileChooser = new JFileChooser(avBase
					.getSnapshotDirectory());
		}

		File dir = this.snapshotFileChooser.getCurrentDirectory();
		String currentTimeAsString = AVBaseConstants.SNAPSHOT_FILE_DATE_FORMAT
				.format(new Date(System.currentTimeMillis()));
		this.snapshotFileChooser.setSelectedFile(new File(
				dir,
				currentTimeAsString + ".png"));

		Component componentToCapture = this.getContentPane();

		displayFileChooser(avBase, componentToCapture);
	}
}
