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

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("nls")
public class TestReaderInputStream {

    @Test
    public void testUTF8() throws Exception {
        FileInputStream fis = new FileInputStream(UnitTestUtil.getTestDataFile("legal_notice.xml"));
        ReaderInputStream ris = new ReaderInputStream(new FileReader(UnitTestUtil.getTestDataFile("legal_notice.xml")), StandardCharsets.UTF_8);

        int value;
        do {
            value = fis.read();
            assertEquals(value, ris.read());
        } while (value != -1);
    }

    @Test
    public void testUTF16() throws Exception {
        String actual = "!?abc";
        ReaderInputStream ris = new ReaderInputStream(new StringReader(actual), StandardCharsets.UTF_16.newEncoder(), 2);
        byte[] result = ObjectConverterUtil.convertToByteArray(ris);
        String resultString = new String(result, StandardCharsets.UTF_16);
        assertEquals(resultString, actual);
    }

    @Test
    public void testASCII() throws Exception {
        String actual = "!?abc";
        ReaderInputStream ris = new ReaderInputStream(new StringReader(actual), StandardCharsets.US_ASCII.newEncoder(), 1);
        byte[] result = ObjectConverterUtil.convertToByteArray(ris);
        String resultString = new String(result, StandardCharsets.US_ASCII);
        assertEquals(resultString, actual);
    }

    @Test
    public void testASCIIError() {
        String actual = "!?abc\uffffafs";
        Charset cs = StandardCharsets.US_ASCII;
        ReaderInputStream ris = new ReaderInputStream(new StringReader(actual), cs.newEncoder(), 1);
        assertThrows(IOException.class, () -> ObjectConverterUtil.convertToByteArray(ris));
    }

}
