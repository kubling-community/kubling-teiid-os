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

import com.kubling.teiid.core.TeiidException;
import com.kubling.teiid.net.TeiidURL;
import com.kubling.teiid.net.socket.OioOjbectChannelFactory;
import com.kubling.teiid.net.socket.SocketServerConnection;
import com.kubling.teiid.net.socket.SocketServerConnectionFactory;

import java.util.Properties;


/**
 * <p> The java.sql.DriverManager class uses this class to connect to Teiid Server.
 * The TeiidDriver class has a static initializer, which
 * is used to instantiate and register itself with java.sql.DriverManager. The
 * DriverManager's <code>getConnection</code> method calls <code>connect</code>
 * method on available registered drivers.
 */

final class SocketProfile implements ConnectionProfile {


    /**
     * This method tries to make a connection to the given URL. This class
     * will return a null if this is not the right driver to connect to the given URL.
     *
     * @param url used to establish a connection.
     * @return Connection object created
     * @throws TeiidSQLException if it is unable to establish a connection to the server.
     */
    public ConnectionImpl connect(String url, Properties info) throws TeiidSQLException {

        int loginTimeoutSeconds = 0;
        SocketServerConnection serverConn;
        try {
            String timeout = info.getProperty(TeiidURL.CONNECTION.LOGIN_TIMEOUT);
            if (timeout != null) {
                loginTimeoutSeconds = Integer.parseInt(timeout);
            }

            if (loginTimeoutSeconds > 0) {
                OioOjbectChannelFactory.TIMEOUTS.set(System.currentTimeMillis() +
                        Integer.valueOf(loginTimeoutSeconds * 1000).longValue());
            }
            serverConn = SocketServerConnectionFactory.getInstance(info).getConnection(info);
        } catch (TeiidException e) {
            throw TeiidSQLException.create(e);
        } finally {
            if (loginTimeoutSeconds > 0) {
                OioOjbectChannelFactory.TIMEOUTS.remove();
            }
        }

        // construct a MMConnection object.
        return new ConnectionImpl(serverConn, info, url);
    }

}
