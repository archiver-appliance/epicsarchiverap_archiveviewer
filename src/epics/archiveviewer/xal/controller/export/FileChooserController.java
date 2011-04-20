/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.controller.export;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
class FileChooserController implements ActionListener
{
	private final String homeDirectory;
	private final JTextField filePathField;
	private final JButton fileChooserButton;
	
	public FileChooserController(String homeDir, JTextField pathF, JButton chooserB)
	{
		this.homeDirectory = homeDir;
		this.filePathField = pathF;
		this.fileChooserButton = chooserB;
		this.fileChooserButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) 
	{
		JFileChooser jfc = new JFileChooser();
		
		File f = null;

		if (this.filePathField.getText().equals("") == false)
		{
			try
			{
				f = new File(this.filePathField.getText());
			}
			catch (Exception ex)
			{
				//do nothing
			}
		}
		else
		{
			try
			{
				f = new File(this.homeDirectory);
			}
			catch (Exception ex)
			{
				//do nothing
			}
		}
		
		jfc.setCurrentDirectory(f);

		if (jfc.showSaveDialog(this.fileChooserButton) == JFileChooser.APPROVE_OPTION)
		{	
			f = jfc.getSelectedFile();	
			if (f != null)
			{
				filePathField.setText(f.getAbsolutePath());
			}
		}		
	}
}
