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

import javax.sql.rowset.serial.SerialBlob;
import java.net.URI;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("nls" )
public class TestDataTypeManager {

    private void helpDetermineDataType(Object value, Class<?> expectedClass) {
        Class<?> actualClass = DataTypeManager.determineDataTypeClass(value);
        assertEquals(expectedClass, actualClass, "Mismatch in expected and actual MetaMatrix type class for [" + value + "]: " );
    }

    public static String[] dataTypes = {"string", "char", "boolean", "byte", "short", "integer", "long", "biginteger",
            "float", "double", "bigdecimal", "date", "time", "timestamp", "object", "blob", "clob", "xml"};

    /**
     * I - Implicitly Converted
     * C - Explicitly Converted
     * N - Cannot be converted
     * o - No conversion needed
     */
    public static char[][] conversions =
            {
                    /*                                                          Big                             */
                    /*                                       i                   d                              */
                    /*                   s                   n       b       d   e          Time o              */
                    /*                   t               s   t       i   f   o   c           s   b              */
                    /*                   r   c   b   b   h   e   L   g   l   u   i   d   t   t   j   b   c      */
                    /*                   i   h   o   y   o   g   o   i   o   b   m   a   i   a   e   l   l   x   */
                    /*                   n   a   o   t   r   e   n   n   a   l   a   t   m   m   c   o   o   m   */
                    /*                   g   r   l   e   t   r   g   t   t   e   l   e   e   p   t   b   b   l   */
                    /*                  -------------------------------------------------------------------------*/
                    /*String*/       {'O', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'I', 'N', 'I', 'C'},
                    /*char*/        {'I', 'O', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*bool*/        {'I', 'N', 'O', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*byte*/        {'I', 'N', 'C', 'O', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*short*/       {'I', 'N', 'C', 'C', 'O', 'I', 'I', 'I', 'I', 'I', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*int*/         {'I', 'N', 'C', 'C', 'C', 'O', 'I', 'I', 'C', 'I', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*long*/        {'I', 'N', 'C', 'C', 'C', 'C', 'O', 'I', 'C', 'C', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*bigint*/      {'I', 'N', 'C', 'C', 'C', 'C', 'C', 'O', 'C', 'C', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*float*/       {'I', 'N', 'C', 'C', 'C', 'C', 'C', 'C', 'O', 'I', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*double*/      {'I', 'N', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'O', 'I', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*bigdecimal*/  {'I', 'N', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'O', 'N', 'N', 'N', 'I', 'N', 'N', 'N'},
                    /*date*/        {'I', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'O', 'N', 'I', 'I', 'N', 'N', 'N'},
                    /*time*/        {'I', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'O', 'I', 'I', 'N', 'N', 'N'},
                    /*timestamp*/   {'I', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'C', 'C', 'O', 'I', 'N', 'N', 'N'},
                    /*object*/      {'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'C', 'O', 'C', 'C', 'C'},
                    /*blob*/        {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'I', 'O', 'N', 'N'},
                    /*clob*/        {'C', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'I', 'N', 'O', 'N'},
                    /*xml*/         {'C', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'I', 'N', 'N', 'O'}
            };


    // ################################## ACTUAL TESTS ################################

    @Test
    public void testTypeMappings() {
        Set<String> dataTypeNames = DataTypeManager.getAllDataTypeNames();
        for (String dataTypeName : dataTypeNames) {
            Class<?> dataTypeClass = DataTypeManager.getDataTypeClass(dataTypeName);
            assertNotNull(dataTypeClass, "Data type class was null for type " + dataTypeName);
            String dataTypeName2 = DataTypeManager.getDataTypeName(dataTypeClass);
            assertEquals(dataTypeName, dataTypeName2);
        }
    }

    @Test
    public void testCheckConversions() {
        for (int src = 0; src < dataTypes.length; src++) {
            for (int tgt = 0; tgt < dataTypes.length; tgt++) {
                char c = conversions[src][tgt];

                if (c == 'I') {
                    assertTrue(DataTypeManager.isImplicitConversion(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " should be Implicit" );
                    assertFalse(DataTypeManager.isExplicitConversion(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " should be not be Explicit" );
                    assertTrue(DataTypeManager.isTransformable(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " transform should be avaialble" );
                } else if (c == 'C') {
                    assertFalse(DataTypeManager.isImplicitConversion(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " should not be Implicit" );
                    assertTrue(DataTypeManager.isExplicitConversion(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " should be Explicit" );
                    assertTrue(DataTypeManager.isTransformable(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " transform should be avaialble" );
                } else if (c == 'O' || c == 'N') {
                    assertFalse(DataTypeManager.isImplicitConversion(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " should not be Implicit" );
                    assertFalse(DataTypeManager.isExplicitConversion(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " should not be Explicit" );
                    assertFalse(DataTypeManager.isTransformable(dataTypes[src], dataTypes[tgt]),
                            "src=" + dataTypes[src] + " target=" + dataTypes[tgt] + " No transform should be avaialble" );
                }
            }
        }
    }

    /**
     * Test determine data type for a STRING object.
     */
    @Test
    public void testDetermineDataType1() {
        helpDetermineDataType("abc", DataTypeManager.DefaultDataClasses.STRING);
    }

    /**
     * Test determine data type for a NULL object.
     */
    @Test
    public void testDetermineDataType2() {
        helpDetermineDataType(null, DataTypeManager.DefaultDataClasses.NULL);
    }

    /**
     * Test determine data type for an unknown object type - should be typed as an OBJECT.
     */
    @Test
    public void testDetermineDataType3() throws Exception {
        java.net.URL url = new URI("http://fake" ).toURL();
        helpDetermineDataType(url, DataTypeManager.DefaultDataClasses.OBJECT);
    }

    @Test
    public void testCheckAllConversions() {
        Set<String> allTypes = DataTypeManager.getAllDataTypeNames();
        for (String src : allTypes) {
            for (String tgt : allTypes) {
                boolean isImplicit = DataTypeManager.isImplicitConversion(src, tgt);
                boolean isExplicit = DataTypeManager.isExplicitConversion(src, tgt);

                if (isImplicit && isExplicit) {
                    fail("Can't be both implicit and explicit for " + src + " to " + tgt);
                }
            }
        }
    }

    @Test
    public void testTimeConversions() {
        Transform t = DataTypeManager.getTransform(DataTypeManager.DefaultDataTypes.TIMESTAMP, DataTypeManager.DefaultDataTypes.DATE);

        assertEquals(DataTypeManager.DefaultDataClasses.DATE, t.getTargetType());

        t = DataTypeManager.getTransform(DataTypeManager.DefaultDataTypes.TIME, DataTypeManager.DefaultDataTypes.TIMESTAMP);

        assertEquals(DataTypeManager.DefaultDataClasses.TIMESTAMP, t.getTargetType());
    }

    @Test
    public void testJDBCSQLTypeInfo() {

        Set<String> types = JDBCSQLTypeInfo.getMMTypeNames();

        for (String type : types) {
            assertEquals(
                    JDBCSQLTypeInfo.getSQLType(type),
                    JDBCSQLTypeInfo.getSQLTypeFromRuntimeType(DataTypeManager.getDataTypeClass(type)),
                    "Didn't get match for " + type);

            //the class names will not match the runtime types for xml, clob, blob
            if (!type.equalsIgnoreCase(DataTypeManager.DefaultDataTypes.NULL)
                    && !type.equalsIgnoreCase(DataTypeManager.DefaultDataTypes.VARBINARY)
                    && !type.equalsIgnoreCase(DataTypeManager.DefaultDataTypes.XML)
                    && !type.equalsIgnoreCase(DataTypeManager.DefaultDataTypes.CLOB)
                    && !type.equalsIgnoreCase(DataTypeManager.DefaultDataTypes.BLOB)
                    && !type.equalsIgnoreCase(DataTypeManager.DefaultDataTypes.JSON)) {
                assertEquals(
                        JDBCSQLTypeInfo.getSQLType(type),
                        JDBCSQLTypeInfo.getSQLTypeFromClass(DataTypeManager.getDataTypeClass(type).getName()),
                        "Didn't get match for " + type);
            }
        }

        assertEquals(
                Types.TIMESTAMP,
                JDBCSQLTypeInfo.getSQLTypeFromRuntimeType(DataTypeManager.DefaultDataClasses.TIMESTAMP));
        assertEquals(Types.SQLXML, JDBCSQLTypeInfo.getSQLTypeFromRuntimeType(DataTypeManager.DefaultDataClasses.XML));
        assertEquals(DataTypeManager.DefaultDataTypes.STRING, JDBCSQLTypeInfo.getTypeName(Types.CHAR));
        assertEquals(Types.CHAR, JDBCSQLTypeInfo.getSQLTypeFromRuntimeType(DataTypeManager.DefaultDataClasses.CHAR));

        assertEquals(
                Types.ARRAY,
                JDBCSQLTypeInfo.getSQLType(
                        DataTypeManager.getDataTypeName(DataTypeManager.getArrayType(DataTypeManager.DefaultDataClasses.BIG_DECIMAL))));
    }

    @Test
    public void testRuntimeTypeConversion() throws Exception {
        assertNull(DataTypeManager.convertToRuntimeType(null, true));

        assertInstanceOf(BlobType.class, DataTypeManager.convertToRuntimeType(new SerialBlob(new byte[0]), true));

        //unknown type should return as same
        Object foo = new Object();
        assertEquals(foo, DataTypeManager.convertToRuntimeType(foo, true));

        //known type should return as same
        Integer bar = 1;
        assertEquals(bar, DataTypeManager.convertToRuntimeType(bar, true));
    }

    @Test
    public void testObjectType() {
        assertEquals(DataTypeManager.DefaultDataClasses.OBJECT, DataTypeManager.getDataTypeClass("foo" ));

        assertEquals(DataTypeManager.DefaultDataTypes.OBJECT, DataTypeManager.getDataTypeName(TestDataTypeManager.class));
    }

    @Test
    public void testImplicitConversions() {
        List<String> c = new ArrayList<>();
        DataTypeManager.getImplicitConversions(DataTypeManager.DefaultDataTypes.INTEGER, c);
        assertEquals(Arrays.asList(DataTypeManager.DefaultDataTypes.LONG,
                DataTypeManager.DefaultDataTypes.BIG_INTEGER,
                DataTypeManager.DefaultDataTypes.DOUBLE,
                DataTypeManager.DefaultDataTypes.BIG_DECIMAL,
                DataTypeManager.DefaultDataTypes.STRING,
                DataTypeManager.DefaultDataTypes.OBJECT), c);
    }

    @Test
    public void testStringToXML() {
        assertThrows(TransformationException.class,
                () -> DataTypeManager.transformValue("hello", DataTypeManager.DefaultDataClasses.XML));
    }

    static class Foo {
        @Override
        public String toString() {
            return "hello";
        }
    }

    @Test
    public void testObjectToString() throws Exception {
        assertEquals("hello", DataTypeManager.transformValue(new Foo(), DataTypeManager.DefaultDataClasses.STRING));
    }

    @Test
    public void testObjectArrayToObject() throws Exception {
        Object[] value = {1, 2};
        assertArrayEquals(value, (Object[]) DataTypeManager.transformValue(value, value.getClass(), DataTypeManager.DefaultDataClasses.OBJECT));
    }

    @Test
    public void testByteArray() {
        byte[] value = {1, 2};
        assertArrayEquals(value, (byte[]) DataTypeManager.convertToRuntimeType(value, false));
        assertEquals(new BinaryType(value), DataTypeManager.convertToRuntimeType(value, true));
    }

    @Test
    public void testObjectTypeArray() throws Exception {
        Object value = new Object[]{"a", "b"};
        DataTypeManager.transformValue(value, value.getClass(), String[].class);
    }

    @Test
    public void isHashable() {
        assertFalse(DataTypeManager.isHashable(DataTypeManager.DefaultDataClasses.STRING, true, null));
        assertFalse(DataTypeManager.isHashable(String[].class, false, "Something" ));
    }

    @Test
    public void testGeographyToGeometry() throws Exception {
        assertInstanceOf(GeometryType.class, DataTypeManager.transformValue(new GeographyType(new byte[0]), DataTypeManager.DefaultDataClasses.GEOMETRY));
    }

}
