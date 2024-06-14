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

import com.kubling.teiid.core.CorePlugin;
import com.kubling.teiid.core.types.*;
import com.kubling.teiid.core.types.DataTypeManager.DefaultDataClasses;
import com.kubling.teiid.core.types.DataTypeManager.DefaultDataTypes;
import com.kubling.teiid.core.util.TimestampWithTimezone;
import com.kubling.teiid.query.unittest.TimestampUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransforms {

    private static void helpTestTransform(Object value, Object expectedValue) throws TransformationException {
        Transform transform = DataTypeManager.getTransform(value.getClass(), expectedValue.getClass());
        Object result = transform.transform(value, expectedValue.getClass());
        assertEquals(expectedValue, result);
    }

    private static void validateTransform(String src, Object value, String target, Object expectedValue) 
            throws TransformationException {
        try {
            Transform transform = DataTypeManager.getTransform(DataTypeManager.getDataTypeClass(src), expectedValue.getClass());
            Object result = transform.transform(value, expectedValue.getClass());
            assertTrue(expectedValue.getClass().isAssignableFrom(result.getClass()));
            assertFalse(isException(DataTypeManager.getDataTypeName(value.getClass()), target,value));
        } catch (TransformationException e) {
            if (!isException(DataTypeManager.getDataTypeName(value.getClass()), target,value)) {
                throw e;
            }
        }
    }

    private static void helpTransformException(Object value, Class<?> target, String msg) {
        try {
            Transform transform = DataTypeManager.getTransform(value.getClass(), target);
            transform.transform(value, target);
            fail("Expected to get an exception during the transformation");
        } catch (TransformationException e) {
            if (msg != null) {
                assertEquals(msg, e.getMessage());
            }
        }
    }

    @Test public void testBigDecimalToBigInteger_Defect16875() throws TransformationException {
        helpTestTransform(new BigDecimal("0.5867"), new BigInteger("0"));
    }

    @Test public void testString2Boolean() throws TransformationException {
        helpTestTransform(new String("1"), Boolean.TRUE);
        helpTestTransform(new String("0"), Boolean.FALSE);
        helpTestTransform(new String("true"), Boolean.TRUE);
        helpTestTransform(new String("false"), Boolean.FALSE);
        helpTestTransform(new String("foo"), Boolean.TRUE);
    }

    @Test public void testByte2Boolean() throws TransformationException {
        helpTestTransform(Byte.valueOf((byte)1), Boolean.TRUE);
        helpTestTransform(Byte.valueOf((byte)0), Boolean.FALSE);
        helpTestTransform(Byte.valueOf((byte)12), Boolean.TRUE);
    }

    @Test public void testShort2Boolean() throws TransformationException {
        helpTestTransform(Short.valueOf((short)1), Boolean.TRUE);
        helpTestTransform(Short.valueOf((short)0), Boolean.FALSE);
        helpTestTransform(Short.valueOf((short)12), Boolean.TRUE);
    }

    @Test public void testInteger2Boolean() throws TransformationException {
        helpTestTransform(Integer.valueOf(1), Boolean.TRUE);
        helpTestTransform(Integer.valueOf(0), Boolean.FALSE);
        helpTestTransform(Integer.valueOf(12), Boolean.TRUE);
    }

    @Test public void testLong2Boolean() throws TransformationException {
        helpTestTransform(Long.valueOf(1), Boolean.TRUE);
        helpTestTransform(Long.valueOf(0), Boolean.FALSE);
        helpTestTransform(Long.valueOf(12), Boolean.TRUE);
    }

    @Test public void testBigInteger2Boolean() throws TransformationException {
        helpTestTransform(new BigInteger("1"), Boolean.TRUE);
        helpTestTransform(new BigInteger("0"), Boolean.FALSE);
        helpTestTransform(new BigInteger("12"), Boolean.TRUE);
    }

    @Test public void testBigDecimal2Boolean() throws TransformationException {
        helpTestTransform(new BigDecimal("1"), Boolean.TRUE);
        helpTestTransform(new BigDecimal("0"), Boolean.FALSE);
        helpTestTransform(new BigDecimal("0.00"), Boolean.FALSE);
    }

    static Object[][] testData = {
        /*string-0*/  {"1", "0", "123"}, //$NON-NLS-3$
        /*char-1*/    {Character.valueOf('1'), Character.valueOf('0'), Character.valueOf('1')},
        /*boolean-2*/ {Boolean.TRUE, Boolean.FALSE, Boolean.FALSE},
        /*byte-3*/    {Byte.valueOf((byte)1), Byte.valueOf((byte)0), Byte.valueOf((byte)123)},
        /*short-4*/   {Short.valueOf((short)1), Short.valueOf((short)0), Short.valueOf((short)123)},
        /*integer-5*/ {Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(123)},
        /*long-6*/    {Long.valueOf(1), Long.valueOf(0), Long.valueOf(123)},
        /*biginteger-7*/ {new BigInteger("1"), new BigInteger("0"), new BigInteger("123")}, //$NON-NLS-3$
        /*float-8*/   {Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(123.0f)},
        /*double-9*/  {Double.valueOf(1.0d), Double.valueOf(0.0d), Double.valueOf(123.0d)},
        /*bigdecimal-10*/{new BigDecimal("1"), new BigDecimal("0"), new BigDecimal("123")}, //$NON-NLS-3$
        /*date-11*/    {new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())},
        /*time-12*/    {new Time(System.currentTimeMillis()), new Time(System.currentTimeMillis()), new Time(System.currentTimeMillis())},
        /*timestamp-13*/{new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis())},
        /*object-14*/  {null, null, null},
        /*blob-15*/    {null, null, null},
        /*clob-16*/    {new ClobType(ClobImpl.createClob("ClobData".toCharArray())), new ClobType(ClobImpl.createClob("0".toCharArray())), new ClobType(ClobImpl.createClob("123".toCharArray()))}, //$NON-NLS-3$
        /*xml-17*/     {new XMLType(new SQLXMLImpl("<foo>bar</foo>")), new XMLType(new SQLXMLImpl("<foo>bar</foo>")), new XMLType(new SQLXMLImpl("<foo>bar</foo>"))}, //$NON-NLS-3$
     };

    private String[] dataTypes = TestDataTypeManager.dataTypes;
    private char[][] conversions = TestDataTypeManager.conversions;
    private static boolean isException(String src, String tgt, Object source) {
        return (src.equals(DefaultDataTypes.STRING) && tgt.equals(DefaultDataTypes.XML))
            || (src.equals(DefaultDataTypes.STRING) && tgt.equals(DefaultDataTypes.TIME))
            || (src.equals(DefaultDataTypes.STRING) && tgt.equals(DefaultDataTypes.TIMESTAMP))
            || (src.equals(DefaultDataTypes.STRING) && tgt.equals(DefaultDataTypes.DATE))
            || (src.equals(DefaultDataTypes.CLOB) && tgt.equals(DefaultDataTypes.XML));
    }

    @Test public void testAllConversions() throws TransformationException {
        for (int src = 0; src < dataTypes.length; src++) {
            for (int tgt =0; tgt < dataTypes.length; tgt++) {
                char c = conversions[src][tgt];

                if (c == 'I' || c == 'C') {
                    Object[] srcdata = testData[src];
                    Object[] tgtdata = testData[tgt];
                    for (int i=0; i<tgtdata.length; i++) {
                        if (tgtdata[i] != null && srcdata[i] != null) {
                            validateTransform(dataTypes[src], srcdata[i], dataTypes[tgt],tgtdata[i]);
                        }
                    }
                }
            }
        }
    }

    @Test public void testAllConversionsAsObject() throws TransformationException {
        for (int src = 0; src < dataTypes.length; src++) {
            for (int tgt =0; tgt < dataTypes.length; tgt++) {
                char c = conversions[src][tgt];

                if (c == 'I' || c == 'C') {
                    Object[] srcdata = testData[src];
                    Object[] tgtdata = testData[tgt];
                    for (int i=0; i<tgtdata.length; i++) {
                        if (tgtdata[i] != null && srcdata[i] != null) {
                            validateTransform(DefaultDataTypes.OBJECT, srcdata[i], dataTypes[tgt],tgtdata[i]);
                        }
                    }
                }
            }
        }
    }

    @Test public void testObjectToAnyTransformFailure() {
        Transform transform = DataTypeManager.getTransform(DefaultDataClasses.OBJECT, DefaultDataClasses.TIME);
        try {
            transform.transform("1", DefaultDataClasses.TIME);
            fail("expected exception");
        } catch (TransformationException e) {
            assertEquals("TEIID10076 Invalid conversion from type class java.lang.Object with " +
                    "value '1' to type class java.sql.Time", e.getMessage());
        }
    }

    @Test public void testSQLXMLToStringTransform() throws Exception {
        StringBuffer xml = new StringBuffer();
        int iters = DataTypeManager.MAX_STRING_LENGTH/10;
        for (int i = 0; i < iters; i++) {
            if (i < iters/2) {
                xml.append("<opentag>1");
            } else {
                xml.append("</opentag>");
            }
        }

        String expected = "";
        expected += xml.substring(0, DataTypeManager.MAX_STRING_LENGTH - expected.length());

        helpTestTransform(new StringToSQLXMLTransform().transformDirect(xml.toString()), expected);
    }

    @Test public void testStringToTimestampOutOfRange() throws Exception {
        helpTransformException("2005-13-01 11:13:01", DefaultDataClasses.TIMESTAMP, null);
    }

    @Test public void testStringToTimeTimestampWithWS() throws Exception {
        helpTestTransform(" 2005-12-01 11:13:01 ",
                TimestampUtil.createTimestamp(105, 11, 1, 11, 13, 1, 0));
    }

    @Test public void testStringToTimestampFails() throws Exception {
        helpTransformException("2005-12-01 11:88:60", Timestamp.class,
                "TEIID10060 The string representation '2005-12-01 11:88:60' of a Timestamp value is not valid.");
    }

    @Test public void testStringToTimestampDSTTransition() throws Exception {
        //use a DST time zone
        TimestampWithTimezone.resetCalendar(TimeZone.getTimeZone("America/New_York"));
        try {
            helpTestTransform("2016-03-13 02:00:00",
                    TimestampUtil.createTimestamp(116, 2, 13, 3, 0, 0, 0));
        } finally {
            TimestampWithTimezone.resetCalendar(null);
        }
    }

    @Test public void testStringToLongWithWS() throws Exception {
        helpTestTransform(" 1 ", Long.valueOf(1));
    }

    @Test public void testEngineeringNotationFloatToBigInteger() throws Exception {
        helpTestTransform(Float.MIN_VALUE, new BigDecimal(Float.MIN_VALUE).toBigInteger());
    }

    @Test public void testRangeCheck() throws Exception {
        helpTransformException(300, DefaultDataClasses.BYTE, "TEIID10058 The Integer value '300' is outside the of range for Byte");
    }

    @Test public void testRangeCheck1() throws Exception {
        Double value = Double.valueOf("1E11");//$NON-NLS-1$
        helpTransformException(value, DefaultDataClasses.INTEGER,
                CorePlugin.Util.gs(CorePlugin.Event.TEIID10058, value,
                        Double.class.getSimpleName(), Integer.class.getSimpleName()));
    }

    @Test public void testPrimitiveArrayConversion() throws Exception {
        Object val = DataTypeManager.transformValue(new long[] {1}, DefaultDataClasses.OBJECT, Long[].class);
        assertEquals(new ArrayImpl(new Long[]{Long.valueOf(1)}), val);
    }

}
