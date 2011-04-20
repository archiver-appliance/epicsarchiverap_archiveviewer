package epics.archiveviewer.plotplugins.jfreechart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * Standard item renderer for an {@link XYPlot}.  This class can draw (a) shapes at
 * each point, or (b) lines between points, or (c) both shapes and lines.
 */
public class AVLinesRenderer extends AVAbstractRenderer 
                                    implements XYItemRenderer,
                                               Cloneable,
                                               PublicCloneable,
                                               Serializable {
	private final boolean continueLastLine;
    /**
     * Constructs a new renderer.
     * @param toolTipGenerator  the item label generator (<code>null</code> permitted).
     */
    public AVLinesRenderer(boolean isLastLineToBeContinued) {
    	this.continueLastLine = isLastLineToBeContinued;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    /*the plotted value stays the same, until there is a new
     *archived value, even if the new value is NaN
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        if (!getItemVisible(series, item)) {
            return;   
        }
        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        Paint paint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(seriesStroke);

        
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        
        if (Double.isNaN(x1))
        {
        	return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = -1;
        //needed to be visible for every instruction below
        if(Double.isNaN(y1) == false)
            transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        if (item > 0) 
        {
            // get the previous data point...
            double x0 = dataset.getXValue(series, item - 1);
            double y0 = dataset.getYValue(series, item - 1);

            if (!Double.isNaN(x0) && !Double.isNaN(y0)) {
            	//previous value was definitely not NaN at this point
                double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
                double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);

                if(Double.isNaN(y1))
                	//leave the value
                	transY1 = transY0;
                
                // only draw if we have good values
                if (Double.isNaN(transX0) || Double.isNaN(transY0) 
                    || Double.isNaN(transX1) || Double.isNaN(transY1)) {
                    return;
                }

                state.workingLine.setLine(transX0, transY0, transX1, transY1);

                if (state.workingLine.intersects(dataArea)) {
                    g2.draw(state.workingLine);
                }  
            }
        }
        //if not a number, handle below as a shape
        if(Double.isNaN(y1) == false)
        {
        	//create tooltip entities
            int entityRadius = getDefaultEntityRadius();
            if(item == dataset.getItemCount(series) - 1)
            {
            	//if this is last item, draw till the edge
            	if(this.continueLastLine == true)
            	{
                    state.workingLine.setLine(transX1, transY1, dataArea.getMaxX(), transY1);
                    entityArea = new Rectangle2D.Double(transX1 - entityRadius, transY1 - entityRadius, dataArea.getMaxX() - transX1 + entityRadius, 2 * entityRadius);   
            	}
            	else
            	{
            		entityArea = createTransformedShape(getItemShape(series, item), transX1, transY1);
            	}
            	
            }
            else
            {
            	//draw the entity to the next data point
                double x2 = dataset.getXValue(series, item + 1);
                double y2 = dataset.getYValue(series, item + 1);
                if (Double.isNaN(x2))
                {
                	//should never be the case however
                	return;
                }
                double transX2 = domainAxis.valueToJava2D(x2, dataArea, xAxisLocation);
                double transY2 = -1;
                if(Double.isNaN(y2) == false)
                {	
                    transY2 = rangeAxis.valueToJava2D(y2, dataArea, yAxisLocation);
                	
                    int[] xpoints = new int[]
											{
                    							(int)transX1 - entityRadius,
												(int)transX1 + entityRadius,
												(int)transX2 + entityRadius,
												(int)transX2 + entityRadius,
												(int)transX2 - entityRadius,
												(int)transX1 - entityRadius														
											};
                    int[] ypoints = new int[]
											{
                    							(int)transY1 - entityRadius, 
												(int)transY1 - entityRadius,
												(int)transY2 - entityRadius,
												(int)transY2 + entityRadius,
												(int)transY2 + entityRadius,
												(int)transY1 + entityRadius														
											};
                    int npoints = 6;
                    entityArea = new Polygon(xpoints, ypoints, npoints);	          
                }
                else
                {
                    entityArea = new Rectangle2D.Double(transX1 - entityRadius, transY1 - entityRadius, transX2 - transX1 + entityRadius, 2 * entityRadius); 
                }
            }  
        }
        else
        {            
        	Shape shape = getItemShape(series, item);
    		g2.setClip(info.getPlotArea());

    		transY1 = dataArea.getMaxY() + 0.75 * (g2.getClip().getBounds2D().getMaxY() - dataArea.getMaxY());
            shape = createTransformedShape(shape, transX1, transY1);

        	g2.fill(shape);
        	g2.setClip(dataArea);
 
            entityArea = shape;
        }
        
        /*only for debugging: draw the shapes
        java.awt.Color c = g2.getColor();
        g2.setColor(java.awt.Color.BLACK);
        g2.draw(entityArea);
        g2.setColor(c);*/

        // draw the item label if there is one...
        if (isItemLabelVisible(series, item)) {
            drawItemLabel(
                g2, PlotOrientation.VERTICAL, dataset, series, item, transX1, transY1, (y1 < 0.0)
            );
        }

        updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, PlotOrientation.VERTICAL);

        // add an entity for the item...
        if (entities != null) {
            addEntity(entities, entityArea, dataset, series, item, transX1, transY1);
        }
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
