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

package com.kubling.teiid.client;

import com.kubling.teiid.core.TeiidProcessingException;
import com.kubling.teiid.core.util.UnitTestUtil;
import com.kubling.teiid.netty.handler.codec.serialization.CompactObjectInputStream;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("nls")
public class TestRequestMessage {

    public static RequestMessage example() {
        RequestMessage message = new RequestMessage();
        message.setStatementType(RequestMessage.StatementType.CALLABLE);
        message.setFetchSize(100);
        List<Integer> params = new ArrayList<Integer>();
        params.add(Integer.valueOf(100));
        params.add(Integer.valueOf(200));
        params.add(Integer.valueOf(300));
        params.add(Integer.valueOf(400));
        message.setParameterValues(params);

        message.setPartialResults(true);
        message.setStyleSheet("myStyleSheet");
        message.setExecutionPayload("myExecutionPayload");
        try {
            message.setTxnAutoWrapMode(RequestMessage.TXN_WRAP_ON);
        } catch (TeiidProcessingException e) {
            throw new RuntimeException(e);
        }

        message.setShowPlan(RequestMessage.ShowPlan.ON);
        message.setRowLimit(1313);
        message.setReturnAutoGeneratedKeys(true);
        message.setDelaySerialization(true);
        message.setSpanContext("foo");
        return message;
    }

    @Test
    public void testSerialize() throws Exception {
        RequestMessage copy = UnitTestUtil.helpSerialize(example());

        assertTrue(copy.isCallableStatement());
        assertEquals(100, copy.getFetchSize());
        assertNotNull(copy.getParameterValues());
        assertEquals(4, copy.getParameterValues().size());
        assertEquals(Integer.valueOf(100), copy.getParameterValues().get(0));
        assertEquals(Integer.valueOf(200), copy.getParameterValues().get(1));
        assertEquals(Integer.valueOf(300), copy.getParameterValues().get(2));
        assertEquals(Integer.valueOf(400), copy.getParameterValues().get(3));

        assertFalse(copy.isPreparedStatement());
        assertEquals("myStyleSheet", copy.getStyleSheet());
        assertEquals("myExecutionPayload", copy.getExecutionPayload());
        assertEquals(RequestMessage.TXN_WRAP_ON, copy.getTxnAutoWrapMode());
        assertEquals(RequestMessage.ShowPlan.ON, copy.getShowPlan());
        assertEquals(1313, copy.getRowLimit());
        assertTrue(copy.isReturnAutoGeneratedKeys());
        assertTrue(copy.isDelaySerialization());
        assertEquals("foo", copy.getSpanContext());
    }

    @Test public void testInvalidTxnAutoWrap() {
        RequestMessage rm = new RequestMessage();
        try {
            rm.setTxnAutoWrapMode("foo");
            fail("exception expected");
        } catch (TeiidProcessingException e) {
            assertEquals("TEIID20000 'FOO' is an invalid transaction autowrap mode.", e.getMessage());
        }
    }

    @Test public void test83() throws FileNotFoundException, IOException, ClassNotFoundException {
        CompactObjectInputStream ois =
                new CompactObjectInputStream(new FileInputStream(UnitTestUtil.getTestDataFile("req.ser")),
                        RequestMessage.class.getClassLoader());
        RequestMessage rm = (RequestMessage) ois.readObject();
        ois.close();
        assertFalse(rm.isReturnAutoGeneratedKeys());
        assertFalse(rm.isDelaySerialization());
    }

}