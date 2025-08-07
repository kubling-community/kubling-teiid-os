package com.kubling.teiid.core.types;

import com.kubling.teiid.core.TeiidRuntimeException;
import com.kubling.teiid.core.util.UnitTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJsonType {

    @Test
    public void testJsonEquals() {
        String json1 = "{ \"a\": 1, \"b\": 2 }";
        String json2 = "{ \"b\": 2, \"a\": 1 }";

        JsonType jt1 = new JsonType(new ClobImpl(json1));
        JsonType jt2 = new JsonType(new ClobImpl(json2));

        assertEquals(jt1, jt2);
    }

    @Test
    public void testJsonCompare() throws Exception {
        String json1 = "{ \"a\": 1, \"b\": 2 }";
        String json2 = "{ \"b\": 2, \"a\": 1 }";

        JsonType jt1 = new JsonType(new ClobImpl(json1));
        JsonType jt2 = new JsonType(new ClobImpl(json2));

        assertEquals(0, jt1.compareTo(jt2));
    }

    @Test
    public void testJsonHashCode() throws Exception {
        String json1 = "{ \"a\": 1, \"b\": 2 }";
        String json2 = "{ \"b\": 2, \"a\": 1 }";

        JsonType jt1 = new JsonType(new ClobImpl(json1));
        JsonType jt2 = new JsonType(new ClobImpl(json2));

        assertEquals(jt1.hashCode(), jt2.hashCode());
    }

    @Test
    public void testJsonTypeSerialization() throws Exception {
        String json = "{\"z\":9,\"a\":1}";
        JsonType jt = new JsonType(new ClobImpl(json));

        jt.setReferenceStreamId(null);

        JsonType read = UnitTestUtil.helpSerialize(jt);

        assertEquals(json.length(), read.length());
        assertEquals(json, read.getSubString(1, json.length()));

        assertEquals(jt, read);
        assertEquals(0, jt.compareTo(read));
        assertEquals(jt.hashCode(), read.hashCode());
    }

    @Test
    public void testInvalidJson() {
        JsonType jt = new JsonType(new ClobImpl("{ invalid json }"));

        assertThrows(TeiidRuntimeException.class, jt::hashCode);
    }

    @Test
    public void testEqualsDifferentType() {
        JsonType jt = new JsonType(new ClobImpl("{\"a\": 1}"));
        assertNotEquals(new Object(), jt);
    }

    @Test
    public void testCompareWithNonJsonType() throws Exception {
        JsonType jt = new JsonType(new ClobImpl("{\"a\": 1}"));
        ClobType ct = new ClobType(new ClobImpl("other text"));

        assertNotEquals(0, jt.compareTo(ct)); // falls back to default compare
    }

}
