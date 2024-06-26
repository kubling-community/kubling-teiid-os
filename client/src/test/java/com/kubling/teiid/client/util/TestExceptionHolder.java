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

package com.kubling.teiid.client.util;

import com.kubling.teiid.client.SourceWarning;
import com.kubling.teiid.core.TeiidException;
import com.kubling.teiid.core.TeiidProcessingException;
import com.kubling.teiid.core.TeiidRuntimeException;
import com.kubling.teiid.core.util.ReflectionHelper;
import com.kubling.teiid.core.util.UnitTestUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("nls")
public class TestExceptionHolder {

    private static final class CustomStream extends ObjectOutputStream {
        final AtomicInteger count = new AtomicInteger();

        private CustomStream(OutputStream out)
                throws IOException {
            super(out);
            this.enableReplaceObject(true);
        }

        @Override
        protected Object replaceObject(Object obj) throws IOException {
            if (obj instanceof ExceptionHolder) {
                count.incrementAndGet();
            }
            return super.replaceObject(obj);
        }
    }

    @SuppressWarnings("all")
    public static class BadException extends TeiidProcessingException {
        private Object obj;
        public BadException(String msg) {super(msg);}
        public BadException(Object obj) {
            this.obj = obj;
        }
    }

    @Test public void testDeserializationUnknownException() throws Exception {
        ClassLoader cl = new URLClassLoader(new URL[] {UnitTestUtil.getTestDataFile("test.jar").toURI().toURL()});
        Object obj = ReflectionHelper.create("test.Test", null, cl);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new ExceptionHolder(new BadException(obj)));
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ExceptionHolder holder = (ExceptionHolder)ois.readObject();
        assertTrue(holder.getException() instanceof BadException);
        assertEquals("Remote com.kubling.teiid.client.util.TestExceptionHolder$BadException: null",
                holder.getException().getMessage());
    }


    @SuppressWarnings("all")
    public static class BadException2 extends TeiidProcessingException {
        public BadException2(String msg) {
            super(msg);
        }
        public BadException2(Throwable e, String msg) {
            super(e, msg);
        }
    }

    @Test public void testDeserializationUnknownChildException() throws Exception {
        ClassLoader cl = new URLClassLoader(new URL[] {UnitTestUtil.getTestDataFile("test.jar").toURI().toURL()});
        Exception obj = (Exception)ReflectionHelper.create("test.UnknownException", null, cl);
        obj.initCause(new SQLException("something bad happended"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new ExceptionHolder(new BadException2(obj, "I have foreign exception embedded in me")));
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ExceptionHolder holder = (ExceptionHolder)ois.readObject();
        Throwable e = holder.getException();
        assertTrue(e instanceof BadException2);
        assertEquals(e.getMessage(),
                "Remote com.kubling.teiid.client.util.TestExceptionHolder$BadException2: I have foreign exception embedded in me");

        e = e.getCause();
        assertTrue(e instanceof TeiidRuntimeException);

        e = e.getCause();
        assertTrue(e instanceof SQLException);

        assertEquals("Remote java.sql.SQLException: something bad happended", e.getMessage());
    }

    @Test public void testSQLExceptionChain() throws Exception {
        ClassLoader cl = new URLClassLoader(new URL[] {UnitTestUtil.getTestDataFile("test.jar").toURI().toURL()});
        Exception obj = (Exception)ReflectionHelper.create("test.UnknownException", null, cl);
        SQLException se = new SQLException("something bad happened", obj);

        SQLException next = se;
        for (int i = 0; i < 10; i++) {
            SQLException se1 = new SQLException("something else bad happened", obj);
            next.setNextException(se1);
            next = se1;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CustomStream oos = new CustomStream(baos);
        oos.writeObject(new ExceptionHolder(se, false));
        oos.flush();

        assertEquals(22, oos.count.get());

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ExceptionHolder holder = (ExceptionHolder)ois.readObject();
        Throwable e = holder.getException();
        assertTrue(e instanceof SQLException);
        assertEquals("Remote java.sql.SQLException: something bad happened", e.getMessage());

        assertTrue(e.getCause() instanceof TeiidRuntimeException);

        e = ((SQLException)e).getNextException();
        assertTrue(e instanceof SQLException);

        assertEquals("Remote java.sql.SQLException: something else bad happened", e.getMessage());

        int count = 0;
        while ((e = ((SQLException)e).getNextException()) != null) {
            count++;
        }
        assertEquals(9, count);
    }

    @Test public void testDeserializationUnknownChildException2() throws Exception {
        ClassLoader cl = new URLClassLoader(new URL[] {UnitTestUtil.getTestDataFile("test.jar").toURI().toURL()});
        ArrayList<String> args = new ArrayList<String>();
        args.add("Unknown Exception");
        Exception obj = (Exception)ReflectionHelper.create("test.UnknownException", args, cl);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new ExceptionHolder(obj));
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ExceptionHolder holder = (ExceptionHolder)ois.readObject();
        Throwable e = holder.getException();
        assertTrue(e instanceof TeiidRuntimeException);
        assertEquals("Remote test.UnknownException: Unknown Exception", e.getMessage());
    }

    private static class NotSerializable {

    }

    @Test public void testDeserializationNotSerializable() throws Exception {
        Exception ex = new TeiidException() {
            NotSerializable ns = new NotSerializable();
        };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new ExceptionHolder(ex));
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ExceptionHolder holder = (ExceptionHolder)ois.readObject();
        Throwable e = holder.getException();
        assertTrue(e instanceof TeiidException);
    }

    // TODO replace the SER file
    public void testSourceWarning() throws Exception {
        ClassLoader cl = new URLClassLoader(new URL[] {UnitTestUtil.getTestDataFile("test.jar").toURI().toURL()});
        ArrayList<String> args = new ArrayList<String>();
        args.add("Unknown Exception");
        Exception obj = (Exception)ReflectionHelper.create("test.UnknownException", args, cl);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(new ExceptionHolder(new SourceWarning("x", "y", obj, true)));
        oos.flush();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        ExceptionHolder holder = (ExceptionHolder)ois.readObject();
        SourceWarning sw = (SourceWarning)holder.getException();
        assertEquals(sw.getConnectorBindingName(), "y");
        assertEquals(sw.getModelName(), "x");
        assertTrue(sw.isPartialResultsError());

        try {
            ois = new ObjectInputStream(new FileInputStream(UnitTestUtil.getTestDataFile("old-exceptionholder.ser")));
            holder = (ExceptionHolder)ois.readObject();
            assertTrue(holder.getException() instanceof TeiidException);
        } finally {
            ois.close();
        }

    }
}
