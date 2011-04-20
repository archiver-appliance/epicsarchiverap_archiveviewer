/*
 * Created on Mar 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view.tooltip;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultiLineToolTip extends JToolTip
{
	private final int width;
	private final int height;

	public MultiLineToolTip(int _width, int _height)
	{
		super();
		this.width = _width;
		this.height = _height;
	}
	
	public int getWidth() {
		String s = getTipText();
		if(s == null || s.trim().equals(""))
			return 0;
		return this.width;
	}
	
	public int getHeight() {
		String s = getTipText();
		if(s == null || s.trim().equals(""))
			return 0;
		return this.height;
	}

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((MultiLineToolTipUI)UIManager.getUI(this));
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "ToolTipUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return MultiLineToolTipUI.UI_CLASS_ID;
    }
}
