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

package com.kubling.teiid.core.types;

import com.kubling.teiid.core.util.UnitTestUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("nls")
public class TestXMLValue {

    @Test
    public void testXMLValue() throws Exception {
        String testString = "<foo>this is an xml value test</foo>";
        SQLXMLImpl xml = new SQLXMLImpl(testString);

        XMLType xv = new XMLType(xml);
        assertEquals(testString, xv.getString());
    }


    @Test
    public void testXMLValuePersistence() throws Exception {
        String testString = "<foo>this is an xml value test</foo>";
        SQLXMLImpl xml = new SQLXMLImpl(testString);

        XMLType xv = new XMLType(xml);
        String key = xv.getReferenceStreamId();

        // now force to serialize
        XMLType read = UnitTestUtil.helpSerialize(xv);

        // make sure we have kept the reference stream id
        assertEquals(key, read.getReferenceStreamId());

        // and lost the original object
        assertNull(read.getReference());
    }

    @Test
    public void testReferencePersistence() throws Exception {
        String testString = "<foo>this is an xml value test</foo>";
        SQLXMLImpl xml = new SQLXMLImpl(testString);

        XMLType xv = new XMLType(xml);
        xv.setReferenceStreamId(null);

        // now force to serialize
        XMLType read = UnitTestUtil.helpSerialize(xv);

        assertEquals(testString, read.getString());
    }


    @Test
    public void testLength() throws Exception {
        String testString = "<foo>this is an xml value test</foo>";
        SQLXMLImpl xml = new SQLXMLImpl(testString);

        XMLType xv = new XMLType(xml);
        assertEquals(36, xv.length());

        xml = new SQLXMLImpl(new InputStreamFactory() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream("<bar/>".getBytes(Streamable.CHARSET));
            }
        });

        xv = new XMLType(xml);
        try {
            xv.length();
            fail();
        } catch (SQLException e) {
            // Ignored
        }
    }
}
