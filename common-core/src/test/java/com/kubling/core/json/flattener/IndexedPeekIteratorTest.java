/*
 *
 * Copyright 2015 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.kubling.core.json.flattener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class IndexedPeekIteratorTest {

    IndexedPeekIterator<Integer> pIterator;
    IndexedPeekIterator<Integer> emptyIterator;

    @BeforeEach
    public void setUp() {
        pIterator =
                new IndexedPeekIterator<>(new ArrayList<>(Arrays.asList(1, 2, 3, 4)).iterator());
        emptyIterator = new IndexedPeekIterator<>(Collections.emptyIterator());
    }

    @Test
    public void testConstructorException() {
        assertThrows(NullPointerException.class, () -> new IndexedPeekIterator<Integer>(null));
    }

    @Test
    public void testInterface() {
        assertInstanceOf(Iterator.class, pIterator);
    }

    @Test
    public void testRemove() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        pIterator = new IndexedPeekIterator<>(list.iterator());
        pIterator.next();
        pIterator.remove();
        assertEquals(new ArrayList<>(Arrays.asList(2, 3, 4)), list);
    }

    @Test
    public void testHasNext() {
        assertTrue(pIterator.hasNext());
        assertFalse(emptyIterator.hasNext());
    }

    @Test
    public void testNext() {
        assertEquals(Integer.valueOf(1), pIterator.next());
        pIterator.next();
        pIterator.next();
        assertEquals(Integer.valueOf(4), pIterator.next());
    }

    @Test
    public void testNextException() {
        assertThrows(NoSuchElementException.class, () -> emptyIterator.next());
    }

    @Test
    public void testPeek() {
        assertEquals(Integer.valueOf(1), pIterator.peek());
        assertEquals(Integer.valueOf(1), pIterator.peek());
        pIterator.next();
        assertEquals(Integer.valueOf(2), pIterator.peek());

    }

    @Test
    public void testPeekException() {
        assertThrows(NoSuchElementException.class, () -> emptyIterator.peek());
    }

    @Test
    public void testRemoveException() {
        assertThrows(IllegalStateException.class, () -> {
            pIterator.peek();
            pIterator.remove();
        });
    }

    @Test
    public void testGetIndex() {
        assertEquals(-1, pIterator.getIndex());
        pIterator.peek();
        assertEquals(-1, pIterator.getIndex());
        pIterator.next();
        assertEquals(0, pIterator.getIndex());
        pIterator.peek();
        assertEquals(0, pIterator.getIndex());
        pIterator.next();
        assertEquals(1, pIterator.getIndex());
        pIterator.peek();
        assertEquals(1, pIterator.getIndex());
        pIterator.next();
        assertEquals(2, pIterator.getIndex());
        pIterator.peek();
        assertEquals(2, pIterator.getIndex());
        pIterator.next();
        assertEquals(3, pIterator.getIndex());
    }

    @Test
    public void testGetCurrent() {
        assertNull(pIterator.getCurrent());
        pIterator.peek();
        assertNull(pIterator.getCurrent());
        pIterator.next();
        assertEquals(Integer.valueOf(1), pIterator.getCurrent());
        pIterator.peek();
        assertEquals(Integer.valueOf(1), pIterator.getCurrent());
        pIterator.next();
        assertEquals(Integer.valueOf(2), pIterator.getCurrent());
        pIterator.peek();
        assertEquals(Integer.valueOf(2), pIterator.getCurrent());
        pIterator.next();
        assertEquals(Integer.valueOf(3), pIterator.getCurrent());
        pIterator.peek();
        assertEquals(Integer.valueOf(3), pIterator.getCurrent());
        pIterator.next();
        assertEquals(Integer.valueOf(4), pIterator.getCurrent());
    }

}
