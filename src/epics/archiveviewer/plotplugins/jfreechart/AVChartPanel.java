/*
 * Created on Dec 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JToolTip;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ExtensionFileFilter;

import epics.archiveviewer.xal.view.tooltip.MultiLineToolTip;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVChartPanel extends ChartPanel
{
	private VolatileImage chartBuffer;
	
    public AVChartPanel(JFreeChart chart) 
    {
    	super(	
			chart, 
			DEFAULT_WIDTH,
			DEFAULT_HEIGHT,
			DEFAULT_MINIMUM_DRAW_WIDTH,
			DEFAULT_MINIMUM_DRAW_HEIGHT,
			DEFAULT_MAXIMUM_DRAW_WIDTH,
			DEFAULT_MAXIMUM_DRAW_HEIGHT,
			true,
			false,
			true,
			true,
			false,
			true);
    	
    }
    
    public void doSaveAs() throws IOException {

        JFileChooser fileChooser = new JFileChooser();
        ExtensionFileFilter filter = 
            new ExtensionFileFilter(localizationResources.getString("PNG_Image_Files"), ".png");
        fileChooser.addChoosableFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (fileChooser.getFileFilter().equals(filter)) {
                if (!filename.endsWith(".png")) {
                    filename = filename + ".png";
                }
            }
            ChartUtilities.saveChartAsPNG(new File(filename), getChart(), getWidth(), getHeight());
        }
    }
    
    public void paintComponent(Graphics g) {
       
        if (getChart() == null)
            return;
        final Graphics2D g2 = (Graphics2D) g.create();
        
        //no scaling
        setScaleX(1.0);
        
        setScaleY(1.0);

        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        
        Rectangle2D available = new Rectangle2D.Double();
        available.setRect(
            insets.left, insets.top,
            size.getWidth() - insets.left - insets.right,
            size.getHeight() - insets.top - insets.bottom
        );

        Rectangle2D chartArea = new Rectangle2D.Double();
        chartArea.setRect(0.0, 0.0, available.getWidth(), available.getHeight());

        //if it's the first time we got called, or the chartArea changed
        if(		this.chartBuffer == null ||
				this.chartBuffer.getWidth(this) != chartArea.getWidth() ||
				this.chartBuffer.getHeight(this) != chartArea.getHeight()
		)
        {
            this.chartBuffer = createVolatileImage((int) chartArea.getWidth(), (int) chartArea.getHeight());
            setRefreshBuffer(true);
        }

        if(getRefreshBuffer() == true)
        {
        	JFreeChartPlotImageGenerator.generate(
        			this.chartBuffer,
        			getChart(),
        			size,
        			insets,
        			null,
        			getInfo()
        			);
        }
		g2.drawImage(this.chartBuffer, insets.left, insets.right, this);
		
	    setVerticalTraceLine(null);
	    setHorizontalTraceLine(null);
    }
    
    public JToolTip createToolTip() 
    {
         JToolTip tip = new MultiLineToolTip(320, 120);
         tip.setComponent(this);
         return tip;
    }
}