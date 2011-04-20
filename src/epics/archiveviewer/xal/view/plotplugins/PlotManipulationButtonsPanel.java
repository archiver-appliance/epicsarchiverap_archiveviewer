package epics.archiveviewer.xal.view.plotplugins;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import epics.archiveviewer.base.AVBaseConstants;
import epics.archiveviewer.base.Icons;
import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * This class is a panel with buttons for plot manipulations
 * 
 * @author Sergei Chevtsov
 */
public class PlotManipulationButtonsPanel extends AVAbstractPanel
{
	private JButton goLeftButton;

	private JButton goRightButton;
	
	private JButton goUpButton;
	private JButton goDownButton;
	private JButton zoomOutVerticallyButton;
	private JButton zoomInVerticallyButton;
	private JButton zoomOutHorizontallyButton;
	private JButton zoomInHorizontallyButton;
	private JButton moreButton;
	
	private JPopupMenu additionalParametersMenu;	
	private JCheckBoxMenuItem antiAliasItem;
	private JCheckBoxMenuItem leaveIgnoredItem;

	public PlotManipulationButtonsPanel()
	{
		init();
	}

	/** Lays out the components */
	protected void addComponents()
	{
		JPanel p = new JPanel(new GridLayout(1, 0));
		p.add(this.goLeftButton);
		p.add(this.goRightButton);
		p.add(this.goUpButton);
		p.add(this.goDownButton);
		p.add(this.zoomOutHorizontallyButton);
		p.add(this.zoomOutVerticallyButton);
		p.add(this.zoomInHorizontallyButton);
		p.add(this.zoomInVerticallyButton);
		p.add(this.moreButton);
		
		this.additionalParametersMenu.add(this.antiAliasItem);
		this.additionalParametersMenu.add(this.leaveIgnoredItem);

		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(p);
	}
	
	protected void addListeners()
	{
		this.moreButton.addActionListener(new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						additionalParametersMenu.show(
											moreButton,
											0,
											0
								);
					}
				});
	}

	protected void createComponents()
	{
		this.goLeftButton = new JButton(new ImageIcon(Icons.LEFT_ARROW));

		this.goRightButton = new JButton(new ImageIcon(Icons.RIGHT_ARROW));
		
		this.goUpButton = new JButton(new ImageIcon(Icons.UP_ARROW));
		
		this.goDownButton = new JButton(new ImageIcon(Icons.DOWN_ARROW));
	
		this.zoomOutHorizontallyButton = new JButton(new ImageIcon(Icons.ZOOM_OUT_HORIZONTAL_IMAGE));
		
		this.zoomInHorizontallyButton = new JButton(new ImageIcon(Icons.ZOOM_IN_HORIZONTAL_IMAGE));
		
		this.zoomOutVerticallyButton = new JButton(new ImageIcon(Icons.ZOOM_OUT_VERTICAL_IMAGE));
		
		this.zoomInVerticallyButton = new JButton(new ImageIcon(Icons.ZOOM_IN_VERTICAL_IMAGE));
		
		this.moreButton = new JButton(AVBaseConstants.MORE_BUTTON_LABEL);

		this.additionalParametersMenu = new JPopupMenu();
		
		this.antiAliasItem = new JCheckBoxMenuItem("Anti-Alias", false);
		this.leaveIgnoredItem = new JCheckBoxMenuItem("Leave Ignored Items", false);
	}
	
	public JButton getGoLeftButton()
	{
		return this.goLeftButton;
	}
	
	public JButton getGoRightButton()
	{
		return this.goRightButton;
	}
	
	public JButton getGoUpButton()
	{
		return this.goUpButton;
	}
	
	public JButton getGoDownButton()
	{
		return this.goDownButton;
	}
	
	public JButton getZoomInHorizontallyButton()
	{
		return this.zoomInHorizontallyButton;
	}
	
	public JButton getZoomInVerticallyButton()
	{
		return this.zoomInVerticallyButton;
	}
	
	public JButton getZoomOutVerticallyButton()
	{
		return this.zoomOutVerticallyButton;
	}
	
	public JButton getZoomOutHorizontallyButton()
	{
		return this.zoomOutHorizontallyButton;
	}
	
	public JButton getMoreButton()
	{
		return this.moreButton;
	}
	
	public JPopupMenu getAdditionalParametersMenu()
	{
		return this.additionalParametersMenu;
	}
	
	public JCheckBoxMenuItem getAntiAliasItem()
	{
		return this.antiAliasItem;
	}
	
	public JCheckBoxMenuItem getLeaveIgnoredItem()
	{
		return this.leaveIgnoredItem;
	}
	
}