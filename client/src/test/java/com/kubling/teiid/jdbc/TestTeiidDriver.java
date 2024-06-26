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

import com.kubling.teiid.net.TeiidURL;
import org.junit.jupiter.api.Test;

import java.sql.DriverPropertyInfo;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTeiidDriver {
    TeiidDriver drv = new TeiidDriver();
    public String localhost = "localhost";

    @Test
    public void testAccepts() throws Exception {
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:12345"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:12345;user=foo;password=bar"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@/foo/blah/deploy.properties"));

        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:12345"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:12345;user=foo;password=bar"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@/foo/blah/deploy.properties"));
        assertTrue(drv.acceptsURL("jdbc:teiid:8294601c-9fe9-4244-9499-4a012c5e1476_vdb"));
        assertTrue(drv.acceptsURL("jdbc:teiid:8294601c-9fe9-4244-9499-4a012c5e1476_vdb@mm://localhost:12345"));
        assertTrue(drv.acceptsURL("jdbc:teiid:test_vdb@mm://local-host:12345"));
        assertTrue(drv.acceptsURL("jdbc:teiid:test_vdb@mm://local_host:12345"));
        assertTrue(drv.acceptsURL("jdbc:teiid:test_vdb.1@mm://local_host:12345"));
        assertTrue(drv.acceptsURL("jdbc:teiid:test_vdb.10@mm://local_host:12345"));
    }

    /** Valid format of urls*/
    @Test public void testAcceptsURL1()  throws Exception   {
        assertTrue(drv.acceptsURL("jdbc:teiid:jvdb@mm://localhost:1234"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234;version=x"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234,localhost2:12342,localhost3:12343"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234,localhost2:12342,localhost3:12343;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mms://localhost:1234;logLevel=1;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@mm://localhost:1234;logLevel=2;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234;logLevel=2;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log;autoCommitTxn=OFF;paritalResultsMode=true"));
        assertTrue(drv.acceptsURL("jdbc:teiid:jvdb@mms://localhost:1234"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234;version=x"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234,localhost2:12342,localhost3:12343"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mms://localhost:1234,localhost2:12342,localhost3:12343;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mms://localhost:1234;logLevel=2;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log;autoCommitTxn=OFF;paritalResultsMode=true"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://127.0.0.1:1234;logLevel=2"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mms://127.0.0.1:1234"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://127.0.0.1:1234,localhost.mydomain.com:63636;logLevel=2"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://my-host.mydomain.com:53535,127.0.0.1:1234"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://123.123.123.123:53535,127.0.0.1:1234"));

        //DQP type
        assertTrue(drv.acceptsURL("jdbc:teiid:jvdb@c:/dqp.properties;version=1"));
        assertTrue(drv.acceptsURL("jdbc:teiid:jvdb@/foo/dqp.properties;version=1"));
        assertTrue(drv.acceptsURL("jdbc:teiid:jvdb@../foo/dqp.properties;version=1"));

        assertTrue(drv.acceptsURL("jdbc:teiid:jvdb@mm://localhost:port"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@localhost:port;version=x"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234,localhost2:12342,localhost3:12343"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234,localhost2:12342,localhost3:12343;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234;logLevel=1;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234;logLevel=2;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234;logLevel=2;logFile=D:\\metamatrix\\work\\DQP\\log\\jdbcLogFile.log;autoCommitTxn=OFF;paritalResultsMode=true"));
        assertTrue(!drv.acceptsURL("jdbc:teiid:@localhost:1234;stickyConnections=false;socketsPerVM=4"));
        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://my_host.mydomain.com:53535,127.0.0.1:1234"));

        assertTrue(drv.acceptsURL("jdbc:teiid:vdb@mm://localhost:1234;version=x;useJDBC4ColumnNameAndLabelSemantics=false"));

    }

    /** Invalid format of urls*/
    @Test public void testAcceptsURL2() throws Exception    {
        assertTrue(!drv.acceptsURL("jdbc:matamatrix:test"));
        assertTrue(!drv.acceptsURL("metamatrix:test"));
        assertTrue(!drv.acceptsURL("jdbc&matamatrix:test"));
        assertTrue(!drv.acceptsURL("jdbc;metamatrix:test"));
    }

    @Test public void testParseURL() throws Exception{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT@mm://slwxp157:1234", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertTrue(p.getProperty(TeiidURL.CONNECTION.SERVER_URL).equals("mm://slwxp157:1234"));
        assertEquals(3, p.size());
    }

    @Test public void testParseURL2() throws Exception {
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT@mms://slwxp157:1234;version=3", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertTrue(p.getProperty(BaseDataSource.VDB_VERSION).equals("3"));
        assertTrue(p.getProperty(TeiidURL.CONNECTION.SERVER_URL).equals("mms://slwxp157:1234"));
        assertTrue(p.getProperty(BaseDataSource.VERSION).equals("3"));
        assertTrue(p.getProperty(BaseDataSource.APP_NAME).equals(BaseDataSource.DEFAULT_APP_NAME));
        assertEquals(5, p.size());
    }

    @Test public void testParseURL3() throws Exception{
        Properties p = new Properties();
        TeiidDriver.parseURL("jdbc:teiid:BQT@mm://slwxp157:1234,slntmm01:43401,sluxmm09:43302;version=4;autoCommitTxn=ON;partialResultsMode=YES;ApplicationName=Client", p);
        assertTrue(p.getProperty(BaseDataSource.VDB_NAME).equals("BQT"));
        assertTrue(p.getProperty(BaseDataSource.VDB_VERSION).equals("4"));        
        assertTrue(p.getProperty(ExecutionProperties.PROP_TXN_AUTO_WRAP).equals("ON"));
        assertTrue(p.getProperty(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE).equals("YES"));
        assertTrue(p.getProperty(TeiidURL.CONNECTION.SERVER_URL).equals("mm://slwxp157:1234,slntmm01:43401,sluxmm09:43302"));
        assertTrue(p.getProperty(BaseDataSource.VERSION).equals("4"));
        assertTrue(p.getProperty(BaseDataSource.APP_NAME).equals("Client"));
        assertEquals(7, p.size());
    }

    @Test public void testGetPropertyInfo1() throws Exception {
        DriverPropertyInfo info[] = drv.getPropertyInfo("jdbc:teiid:vdb@mm://localhost:12345;applicationName=x", null);

        assertEquals(29, info.length);
        assertEquals(false, info[1].required);
        assertEquals("ApplicationName", info[1].name);
        assertEquals("x", info[1].value);

        for (DriverPropertyInfo dpi : info) {
            assertTrue(!dpi.description.startsWith("<Missing message"), dpi.name);
        }
    }

}
