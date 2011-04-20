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
 * ------------------------
 * KeyedObjects2DTests.java
 * ------------------------
 * (C) Copyright 2004 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedObjects2DTests.java,v 1.1.1.1 2007/01/29 22:46:52 rdh Exp $
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.KeyedObjects2D;

/**
 * Tests for the {@link KeyedObjects2D} class.
 *
 */
public class KeyedObjects2DTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(KeyedObjects2DTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public KeyedObjects2DTests(final String name) {
        super(name);
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        final KeyedObjects2D o1 = new KeyedObjects2D();
        o1.setObject(new Integer(1), "V1", "C1");
        o1.setObject(null, "V2", "C1");
        o1.setObject(new Integer(3), "V3", "C2");
        KeyedObjects2D o2 = null;
        try {
            o2 = (KeyedObjects2D) o1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("KeyedObjects2DTests.testCloning: failed to clone.");
        }
        assertTrue(o1 != o2);
        assertTrue(o1.getClass() == o2.getClass());
        assertTrue(o1.equals(o2));
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final KeyedObjects2D ko2D1 = new KeyedObjects2D();
        ko2D1.addObject(new Double(234.2), "Row1", "Col1");
        ko2D1.addObject(null, "Row1", "Col2");
        ko2D1.addObject(new Double(345.9), "Row2", "Col1");
        ko2D1.addObject(new Double(452.7), "Row2", "Col2");

        KeyedObjects2D ko2D2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(ko2D1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            ko2D2 = (KeyedObjects2D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(ko2D1, ko2D2);

    }

}
