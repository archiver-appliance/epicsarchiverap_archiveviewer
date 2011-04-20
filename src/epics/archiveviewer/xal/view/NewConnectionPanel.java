/*
 * Created on 07.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.xal.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComboBox;

import epics.archiveviewer.xal.view.components.AVAbstractPanel;

/**
 * @author Sergei Chevtsov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewConnectionPanel extends AVAbstractPanel
{
    private JComboBox parametersBox;
    
    public NewConnectionPanel()
    {
        init();
    }
    
    protected void createComponents()
    {
        this.parametersBox = new JComboBox();
        this.parametersBox.setEditable(true);
        this.parametersBox.setPreferredSize(new Dimension(640, this.parametersBox.getPreferredSize().height));
    }
    
    protected void addComponents()
    {     
       setLayout(new BorderLayout());
       add(this.parametersBox, BorderLayout.NORTH);
    }
    
    public JComboBox getParametersBox()
    {
        return this.parametersBox;
    }
}
