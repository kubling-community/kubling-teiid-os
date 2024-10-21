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

import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * XML StreamSource Translator.
 */
public class TestXMLStreamSourceTranslator {

    private static final String sourceXML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<Books:bookCollection xmlns:Books=\"http://www.metamatrix.com/XMLSchema/DataSets/Books\">\r\n" +
                    "   <book isbn=\"0-7356-0877-6\">\r\n" +
                    "      <title>After the Gold Rush</title>\r\n" +
                    "      <subtitle>Creating a True Profession of Software Engineering</subtitle>\r\n" +
                    "      <edition>1</edition>\r\n" +
                    "      <authors>\r\n" +
                    "         <author>McConnell</author>\r\n" +
                    "      </authors>\r\n" +
                    "      <publishingInformation>\r\n" +
                    "         <publisher>Microsoft Press</publisher>\r\n" +
                    "         <publishDate>1999</publishDate>\r\n" +
                    "      </publishingInformation>\r\n" +
                    "   </book>\r\n" +
                    "   <book isbn=\"1-55615-484-4\">\r\n" +
                    "      <title>Code Complete</title>\r\n" +
                    "      <subtitle>A Practical Handbook of Software Construction</subtitle>\r\n" +
                    "      <edition>1</edition>\r\n" +
                    "      <authors>\r\n" +
                    "         <author>McConnell</author>\r\n" +
                    "      </authors>\r\n" +
                    "      <publishingInformation>\r\n" +
                    "         <publisher>Microsoft Press</publisher>\r\n" +
                    "         <publishDate>1993</publishDate>\r\n" +
                    "      </publishingInformation>\r\n" +
                    "   </book>\r\n" +
                    "   <book isbn=\"1-556-15900-5\">\r\n" +
                    "      <title>Rapid Development</title>\r\n" +
                    "      <subtitle>Taming Wild Software Schedules</subtitle>\r\n" +
                    "      <edition>1</edition>\r\n" +
                    "      <authors>\r\n" +
                    "         <author>McConnell</author>\r\n" +
                    "      </authors>\r\n" +
                    "      <publishingInformation>\r\n" +
                    "         <publisher>Microsoft Press</publisher>\r\n" +
                    "         <publishDate>1996</publishDate>\r\n" +
                    "      </publishingInformation>\r\n" +
                    "   </book>\r\n" +
                    "</Books:bookCollection>";

    @Test
    public void testStreamSourceWithStream() throws Exception {
        StandardXMLTranslator translator = new StandardXMLTranslator(new StreamSource(new StringReader(sourceXML)));
        compareDocuments(sourceXML, translator.getString());
    }

    private void compareDocuments(String expectedDoc, String actualDoc) {
        StringTokenizer tokens1 = new StringTokenizer(expectedDoc, "\r\n");
        StringTokenizer tokens2 = new StringTokenizer(actualDoc, "\n");
        while (tokens1.hasMoreTokens()) {
            String token1 = tokens1.nextToken().trim();
            if (!tokens2.hasMoreTokens()) {
                fail("XML doc mismatch: expected=" + token1 + "\nactual=none");
            }
            String token2 = tokens2.nextToken().trim();
            assertEquals(token1, token2);
        }
        if (tokens2.hasMoreTokens()) {
            fail("XML doc mismatch: expected=none\nactual=" + tokens2.nextToken().trim());
        }
    }
}
