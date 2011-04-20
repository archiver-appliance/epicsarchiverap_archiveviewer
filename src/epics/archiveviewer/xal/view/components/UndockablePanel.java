/*
 * Created on Feb 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import epics.archiveviewer.base.Icons;
import epics.archiveviewer.xal.view.plotplugins.PlotPluginWrapperPanel;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class UndockablePanel extends JPanel implements ActionListener
{
	private final JPanel contentPanel;
	private final JFrame owner;
	private JButton dockButton;
	private JDialog wrappedDialog;	
	
	private void createComponents() {
		this.dockButton = new JButton();
		this.dockButton.setIcon(new ImageIcon(Icons.UNDOCK_IMAGE));
		this.dockButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		this.wrappedDialog = new JDialog(this.owner, false);
	}

	private void addComponents() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(this.dockButton, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);
		add(this.contentPanel, BorderLayout.CENTER);
	}
	
	public UndockablePanel(JPanel content, JFrame _owner)
	{
		this.contentPanel = content;
		this.owner = _owner;
		createComponents();
		addComponents();
		this.dockButton.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(this.wrappedDialog.isShowing())
		{
			//dock
			this.wrappedDialog.setVisible(false);
			this.wrappedDialog.getContentPane().removeAll();
			this.dockButton.setIcon(new ImageIcon(Icons.DOCK_IMAGE));
			addToContainer();
		}
		else
		{
			this.wrappedDialog.setLocationRelativeTo(this.contentPanel.getParent());
			//undock
			removeFromContainer();
			this.dockButton.setIcon(new ImageIcon(Icons.UNDOCK_IMAGE));
			this.wrappedDialog.getContentPane().add(this);
			this.wrappedDialog.setSize(this.contentPanel.getSize());
			this.wrappedDialog.setVisible(true);
		}
	}
	
	public abstract void addToContainer();
	
	public abstract void removeFromContainer();
	
	public JDialog getWrapperDialog()
	{
		return this.wrappedDialog;
	}
}
