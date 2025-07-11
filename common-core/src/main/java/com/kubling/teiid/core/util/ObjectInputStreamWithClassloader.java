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

import com.kubling.teiid.core.*;
import com.kubling.teiid.core.types.DataTypeManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class ObjectInputStreamWithClassloader extends
        ObjectInputStream {

    private final ClassLoader cl;

    private static final Set<String> safeClasses = new HashSet<>();

    static {

        safeClasses.add(java.lang.Number.class.getName());
        safeClasses.add(java.util.Properties.class.getName());
        safeClasses.add(java.util.Hashtable.class.getName());
        safeClasses.add("java.util.Arrays$ArrayList");

        safeClasses.add("sun.util.calendar.ZoneInfo");
        safeClasses.add(java.util.TimeZone.class.getName());

        safeClasses.add("com.kubling.teiid.net.socket.Handshake");
        safeClasses.add("com.kubling.teiid.net.socket.Message");
        safeClasses.add("com.kubling.teiid.net.socket.ServiceInvocationStruct");
        safeClasses.add("com.kubling.teiid.client.security.ILogon");
        safeClasses.add("com.kubling.teiid.client.security.LogonResult");
        safeClasses.add("com.kubling.teiid.client.security.SessionToken");
        safeClasses.add("com.kubling.teiid.client.DQP");
        safeClasses.add("com.kubling.teiid.client.RequestMessage");
        safeClasses.add("com.kubling.teiid.client.ResultsMessage");
        safeClasses.add("com.kubling.teiid.client.security.LogonException");
        safeClasses.add("com.kubling.teiid.client.security.TeiidSecurityException");
        safeClasses.add("com.kubling.teiid.client.security.InvalidSessionException");
        safeClasses.add(StackTraceElement.class.getName());

        safeClasses.add("com.kubling.teiid.client.metadata.MetadataResult");
        safeClasses.add("com.kubling.teiid.client.metadata.ParameterInfo");
        safeClasses.add("com.kubling.teiid.client.metadata.ResultsMetadataConstants");

        safeClasses.add("com.kubling.teiid.client.xa.XidImpl");
        safeClasses.add("com.kubling.teiid.client.xa.XATransactionException");

        safeClasses.addAll(
                DataTypeManager.getAllDataTypeClasses().stream()
                        .map(Class::getName)
                        .collect(Collectors.toSet())
        );

        safeClasses.add(com.kubling.teiid.core.types.ClobType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.BaseClobType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.ClobImpl.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.BlobType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.BlobImpl.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.BaseLob.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.XMLType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.SQLXMLImpl.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.Streamable.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.ArrayImpl.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.BinaryType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.NullType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.GeometryType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.GeographyType.class.getName());
        safeClasses.add(com.kubling.teiid.core.types.AbstractGeospatialType.class.getName());

        safeClasses.add("com.kubling.teiid.client.util.ExceptionHolder");
        safeClasses.add(TeiidRuntimeException.class.getName());
        safeClasses.add(TeiidComponentException.class.getName());
        safeClasses.add(TeiidException.class.getName());
        safeClasses.add(TeiidProcessingException.class.getName());
        safeClasses.add(ComponentNotFoundException.class.getName());

        safeClasses.add(ComponentNotFoundException.class.getName());

    }

    public ObjectInputStreamWithClassloader(InputStream in,
                                            ClassLoader cl) throws IOException {
        super(in);
        this.cl = cl;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {

        //see java bug id 6434149
        try {
            checkClass(desc.getName());
            return Class.forName(desc.getName(), false, cl);
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }

    public static void checkClass(String name) throws ClassNotFoundException {

        if (name.startsWith("[")) {

            if ("BCDFIJSZ".indexOf(name.charAt(1)) >= 0) {
                return; // byte[], char[], double[], etc.
            }
            String elementType = name;
            while (elementType.startsWith("[")) {
                elementType = elementType.substring(1);
            }
            if (elementType.startsWith("L") && elementType.endsWith(";")) {
                elementType = elementType.substring(1, elementType.length() - 1);
            }
            checkClass(elementType);
            return;
        }

        // Allow lists, sets, and maps if they are from trusted packages (raw)
        if (isJavaUtilCollection(name)) {
            return;
        }

        if (!safeClasses.contains(name)) {
            throw new ClassNotFoundException("Blocked class: " + name);
        }
    }

    private static boolean isJavaUtilCollection(String name) {
        return name.equals("java.util.ArrayList")
                || name.equals("java.util.LinkedList")
                || name.equals("java.util.HashSet")
                || name.equals("java.util.LinkedHashSet")
                || name.equals("java.util.TreeSet")
                || name.equals("java.util.HashMap")
                || name.equals("java.util.LinkedHashMap")
                || name.equals("java.util.TreeMap");
    }
}