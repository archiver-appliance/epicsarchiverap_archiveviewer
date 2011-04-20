/*
 * Created on Dec 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package epics.archiveviewer.plotplugins.jfreechart;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;

/**
 * @author serge
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AVEntityCollection implements EntityCollection
{
    /** Storage for the entities. */
    private Collection entities;

    /**
     * Constructs a new entity collection (initially empty).
     */
    public AVEntityCollection() {
    	//use a thread safe vector
    	//the rest of code is from org.jfree.chart.StandardEntityCollection
        this.entities = new java.util.Vector();
    }

    /**
     * Clears the entities.
     */
    public void clear() {
        this.entities.clear();
    }

    /**
     * Adds an entity.
     *
     * @param entity  the entity.
     */
    public void addEntity(ChartEntity entity) {
        this.entities.add(entity);
    }
    
    /**
     * Adds all the entities from the specified collection.
     * 
     * @param collection  the collection of entities.
     */
    public void addEntities(EntityCollection collection) {
        this.entities.addAll(collection.getEntities());
    }

    /**
     * Returns an entity for the specified coordinates.
     *
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     *
     * @return the entity.
     */
    public ChartEntity getEntity(double x, double y) {

        ChartEntity result = null;

        Iterator iterator = this.entities.iterator();
        while (iterator.hasNext()) {
            ChartEntity entity = (ChartEntity) iterator.next();
            if (entity.getArea().contains(x, y)) {
                result = entity;
            }
        }

        return result;
    }

    /**
     * Returns the entities in an unmodifiable collection.
     * 
     * @return The entities.
     */
    public Collection getEntities() {
        return Collections.unmodifiableCollection(this.entities);
    }
    
    /**
     * Returns an iterator for the entities in the collection.
     *
     * @return An iterator.
     */
    public Iterator iterator() {
        return this.entities.iterator();
    }

}
