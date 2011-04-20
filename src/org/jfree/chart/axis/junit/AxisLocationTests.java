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
 * ----------------------
 * AxisLocationTests.java
 * ----------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisLocationTests.java,v 1.1.1.1 2007/01/29 22:46:44 rdh Exp $
 *
 * Changes
 * -------
 * 10-Jul-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.AxisLocation;

/**
 * Tests for the {@link AxisLocation} class.
 *
 */
public class AxisLocationTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(AxisLocationTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public AxisLocationTests(String name) {
        super(name);
    }
    
    /**
     * Problem equals method.
     */
    public void testEquals() {
        assertEquals(AxisLocation.TOP_OR_RIGHT, AxisLocation.TOP_OR_RIGHT);
        assertEquals(AxisLocation.BOTTOM_OR_RIGHT, AxisLocation.BOTTOM_OR_RIGHT);    
        assertEquals(AxisLocation.TOP_OR_LEFT, AxisLocation.TOP_OR_LEFT);    
        assertEquals(AxisLocation.BOTTOM_OR_LEFT, AxisLocation.BOTTOM_OR_LEFT);    
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {
        AxisLocation location1 = AxisLocation.BOTTOM_OR_RIGHT;
        AxisLocation location2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(location1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            location2 = (AxisLocation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(location1, location2);
        boolean same = location1 == location2;
        assertEquals(true, same);        
    }

}
