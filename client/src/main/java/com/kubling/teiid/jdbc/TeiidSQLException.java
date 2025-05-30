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
import com.kubling.teiid.client.security.InvalidSessionException;
import com.kubling.teiid.client.security.LogonException;
import com.kubling.teiid.client.util.ExceptionUtil;
import com.kubling.teiid.core.TeiidException;
import com.kubling.teiid.core.TeiidProcessingException;
import com.kubling.teiid.core.TeiidRuntimeException;
import com.kubling.teiid.net.CommunicationException;
import com.kubling.teiid.net.ConnectionException;

import java.io.IOException;
import java.io.Serial;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.sql.SQLException;


/**
 * Teiid specific SQLException
 */

public class TeiidSQLException extends SQLException {

    @Serial
    private static final long serialVersionUID = 3672305321346173922L;
    private String teiidCode;

    /**
     * No-arg constructor required by Externalizable semantics.
     */
    public TeiidSQLException() {
        super();
    }

    public TeiidSQLException(String reason) {
        super(reason, SQLStates.DEFAULT);
    }

    public TeiidSQLException(String reason, String state) {
        super(reason, state);
    }

    public static TeiidSQLException create(Throwable exception) {
        if (exception instanceof TeiidSQLException) {
            return (TeiidSQLException) exception;
        }
        return create(exception, exception.getMessage());
    }

    public TeiidSQLException(Throwable ex, String reason, String sqlState, int errorCode) {
        super(reason, sqlState, errorCode); // passing the message to the super class constructor.
        initCause(ex);
    }

    private TeiidSQLException(SQLException ex, String message, boolean addChildren) {
        super(message, ex.getSQLState() == null ? SQLStates.DEFAULT : ex.getSQLState(), ex.getErrorCode(), ex);
        if (addChildren) {
            SQLException childException = ex.getNextException(); // this a child to the SQLException constructed from reason

            while (childException != null) {
                if (childException instanceof TeiidSQLException) {
                    super.setNextException(ex);
                    break;
                }
                super.setNextException(new TeiidSQLException(childException, getMessage(childException, null), false));
                childException = childException.getNextException();
            }
        }
    }

    public static TeiidSQLException create(Throwable exception, String message) {
        message = getMessage(exception, message);
        Throwable origException = exception;
        if (exception instanceof TeiidSQLException
                && message.equals(exception.getMessage())) {
            return (TeiidSQLException) exception;
        }
        if (exception instanceof SQLException) {
            return new TeiidSQLException((SQLException) exception, message, true);
        }
        String sqlState = null;
        int errorCode = 0;
        SQLException se = ExceptionUtil.getExceptionOfType(exception, SQLException.class);
        if (se != null && se.getSQLState() != null) {
            sqlState = se.getSQLState();
            errorCode = se.getErrorCode();
        }
        TeiidException te = ExceptionUtil.getExceptionOfType(exception, TeiidException.class);
        String code = null;
        if (te != null && te.getCode() != null) {
            code = te.getCode();
            if (errorCode == 0) {
                String intPart = code;
                if (code.startsWith("TEIID")) {
                    intPart = code.substring(5);
                }
                try {
                    errorCode = Integer.parseInt(intPart);
                } catch (NumberFormatException e) {
                    // Ignored
                }
            }
        }
        if (sqlState == null) {
            exception = findRootException(exception);
            sqlState = determineSQLState(exception, sqlState);
        }
        if (sqlState == null) {
            sqlState = SQLStates.DEFAULT;
        }
        TeiidSQLException tse = new TeiidSQLException(origException, message, sqlState, errorCode);
        tse.teiidCode = code;
        return tse;
    }

    private static String determineSQLState(Throwable exception,
                                            String sqlState) {
        if (exception instanceof InvalidSessionException) {
            sqlState = SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION;
        } else if (exception instanceof LogonException) {
            sqlState = SQLStates.INVALID_AUTHORIZATION_SPECIFICATION_NO_SUBCLASS;
        } else if (exception instanceof ProcedureErrorInstructionException) {
            sqlState = SQLStates.VIRTUAL_PROCEDURE_ERROR;
        } else if (exception instanceof TeiidProcessingException) {
            sqlState = SQLStates.USAGE_ERROR;
            if (SQLStates.QUERY_CANCELED.equals(((TeiidException) exception).getCode())) {
                sqlState = SQLStates.QUERY_CANCELED;
            }
        } else if (exception instanceof UnknownHostException
                || exception instanceof ConnectException
                || exception instanceof MalformedURLException
                || exception instanceof NoRouteToHostException
                || exception instanceof ConnectionException) {
            sqlState = SQLStates.CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION;
        } else if (exception instanceof IOException) {
            sqlState = SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION;
        } else if (exception instanceof TeiidException) {
            if (exception instanceof CommunicationException) {
                sqlState = SQLStates.CONNECTION_EXCEPTION_STALE_CONNECTION;
            }

            Throwable originalException = exception;
            exception = originalException.getCause();
            exception = findRootException(exception);

            if (exception != null && exception != originalException) {
                sqlState = determineSQLState(exception, sqlState);
            }
        }
        return sqlState;
    }

    private static Throwable findRootException(Throwable exception) {
        if (exception instanceof TeiidRuntimeException) {
            while (exception.getCause() != exception
                    && exception.getCause() != null) {
                exception = exception.getCause();
            }
            if (exception instanceof TeiidRuntimeException runtimeException) {
                while (runtimeException.getCause() != exception
                        && runtimeException.getCause() != null) {
                    if (runtimeException.getCause() instanceof TeiidRuntimeException) {
                        runtimeException = (TeiidRuntimeException) runtimeException
                                .getCause();
                    } else {
                        exception = runtimeException.getCause();
                        break;
                    }
                }
            }
        }
        return exception;
    }

    /**
     * @since 4.1
     */
    private static String getMessage(Throwable exception,
                                     String message) {
        if (message == null) {
            message = exception.getMessage();
            if (message == null) {
                message = exception.getClass().getName();
            }
        }
        return message;
    }

    public boolean isSystemErrorState() {
        return SQLStates.isSystemErrorState(getSQLState());
    }

    public boolean isUsageErrorState() {
        return SQLStates.isUsageErrorState(getSQLState());
    }

    public String getTeiidCode() {
        return teiidCode;
    }
}