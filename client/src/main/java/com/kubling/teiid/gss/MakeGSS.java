/*-------------------------------------------------------------------------
 *
 * Copyright (c) 2008, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *   $PostgreSQL: pgjdbc/org/postgresql/gss/MakeGSS.java,v 1.2.2.1 2009/08/18 03:37:08 jurka Exp $
 *
 *-------------------------------------------------------------------------
 */

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

package com.kubling.teiid.gss;

import com.kubling.teiid.client.security.ILogon;
import com.kubling.teiid.client.security.LogonException;
import com.kubling.teiid.client.security.LogonResult;
import com.kubling.teiid.core.TeiidComponentException;
import com.kubling.teiid.jdbc.JDBCPlugin;
import com.kubling.teiid.jdbc.TeiidSQLException;
import com.kubling.teiid.net.CommunicationException;
import com.kubling.teiid.net.TeiidURL;
import org.ietf.jgss.*;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MakeGSS {

    private static final Logger logger = Logger.getLogger("org.teiid.jdbc");

    public static LogonResult authenticate(ILogon logon, Properties props)
            throws LogonException, TeiidComponentException, CommunicationException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("GSS Authentication Request");
        }

        Object result;

        StringBuilder errors = new StringBuilder();
        String jaasApplicationName = props.getProperty(TeiidURL.CONNECTION.JAAS_NAME);
        String nl = System.lineSeparator();
        if (jaasApplicationName == null) {
            jaasApplicationName = "Teiid";
        }

        String kerberosPrincipalName = props.getProperty(TeiidURL.CONNECTION.KERBEROS_SERVICE_PRINCIPLE_NAME);
        if (kerberosPrincipalName == null) {
            try {
                TeiidURL url = new TeiidURL(props.getProperty(TeiidURL.CONNECTION.SERVER_URL));
                kerberosPrincipalName = "TEIID/" + url.getHostInfo().getFirst().getHostName();
            } catch (Exception e) {
                // Ignore exception
            }
            if (kerberosPrincipalName == null) {
                errors.append(JDBCPlugin.Util.getString("client_prop_missing",
                        TeiidURL.CONNECTION.KERBEROS_SERVICE_PRINCIPLE_NAME));
                errors.append(nl);
            }
        }

        String krb5 = System.getProperty("java.security.krb5.conf");
        String realm = System.getProperty("java.security.krb5.realm");
        String kdc = System.getProperty("java.security.krb5.kdc");


        if (krb5 == null && realm == null && kdc == null) {
            errors.append(JDBCPlugin.Util.getString("no_gss_selection"));
            errors.append(nl);
        } else if (krb5 != null && (realm != null || kdc != null)) {
            errors.append(JDBCPlugin.Util.getString("ambigious_gss_selection"));
            errors.append(nl);
        } else if ((realm != null && kdc == null) || (realm == null && kdc != null)) {
            // krb5 is null here..
            if (realm == null) {
                errors.append(JDBCPlugin.Util.getString("system_prop_missing", "java.security.krb5.realm"));
                errors.append(nl);
            }
            if (kdc == null) {
                errors.append(JDBCPlugin.Util.getString("system_prop_missing", "java.security.krb5.kdc"));
                errors.append(nl);
            }
        }

        String config = System.getProperty("java.security.auth.login.config");
        if (config == null) {
            errors.append(JDBCPlugin.Util.getString("system_prop_missing", "java.security.auth.login.config"));
            errors.append(nl);
        }
        try {
            String user = props.getProperty(TeiidURL.CONNECTION.USER_NAME);
            String password = props.getProperty(TeiidURL.CONNECTION.PASSWORD);

            boolean performAuthentication = true;
            GSSCredential gssCredential = null;
            Subject sub = Subject.getSubject(AccessController.getContext());
            if (sub != null) {
                Set<GSSCredential> gssCreds = sub.getPrivateCredentials(GSSCredential.class);
                if (gssCreds != null && !gssCreds.isEmpty()) {
                    gssCredential = gssCreds.iterator().next();
                    performAuthentication = false;
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("GSS Authentication using delegated credential");
                    }
                } else {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("No delegation credential found in the subject");
                    }
                }
            }

            if (performAuthentication) {
                if (!errors.isEmpty()) {
                    throw new LogonException(JDBCPlugin.Event.TEIID20005, errors.toString());
                }

                LoginContext lc = new LoginContext(jaasApplicationName, new GSSCallbackHandler(user, password));
                lc.login();

                sub = lc.getSubject();
            }

            PrivilegedAction action = new GssAction(logon, kerberosPrincipalName, props, user, gssCredential);
            result = Subject.doAs(sub, action);
        } catch (Exception e) {
            throw new LogonException(JDBCPlugin.Event.TEIID20005, e, JDBCPlugin.Util.gs(JDBCPlugin.Event.TEIID20005));
        }

        if (result instanceof LogonException) {
            throw (LogonException) result;
        } else if (result instanceof TeiidComponentException) {
            throw (TeiidComponentException) result;
        } else if (result instanceof CommunicationException) {
            throw (CommunicationException) result;
        } else if (result instanceof Exception) {
            throw new LogonException(JDBCPlugin.Event.TEIID20005, (Exception) result,
                    JDBCPlugin.Util.gs(JDBCPlugin.Event.TEIID20005));
        }

        return (LogonResult) result;
    }

}

