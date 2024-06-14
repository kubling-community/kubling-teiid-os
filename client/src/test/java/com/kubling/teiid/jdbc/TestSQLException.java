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

package com.kubling.teiid.jdbc;

import com.kubling.teiid.client.ProcedureErrorInstructionException;
import com.kubling.teiid.core.BundleUtil;
import com.kubling.teiid.core.TeiidException;
import com.kubling.teiid.core.TeiidProcessingException;
import com.kubling.teiid.core.TeiidRuntimeException;
import com.kubling.teiid.net.CommunicationException;
import com.kubling.teiid.net.ConnectionException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class TestSQLException {

    /*
     * Test method for 'com.metamatrix.jdbc.MMSQLException.MMSQLException()'
     */
    @Test
    public void testMMSQLException() {
        TeiidSQLException e = new TeiidSQLException();
        String sqlState = e.getSQLState();
        Throwable cause = e.getCause();
        int errorCode = e.getErrorCode();
        Throwable nestedException = e.getCause();
        SQLException nextException = e.getNextException();

        assertTrue(sqlState == null,
                "Expected MMSQLException.getSQLState() to return <null> but got \""
                        + sqlState + "\" instead.");
        assertTrue(cause == null,
                "Expected MMSQLException.getCause() to return <null> but got ["
                        + (cause != null ? cause.getClass().getName()
                        : "<unknown>") + "] instead.");
        assertTrue(errorCode == 0,
                "Expected MMSQLException.getErrorCode() to return [0] but got ["
                        + errorCode + "] instead.");
        assertTrue(nestedException == null,
                "Expected MMSQLException.getNestedException() to return <null> but got ["
                        + (nestedException != null ? nestedException.getClass()
                        .getName() : "<unknown>") + "] instead.");
        assertTrue(nextException == null,
                "Expected MMSQLException.getNextException() to return <null> but got a SQLException with message \""
                        + (nextException != null ? nextException.getMessage()
                        : "") + "\" instead.");
    }

    /*
     * Test method for 'com.metamatrix.jdbc.MMSQLException.create(Throwable)'
     *
     * Tests various simple exceptions to see if the expected SQLState is
     * returend.
     */
    @Test public void testCreateThrowable_01() {
        testCreateThrowable(new CommunicationException(
                "A test MM Communication Exception"),
                SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION);
        testCreateThrowable(
                new ConnectException("A test connection attempt exception"),
                SQLStates.CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION);
        testCreateThrowable(
                new ConnectionException("A test MM Connection Exception"),
                SQLStates.CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION);
        testCreateThrowable(new IOException(
                "A test Generic java.io.IOException"),
                SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION);
        testCreateThrowable(
                new MalformedURLException(
                        "A test java.net.MalformedURLException"),
                SQLStates.CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION);
        testCreateThrowable(new TeiidException(
                "A test Generic MM Core Exception"), SQLStates.DEFAULT);
        testCreateThrowable(new TeiidException("A test MM Exception"),
                SQLStates.DEFAULT);
        testCreateThrowable(new TeiidProcessingException(
                "A test Generic MM Query Processing Exception"),
                SQLStates.USAGE_ERROR);
        testCreateThrowable(new TeiidRuntimeException(
                "A test MM Runtime Exception"), SQLStates.DEFAULT);
        testCreateThrowable(new TeiidSQLException(
                "A test Generic MM SQL Exception"), SQLStates.DEFAULT);
        testCreateThrowable(
                new NoRouteToHostException(
                        "A test java.net.NoRouteToHostException"),
                SQLStates.CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION);
        testCreateThrowable(new NullPointerException("A test NPE"),
                SQLStates.DEFAULT);
        testCreateThrowable(new ProcedureErrorInstructionException(
                "A test SQL Procedure Error exception"),
                SQLStates.VIRTUAL_PROCEDURE_ERROR);
        testCreateThrowable(new SocketTimeoutException(
                "A test socket timeout exception"),
                SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION);
        testCreateThrowable(
                new UnknownHostException("A test connection attempt exception"),
                SQLStates.CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION);
    }

    /*
     * Test method for 'com.metamatrix.jdbc.MMSQLException.create(Throwable)'
     *
     * Tests various nested exceptions to see if the expected SQLState is
     * returend.
     */
    @Test public void testCreateThrowable_02() {
        testCreateThrowable(
                new CommunicationException(new ConnectException(
                        "A test java.net.ConnectException"),
                        "Test Communication Exception with a ConnectException in it"),
                SQLStates.CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION);
        testCreateThrowable(new CommunicationException(new SocketException(
                "A test java.net.SocketException"),
                "Test Communication Exception with a SocketException in it"),
                SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION);
        testCreateThrowable(
                new TeiidException(new SocketTimeoutException(
                        "A test java.net.SocketTimeoutException"),
                        "Test MetaMatrixException with a SocketTimeoutException in it"),
                SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION);
    }

    @Test public void testCreateThrowable3() {
        TeiidSQLException e = testCreateThrowable(
                            new TeiidException(
                                            new SocketTimeoutException(
                                                    "A test MM Invalid Session Exception"),
                                            "Test MetaMatrixRuntimeException with a InvalidSessionException in it"),
                            SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION);

        //test to ensure that wrapping mmsqlexceptions works
        TeiidSQLException e1 = TeiidSQLException.create(e, "new message");
        assertEquals("new message", e1.getMessage());
        testCreateThrowable(((TeiidSQLException)e1.getCause()).getCause(), SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION);
    }

    /*
     * Helper method to test SQLState and general MMSQLException validation
     */
    private TeiidSQLException testCreateThrowable(Throwable ecause, String esqlState) {
        TeiidSQLException e = TeiidSQLException.create(ecause);
        if (ecause.getClass() == TeiidSQLException.class) {
            ecause = null;
        }
        String sqlState = e.getSQLState();
        Throwable cause = e.getCause();
        int errorCode = e.getErrorCode();
        Throwable nestedException = e.getCause();
        SQLException nextException = e.getNextException();

        assertEquals(esqlState, sqlState);
        assertEquals(ecause, cause);
        assertEquals(0, errorCode);
        assertEquals(nestedException, ecause);
        assertNull(nextException);
        return e;
    }

    @Test public void testCreate() {
        TeiidSQLException exception = TeiidSQLException.create(new Exception());

        assertEquals(exception.getMessage(), Exception.class.getName());
        assertNotNull(exception.getSQLState());
        assertEquals(exception.getSQLState(), "38000");

        assertEquals(exception, TeiidSQLException.create(exception));
    }

    @Test public void testCreateFromSQLException() {
        SQLException sqlexception = new SQLException("foo", "21");

        SQLException nested = new SQLException("bar");

        sqlexception.setNextException(nested);

        String message = "top level message";

        TeiidSQLException exception = TeiidSQLException.create(sqlexception, message);
        exception.printStackTrace();
        assertEquals(sqlexception, exception.getCause());
        assertEquals(exception.getMessage(), message);
        assertEquals(exception.getSQLState(), sqlexception.getSQLState());
        assertEquals(exception.getNextException().getMessage(), nested.getMessage());
    }
    public static enum Event implements BundleUtil.Event {
        TEIID21,
    }
    @Test public void testCodeAsVendorCode() {

        TeiidException sqlexception = new TeiidException(Event.TEIID21, "foo");

        String message = "top level message";

        TeiidSQLException exception = TeiidSQLException.create(sqlexception, message);

        assertEquals(sqlexception.getCode(), exception.getTeiidCode());
        assertEquals(21, exception.getErrorCode());
    }

}
