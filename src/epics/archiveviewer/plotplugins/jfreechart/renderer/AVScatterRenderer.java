package epics.archiveviewer.plotplugins.jfreechart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

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

public class AVScatterRenderer extends AVAbstractRenderer 
                                    implements XYItemRenderer,
                                               Cloneable,
                                               PublicCloneable,
                                               Serializable 
{

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
    /*changed to comply with archive server
     *=> the value stays the same, until there is a new
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
        Shape shape = getItemShape(series, item);
        
    	if(Double.isNaN(y1))
    	{
    		//10% below the time axis
            g2.setClip(info.getPlotArea());
        	transY1 = dataArea.getMaxY() + 0.75 * (g2.getClip().getBounds2D().getMaxY() - dataArea.getMaxY());
            shape = createTransformedShape(shape, transX1, transY1);
    		g2.fill(shape);
    	 	g2.setClip(dataArea);
        }
        else {
        	transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        	shape = createTransformedShape(shape, transX1, transY1);
        	if(shape.intersects(dataArea))
        		g2.fill(shape);
        }
        entityArea = shape;

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
