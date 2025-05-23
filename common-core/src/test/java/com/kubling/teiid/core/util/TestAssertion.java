/*
 * Copyright Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the COPYRIGHT.txt file distributed with this work.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kubling.teiid.core.util;

import com.kubling.teiid.core.CorePlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * TestAssertion
 */
public class TestAssertion {

    private static final String TEST_MESSAGE = "This is a test assertion message";

    /*
     * Test for void assertTrue(boolean)
     */
    @Test
    public void testAssertTrueboolean() {
        Assertion.assertTrue(true);

        try {
            Assertion.assertTrue(false);
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            final String msg = CorePlugin.Util.getString("Assertion.Assertion_failed");
            assertEquals(msg, e.getMessage());
        }
    }

    /*
     * Test for void assertTrue(boolean, String)
     */
    @Test
    public void testAssertTruebooleanString() {
        Assertion.assertTrue(true, TEST_MESSAGE);

        try {
            Assertion.assertTrue(false, TEST_MESSAGE);
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            assertEquals(TEST_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testFailed() {
        try {
            Assertion.failed(null);
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            assertEquals("null", e.getMessage());
        }

        try {
            Assertion.failed(TEST_MESSAGE);
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            assertEquals(TEST_MESSAGE, e.getMessage());
        }
    }

    /*
     * Test for void isNull(Object)
     */
    @Test
    public void testIsNullObject() {
        Assertion.isNull(null);

        try {
            Assertion.isNull("");
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            final String msg = CorePlugin.Util.getString("Assertion.isNull");
            assertEquals(msg, e.getMessage());
        }
    }

    /*
     * Test for void isNull(Object, String)
     */
    @Test
    public void testIsNullObjectString() {
        Assertion.isNull(null, TEST_MESSAGE);

        try {
            Assertion.isNull("", TEST_MESSAGE);
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            assertEquals(TEST_MESSAGE, e.getMessage());
        }
    }

    /*
     * Test for void isNotNull(Object)
     */
    @Test
    public void testIsNotNullObject() {
        Assertion.isNotNull("");

        try {
            Assertion.isNotNull(null);
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            final String msg = CorePlugin.Util.getString("Assertion.isNotNull");
            assertEquals(msg, e.getMessage());
        }
    }

    /*
     * Test for void isNotNull(Object, String)
     */
    @Test
    public void testIsNotNullObjectString() {
        Assertion.isNotNull("", TEST_MESSAGE);

        try {
            Assertion.isNotNull(null, TEST_MESSAGE);
            fail();
        } catch (AssertionError e) {
            // expected, but check the message
            assertEquals(TEST_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void testIsInstanceOf() {
        Assertion.isInstanceOf(1, Integer.class, "name");
        Assertion.isInstanceOf("asdfasdf", String.class, "name2");

        try {
            Assertion.isInstanceOf(1, Long.class, "name3");
            fail();
        } catch (ClassCastException e) {
            // expected, but check the message
            final Object[] params = new Object[]{"name3", Long.class, Integer.class.getName()};
            final String msg = CorePlugin.Util.getString("Assertion.invalidClassMessage", params);
            assertEquals(msg, e.getMessage());
        }
    }

    /*
     * Test for void isNotEmpty(Collection)
     */
    @Test
    public void testIsNotEmptyCollection() {
    }

    /*
     * Test for void isNotEmpty(Collection, String)
     */
    @Test
    public void testIsNotEmptyCollectionString() {
    }

    /*
     * Test for void isNotEmpty(Map)
     */
    @Test
    public void testIsNotEmptyMap() {
    }

    /*
     * Test for void isNotEmpty(Map, String)
     */
    @Test
    public void testIsNotEmptyMapString() {
    }

    /*
     * Test for void contains(Collection, Object)
     */
    @Test
    public void testContainsCollectionObject() {
    }

    /*
     * Test for void contains(Collection, Object, String)
     */
    @Test
    public void testContainsCollectionObjectString() {
    }

    /*
     * Test for void containsKey(Map, Object)
     */
    @Test
    public void testContainsKeyMapObject() {
    }

    /*
     * Test for void containsKey(Map, Object, String)
     */
    @Test
    public void testContainsKeyMapObjectString() {
    }

}
