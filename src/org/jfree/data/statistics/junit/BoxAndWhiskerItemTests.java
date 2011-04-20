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
 * ---------------------------
 * BoxAndWhiskerItemTests.java
 * ---------------------------
 * (C) Copyright 2004 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BoxAndWhiskerItemTests.java,v 1.1.1.1 2007/01/29 22:46:52 rdh Exp $
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.BoxAndWhiskerItem;

/**
 * Tests for the {@link BoxAndWhiskerItem} class.
 *
 */
public class BoxAndWhiskerItemTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(BoxAndWhiskerItemTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public BoxAndWhiskerItemTests(final String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        final BoxAndWhiskerItem i1 = new BoxAndWhiskerItem(
            new Double(1.0), new Double(2.0), new Double(3.0), new Double(4.0), 
            new Double(5.0), new Double(6.0), new Double(7.0), new Double(8.0), new ArrayList()
        );
        final BoxAndWhiskerItem i2 = new BoxAndWhiskerItem(
            new Double(1.0), new Double(2.0), new Double(3.0), new Double(4.0), 
            new Double(5.0), new Double(6.0), new Double(7.0), new Double(8.0), new ArrayList()
        );
        assertTrue(i1.equals(i2));
        assertTrue(i2.equals(i1));

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final BoxAndWhiskerItem i1 = new BoxAndWhiskerItem(
            new Double(1.0), new Double(2.0), new Double(3.0), new Double(4.0), 
            new Double(5.0), new Double(6.0), new Double(7.0), new Double(8.0), new ArrayList()
        );
        BoxAndWhiskerItem i2 = null;
        
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(i1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            i2 = (BoxAndWhiskerItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(i1, i2);

    }

}
