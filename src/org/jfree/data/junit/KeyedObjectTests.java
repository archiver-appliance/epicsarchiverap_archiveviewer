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
 * ---------------------
 * KeyedObjectTests.java
 * ---------------------
 * (C) Copyright 2004 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: KeyedObjectTests.java,v 1.1.1.1 2007/01/29 22:46:52 rdh Exp $
 *
 * Changes
 * -------
 * 27-Jan-2004 : Version 1 (DG);
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

import org.jfree.data.KeyedObject;

/**
 * Tests for the {@link KeyedObject} class.
 *
 */
public class KeyedObjectTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(KeyedObjectTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public KeyedObjectTests(final String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        KeyedObject ko1 = new KeyedObject("Test", "Object");
        KeyedObject ko2 = new KeyedObject("Test", "Object");
        assertTrue(ko1.equals(ko2));
        assertTrue(ko2.equals(ko1));

        ko1 = new KeyedObject("Test 1", "Object");
        ko2 = new KeyedObject("Test 2", "Object");
        assertFalse(ko1.equals(ko2));

        ko1 = new KeyedObject("Test", "Object 1");
        ko2 = new KeyedObject("Test", "Object 2");
        assertFalse(ko1.equals(ko2));

    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        final KeyedObject ko1 = new KeyedObject("Test", "Object");
        KeyedObject ko2 = null;
        try {
            ko2 = (KeyedObject) ko1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("KeyedObjectTests.testCloning: failed to clone.");
        }
        assertTrue(ko1 != ko2);
        assertTrue(ko1.getClass() == ko2.getClass());
        assertTrue(ko1.equals(ko2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final KeyedObject ko1 = new KeyedObject("Test", "Object");
        KeyedObject ko2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(ko1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            ko2 = (KeyedObject) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(ko1, ko2);

    }

}
