/*
 * Created on Dec 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart.renderer;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.data.xy.XYDataset;

import epics.archiveviewer.plotplugins.jfreechart.AVItemEntity;


/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AVAbstractRenderer extends AbstractXYItemRenderer
{

	private static final int ENTITY_RADIUS = 3;

    /**
     * Adds an entity to the collection.
     * 
     * @param entities  the entity collection being populated.
     * @param area  the entity area (if <code>null</code> a default will be used).
     * @param entityX  the entity's center x-coordinate in user space.
     * @param entityY  the entity's center y-coordinate in user space.
     * @param dataset  the dataset.
     * @param series  the series.
     * @param item  the item.
     */
    protected final void addEntity(EntityCollection entities, Shape area, 
                             XYDataset dataset, int series, int item,
                             double entityX, double entityY) {
        
        if (area == null) {
            area = new Ellipse2D.Double(
                entityX - ENTITY_RADIUS, entityY - ENTITY_RADIUS, 
                ENTITY_RADIUS * 2, ENTITY_RADIUS * 2
            );
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
        //use entities that get tooltip on demand only
        AVItemEntity entity = new AVItemEntity(area, dataset, series, item, tip, url);
        entities.addEntity(entity);
    }
}
