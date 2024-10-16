/*-------------------------------------------------------------------------
 *
 * Copyright (c) 2008, PostgreSQL Global Development Group
 *
 * IDENTIFICATION
 *   $PostgreSQL: pgjdbc/org/postgresql/gss/GSSCallbackHandler.java,v 1.2 2008/11/29 07:43:47 jurka Exp $
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

import com.kubling.teiid.jdbc.JDBCPlugin;

import javax.security.auth.callback.*;
import java.io.IOException;
import java.util.logging.Logger;

public class GSSCallbackHandler implements CallbackHandler {

    private static final Logger logger = Logger.getLogger(GSSCallbackHandler.class.getName());

    private final String user;
    private final String password;

    public GSSCallbackHandler(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof TextOutputCallback) {
                TextOutputCallback toc = (TextOutputCallback) callback;
                switch (toc.getMessageType()) {
                    case TextOutputCallback.INFORMATION:
                        logger.info(toc.getMessage());
                        break;
                    case TextOutputCallback.ERROR:
                        logger.severe(toc.getMessage());
                        break;
                    case TextOutputCallback.WARNING:
                        logger.warning(toc.getMessage());
                        break;
                    default:
                        throw new IOException("Unsupported message type: " + toc.getMessageType());
                }
            } else if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback) callback;
                nc.setName(user);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;
                if (password == null) {
                    throw new IOException(JDBCPlugin.Util.getString("no_krb_ticket"));
                }
                pc.setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
            }
        }
    }

}


