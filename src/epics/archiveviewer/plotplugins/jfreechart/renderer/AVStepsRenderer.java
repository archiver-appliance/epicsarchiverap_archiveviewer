package epics.archiveviewer.plotplugins.jfreechart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class AVStepsRenderer extends AVAbstractRenderer 
                            implements XYItemRenderer, 
                                       Cloneable,
                                       PublicCloneable,
                                       Serializable {

	private final boolean continueLastLine;
	
    public AVStepsRenderer(boolean isLastLineToBeContinued)
    {
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
     * @param rangeAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot (<code>null</code> permitted).
     * @param pass  the pass index (ignored here).
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
       
        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        
        if(Double.isNaN(x1))
        {
        	return;
        }
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = -1;       
        
    	if(Double.isNaN(y1) == false)
    		transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        if (item > 0) {
            // get the previous data point...
            double x0 = dataset.getXValue(series, item - 1);
            double y0 = dataset.getYValue(series, item - 1);
            if (Double.isNaN(x0) == false && Double.isNaN(y0) == false) 
            {
            	//previous value not NaN at this point
                double transX0 = domainAxis.valueToJava2D(
                    x0, dataArea, xAxisLocation
                );
                double transY0 = rangeAxis.valueToJava2D(
                    y0, dataArea, yAxisLocation
                );
                
            	if(Double.isNaN(y1))
            		//the same as the value before
            		transY1 = transY0;

                Line2D line = state.workingLine;
                //draw a step
                line.setLine(transX0, transY0, transX1, transY0);
                g2.draw(line);
                line.setLine(transX1, transY0, transX1, transY1);
                g2.draw(line);
            }
        }
    	//if this is last item, we might need to draw till the right edge of the screen
        if(	this.continueLastLine == true &&
        	item == dataset.getItemCount(series) - 1 && 
			Double.isNaN(y1) == false)
        {
            state.workingLine.setLine(transX1, transY1, dataArea.getMaxX(), transY1);
            g2.draw(state.workingLine);                        	               	
        }

        updateCrosshairValues(
            crosshairState, x1, y1, transX1, transY1, PlotOrientation.VERTICAL
        );
        // collect entity and tool tip information...
       
        Shape entityArea = null;
        if (state.getInfo() != null) {
            EntityCollection entities = state.getInfo().getOwner().getEntityCollection();
            if (entities != null) 
            {        
            	if(Double.isNaN(y1) == false)
            	{
            		int entityRadius = getDefaultEntityRadius();
            		if(item == dataset.getItemCount(series) - 1 && this.continueLastLine)
    	            {
            			//if this is last item 
            			if(this.continueLastLine)
            				state.workingLine.setLine(transX1, transY1, dataArea.getMaxX(), transY1);
            			else
            				state.workingLine.setLine(transX1, transY1, transX1, transY1);
	                    entityArea = new Rectangle2D.Double(transX1 - entityRadius, transY1 - entityRadius, dataArea.getMaxX() - transX1 + entityRadius, 2 * entityRadius); 
    	            }
    	            else
    	            {
    	                double x2 = dataset.getXValue(series, item + 1);
    	                if (Double.isNaN(x2))
    	                {
    	                	//should never be the case however
    	                	return;
    	                }
    	                double transX2 = domainAxis.valueToJava2D(x2, dataArea, xAxisLocation);
    	                
	                	entityArea = new Rectangle2D.Double(transX1 - entityRadius, transY1 - entityRadius, transX2 - transX1 + entityRadius, 2 * entityRadius); 
    	            }                
	                /*only for debugging: draw the shapes
	                java.awt.Color c = g2.getColor();
	                g2.setColor(java.awt.Color.BLACK);
	                g2.draw(shape);
	                g2.setColor(c);*/
            	}
            	else
            	{
                    //draw the shape on screen only if NaN
            		Shape shape = getItemShape(series, item);
            		
                    g2.setClip(info.getPlotArea());
                    
                	transY1 = dataArea.getMaxY() + 0.75 * (g2.getClip().getBounds2D().getMaxY() - dataArea.getMaxY());
                    shape = createTransformedShape(shape, transX1, transY1);
                    
                    g2.fill(shape);
                    
                    g2.setClip(dataArea);
                    
                    entityArea = shape;
            	}
                String tip = null;
                XYToolTipGenerator generator = getToolTipGenerator(series, item);
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }

                
                //add an entity for the item...
                if (entities != null) {
                    addEntity(entities, entityArea, dataset, series, item, transX1, transY1);
                }
            }
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
