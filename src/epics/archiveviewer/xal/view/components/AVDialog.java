/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVDialog extends JDialog
{	
	private JButton okButton;
	private JButton cancelButton;

	/**
	 * 
	 * @param p
	 * @param owner
	 * @param title
	 * @param modal
	 * @param resizable
	 * @param relativeLocationComponent
	 * @param okActionListener
	 * @param buttonsAlignment FlowLayout.LEFT, FlowLayout.CENTER, or FlowLayout.RIGHT 
	 * (if -1, no OK/CANCEL buttons will be added)
	 * @throws NullPointerException
	 */
	public AVDialog(
			JPanel p,
			JFrame owner,
			String title,
			boolean modal,
			boolean resizable,
			Component relativeLocationComponent,
			ActionListener okActionListener, int buttonsAlignment
			) throws NullPointerException
	{
		super(owner, title, modal);
		if(owner == null)
			throw new NullPointerException("An AVDialog can not be created without an owner");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		JPanel buttonsPanel2 = null;
		if(buttonsAlignment != -1)
		{
			this.okButton = new JButton("OK");
			ActionListener closeAction = new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dispose();
				}
			};
			if(okActionListener!= null)
			{
				this.okButton.addActionListener(okActionListener);
			}
			this.okButton.addActionListener(closeAction);
			
			this.cancelButton = new JButton("Cancel");
			this.cancelButton.addActionListener(closeAction);

			GridLayout gl = new GridLayout(1,0);
			gl.setHgap(7);
			JPanel buttonsPanel = new JPanel(gl);
			buttonsPanel.add(this.okButton);
			buttonsPanel.add(this.cancelButton);
			
			buttonsPanel2 = new JPanel(new FlowLayout(buttonsAlignment, 7, 0));
			buttonsPanel2.add(buttonsPanel);	
		}
		
		getContentPane().setLayout(new BorderLayout(0, 5));
		getContentPane().add(p, BorderLayout.CENTER);
		
		if(buttonsPanel2 != null)
			getContentPane().add(buttonsPanel2, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(relativeLocationComponent);
		setResizable(resizable);
		setVisible(true);
	}
}
