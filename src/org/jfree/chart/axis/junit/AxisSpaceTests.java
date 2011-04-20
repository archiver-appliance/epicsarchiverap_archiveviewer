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
 * -------------------
 * AxisSpaceTests.java
 * -------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisSpaceTests.java,v 1.1.1.1 2007/01/29 22:46:44 rdh Exp $
 *
 * Changes
 * -------
 * 14-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.AxisSpace;

/**
 * Tests for the {@link AxisSpace} class.
 *
 */
public class AxisSpaceTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(AxisSpaceTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public AxisSpaceTests(String name) {
        super(name);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        AxisSpace s1 = new AxisSpace();
        AxisSpace s2 = null;
        try {
            s2 = (AxisSpace) s1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("AxisSpaceTests.testCloning: failed to clone.");
        }
        assertTrue(s1 != s2);
        assertTrue(s1.getClass() == s2.getClass());
        assertTrue(s1.equals(s2));
    }
    
}

