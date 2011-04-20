/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------------------------
 * StandardContourToolTipGenerator.java
 * ------------------------------------
 * (C) Copyright 2002, 2003, by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: StandardContourToolTipGenerator.java,v 1.1.1.1 2007/01/29 22:46:48 rdh Exp $
 *
 * Changes
 * -------
 * 23-Jan-2003 : Added standard header (DG);
 * 21-Mar-2003 : Implemented Serializable (DG);
 * 15-Jul-2004 : Switched the getZ() and getZValue() methods (DG);
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.data.contour.ContourDataset;

/**
 * A standard tooltip generator for plots that use data from an {@link ContourDataset}.
 *
 * @author David M. O'Donnell
 */
public class StandardContourToolTipGenerator implements ContourToolTipGenerator, Serializable {

    /** The number formatter. */
    private DecimalFormat valueForm = new DecimalFormat("##.###");

    /**
     * Generates a tooltip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param item  the item index (zero-based).
     *
     * @return The tooltip text.
     */
    public String generateToolTip(ContourDataset data, int item) {

        Number x = data.getX(0, item);
        Number y = data.getY(0, item);
        Number z = data.getZ(0, item);
        String xString = null;

        if (data.isDateAxis(0)) {
            SimpleDateFormat formatter = new java.text.SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
            StringBuffer strbuf = new StringBuffer();
            strbuf = formatter.format(
                new Date(x.longValue()), strbuf, new java.text.FieldPosition(0)
            );
            xString = strbuf.toString();
        }
        else {
            xString = this.valueForm.format(x.doubleValue());
        }
        if (z != null) {
            return "X: " + xString
                   + ", Y: " + this.valueForm.format(y.doubleValue())
                   + ", Z: " + this.valueForm.format(z.doubleValue());
        }
        else {
            return "X: " + xString
                 + ", Y: " + this.valueForm.format(y.doubleValue())
                 + ", Z: no data";
        }

    }

    /**
     * Tests if this object is equal to another.
     *
     * @param obj  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof StandardContourToolTipGenerator)) {
            return false;
        }
        StandardContourToolTipGenerator that = (StandardContourToolTipGenerator) obj;
        if (this.valueForm != null) {
            return this.valueForm.equals(that.valueForm);
        }
        return false;

    }

}
