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
package com.kubling.teiid.core.json.flattener;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class IndexedPeekIteratorTest {

    IndexedPeekIterator<Integer> pIterater;
    IndexedPeekIterator<Integer> emptyIterater;

    @BeforeEach
    public void setUp() {
        pIterater =
                new IndexedPeekIterator<>(new ArrayList<>(Arrays.asList(1, 2, 3, 4)).iterator());
        emptyIterater = new IndexedPeekIterator<>(Collections.emptyIterator());
    }

    @Test
    public void testConstructorException() {
        assertThrows(NullPointerException.class, () -> new IndexedPeekIterator<Integer>(null));
    }

    @Test
    public void testIterface() {
        assertInstanceOf(Iterator.class, pIterater);
    }

    @Test
    public void testRemove() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        pIterater = new IndexedPeekIterator<>(list.iterator());
        pIterater.next();
        pIterater.remove();
        assertEquals(new ArrayList<>(Arrays.asList(2, 3, 4)), list);
    }

    @Test
    public void testHasNext() {
        assertTrue(pIterater.hasNext());
        assertFalse(emptyIterater.hasNext());
    }

    @Test
    public void testNext() {
        assertEquals(Integer.valueOf(1), pIterater.next());
        pIterater.next();
        pIterater.next();
        assertEquals(Integer.valueOf(4), pIterater.next());
    }

    @Test
    public void testNextException() {
        assertThrows(NoSuchElementException.class, () -> emptyIterater.next());
    }

    @Test
    public void testPeek() {
        assertEquals(Integer.valueOf(1), pIterater.peek());
        assertEquals(Integer.valueOf(1), pIterater.peek());
        pIterater.next();
        assertEquals(Integer.valueOf(2), pIterater.peek());

    }

    @Test
    public void testPeekException() {
        assertThrows(NoSuchElementException.class, () -> emptyIterater.peek());
    }

    @Test
    public void testRemoveException() {
        assertThrows(IllegalStateException.class, () -> {
            pIterater.peek();
            pIterater.remove();
        });
    }

    @Test
    public void testGetIndex() {
        assertEquals(-1, pIterater.getIndex());
        pIterater.peek();
        assertEquals(-1, pIterater.getIndex());
        pIterater.next();
        assertEquals(0, pIterater.getIndex());
        pIterater.peek();
        assertEquals(0, pIterater.getIndex());
        pIterater.next();
        assertEquals(1, pIterater.getIndex());
        pIterater.peek();
        assertEquals(1, pIterater.getIndex());
        pIterater.next();
        assertEquals(2, pIterater.getIndex());
        pIterater.peek();
        assertEquals(2, pIterater.getIndex());
        pIterater.next();
        assertEquals(3, pIterater.getIndex());
    }

    @Test
    public void testGetCurrent() {
        assertNull(pIterater.getCurrent());
        pIterater.peek();
        assertNull(pIterater.getCurrent());
        pIterater.next();
        assertEquals(Integer.valueOf(1), pIterater.getCurrent());
        pIterater.peek();
        assertEquals(Integer.valueOf(1), pIterater.getCurrent());
        pIterater.next();
        assertEquals(Integer.valueOf(2), pIterater.getCurrent());
        pIterater.peek();
        assertEquals(Integer.valueOf(2), pIterater.getCurrent());
        pIterater.next();
        assertEquals(Integer.valueOf(3), pIterater.getCurrent());
        pIterater.peek();
        assertEquals(Integer.valueOf(3), pIterater.getCurrent());
        pIterater.next();
        assertEquals(Integer.valueOf(4), pIterater.getCurrent());
    }

}
