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

import com.kubling.teiid.jdbc.JDBCURL.ConnectionType;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestEmbeddedProfile {

    /*
     * Test method for 'com.metamatrix.jdbc.EmbeddedEmbeddedProfile.acceptsURL(String)'
     * // (\\w:[\\\\,\\/]|file:\\/\\/|\\/|\\\\|(\\.){1,2}){1}
     */
    @Test
    public void testAcceptsURL() {

        // ClassPath based URL
        assertFalse(ConnectionType.Embedded ==
                JDBCURL.acceptsUrl("jdbc:teiid:BQT@classpath:/dqp.properties;partialResultsMode=true"));

        assertEquals(ConnectionType.Embedded, JDBCURL.acceptsUrl("jdbc:teiid:BQT"));
        assertEquals(ConnectionType.Embedded, JDBCURL.acceptsUrl("jdbc:teiid:BQT!/path/foo.properties"));
        assertEquals(ConnectionType.Embedded, JDBCURL.acceptsUrl("jdbc:teiid:BQT;"));
        assertEquals(ConnectionType.Embedded, JDBCURL.acceptsUrl("jdbc:teiid:BQT;version=1;logFile=foo.txt"));
        assertEquals(ConnectionType.Embedded, JDBCURL.acceptsUrl("jdbc:teiid:BQT.1;version=1;logFile=foo.txt"));
    }

    @Test public void testParseURL() throws SQLException{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertEquals(2, p.size());
    }

    @Test public void testParseURL2() throws SQLException {
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT;version=3", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertTrue(p.getProperty(BaseDataSource.VDB_VERSION).equals("3"));
        assertTrue(p.getProperty(BaseDataSource.VERSION).equals("3"));
        assertEquals(4, p.size());
    }

    @Test public void testParseURL3() throws SQLException{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT;version=4;autoCommitTxn=ON;partialResultsMode=YES;", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertTrue(p.getProperty(BaseDataSource.VDB_VERSION).equals("4"));
        assertTrue(p.getProperty(BaseDataSource.VERSION).equals("4"));
        assertTrue(p.getProperty(ExecutionProperties.PROP_TXN_AUTO_WRAP).equals("ON"));
        assertTrue(p.getProperty(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE).equals("YES"));
        assertEquals(6, p.size());
    }

    @Test public void testParseURL4() throws SQLException{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT;partialResultsMode=true", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertTrue(p.getProperty(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE).equals("true"));
        assertEquals(3, p.size());
    }

    @Test public void testParseURL5() throws SQLException{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
    }

    @Test public void testParseURL55() throws SQLException{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT;", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
    }

    @Test public void testParseURL6() throws SQLException{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT;partialResultsMode=true;version=1", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertTrue(p.getProperty(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE).equals("true"));
        assertTrue(p.getProperty(BaseDataSource.VDB_VERSION).equals("1"));
        assertEquals(5, p.size());

    }

    @Test public void test() throws Exception {
        try {
            Class.forName("com.kubling.teiid.jdbc.TeiidDriver");
            DriverManager.getConnection("jdbc:teiid:Parts@invalidConfig.properties;version=1");
            fail();
        } catch (SQLException e) {
        }
    }
}
