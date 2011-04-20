/*
 * Created on Mar 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.tooltip;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultiLineToolTipUI extends BasicToolTipUI
{
    static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
    static
    {
    	ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
    }
    
	public final static String UI_CLASS_ID = "MultiLineToolTipUI";
	public final static String CLASS_NAME = "epics.archiveviewer.xal.view.tooltip.MultiLineToolTipUI";
	
	private JTextArea textArea;
	
    public static ComponentUI createUI(JComponent c) {
        return sharedInstance;
    }

	
	public MultiLineToolTipUI()
	{
		super();
		this.textArea = new JTextArea();
		this.textArea.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
	}
	
	public Dimension getMaximumSize(JComponent c) 
	{
		return getPreferredSize(c);
	}
	
	public Dimension getMinimumSize(JComponent c) {
		return getPreferredSize(c);
	}
	
	public Dimension getPreferredSize(JComponent c) {
		MultiLineToolTip mtp = (MultiLineToolTip) c;

		
		return new Dimension(mtp.getWidth(), mtp.getHeight());
	}
	
	public void paint(Graphics g, JComponent c) {
		MultiLineToolTip mtp = (MultiLineToolTip) c;
		this.textArea.setBounds(0, 0, mtp.getWidth(), mtp.getHeight());

		String tipText = mtp.getTipText();
		if(tipText == null)
			tipText = "";
		this.textArea.setText(tipText);
		
		this.textArea.getUI().paint(g, this.textArea);	
	}
}