class GssAction implements PrivilegedAction {

    private static final Logger logger = Logger.getLogger("org.teiid.jdbc");
    private final ILogon logon;
    private final String kerberosPrincipalName;
    private final Properties props;
    private final GSSCredential gssCredential;
    private final String user;

    public GssAction(ILogon pgStream, String kerberosPrincipalName, Properties props, String user, GSSCredential gssCredential) {
        this.logon = pgStream;
        this.kerberosPrincipalName = kerberosPrincipalName;
        this.props = props;
        this.user = user;
        this.gssCredential = gssCredential;
    }

    public Object run() {
        byte[] outToken;

        try {
            Oid[] desiredMechs = new Oid[1];
            desiredMechs[0] = new Oid("1.2.840.113554.1.2.2");

            GSSManager manager = GSSManager.getInstance();

            //http://docs.oracle.com/cd/E21455_01/common/tutorials/kerberos_principal.html
            Oid KERBEROS_V5_PRINCIPAL_NAME = new Oid("1.2.840.113554.1.2.2.1");

            // null on second param means the serverName is already in the native format.
            //GSSName serverName = manager.createName(this.kerberosPrincipalName, null);
            GSSName serverName = manager.createName(this.kerberosPrincipalName, KERBEROS_V5_PRINCIPAL_NAME);

            GSSCredential clientCreds = null;
            if (this.gssCredential != null) {
                clientCreds = this.gssCredential;
            }

            GSSContext secContext = manager.createContext(serverName, desiredMechs[0], clientCreds, GSSContext.DEFAULT_LIFETIME);
            secContext.requestMutualAuth(true);
            secContext.requestConf(true);  // Will use confidentiality later
            secContext.requestInteg(true); // Will use integrity later
            secContext.requestCredDeleg(true); //will use credential delegation

            byte[] inToken = new byte[0];

            boolean established = false;
            LogonResult result = null;
            while (!established) {
                outToken = secContext.initSecContext(inToken, 0, inToken.length);
                if (outToken != null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Sending Service Token to Server (GSS Authentication Token)");
                    }
                    result = logon.neogitiateGssLogin(this.props, outToken, true);
                    inToken = (byte[]) result.getProperty(ILogon.KRB5TOKEN);
                }

                if (!secContext.isEstablished()) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Authentication GSS Continue");
                    }
                } else {
                    established = true;
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Authentication GSS Established");
                    }
                }
            }
            return result;
        } catch (GSSException gsse) {
            return TeiidSQLException.create(gsse, JDBCPlugin.Util.gs(JDBCPlugin.Event.TEIID20005));
        } catch (Exception e) {
            return e;
        }
    }
}

