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

package com.kubling.teiid.core.types.basic;

import com.kubling.teiid.core.types.TransformationException;
import com.kubling.teiid.core.types.XMLType;
import org.junit.jupiter.api.Test;

import java.sql.SQLXML;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SuppressWarnings("nls")
public class TestStringToXmlTransform {

    @Test
    public void testGoodXML() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><customer>\n" +
                "<name>ABC</name>" +
                "<age>32</age>" +
                "</customer>";

        StringToSQLXMLTransform transform = new StringToSQLXMLTransform();

        SQLXML xmlValue = (SQLXML) transform.transformDirect(xml);
        assertEquals(xml.replaceAll("[\r]", ""), xmlValue.getString().replaceAll("[\r]", ""));
    }

    @Test
    public void testGoodElement() throws Exception {
        String xml = "<customer>\n" +
                "<name>ABC</name>" +
                "<age>32</age>" +
                "</customer>";

        StringToSQLXMLTransform transform = new StringToSQLXMLTransform();

        XMLType xmlValue = (XMLType) transform.transformDirect(xml);
        assertEquals(xml.replaceAll("[\r]", ""), xmlValue.getString().replaceAll("[\r]", ""));
        assertEquals(XMLType.Type.ELEMENT, xmlValue.getType());
    }

    @Test
    public void testBadXML() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><customer>\n" +
                "<name>ABC</name>" +
                "<age>32</age>" +
                "<customer>";

        StringToSQLXMLTransform transform = new StringToSQLXMLTransform();

        assertThrows(TransformationException.class, () -> transform.transformDirect(xml));
    }

}
