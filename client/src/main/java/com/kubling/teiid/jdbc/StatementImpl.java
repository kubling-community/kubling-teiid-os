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

import com.kubling.teiid.client.DQP;
import com.kubling.teiid.client.RequestMessage;
import com.kubling.teiid.client.ResultsMessage;
import com.kubling.teiid.client.metadata.ParameterInfo;
import com.kubling.teiid.client.metadata.ResultsMetadataConstants;
import com.kubling.teiid.client.plan.Annotation;
import com.kubling.teiid.client.plan.PlanNode;
import com.kubling.teiid.client.util.ResultsFuture;
import com.kubling.teiid.core.TeiidComponentException;
import com.kubling.teiid.core.TeiidException;
import com.kubling.teiid.core.TeiidProcessingException;
import com.kubling.teiid.core.types.DataTypeManager;
import com.kubling.teiid.core.types.JDBCSQLTypeInfo;
import com.kubling.teiid.core.types.SQLXMLImpl;
import com.kubling.teiid.core.util.SqlUtil;
import com.kubling.teiid.core.util.StringUtil;
import com.kubling.teiid.jdbc.tracing.TracingHelper;
import com.kubling.teiid.net.TeiidURL;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StatementImpl extends WrapperImpl implements TeiidStatement {

    public static final String NEWINSTANCE = "NEWINSTANCE";

    private static final Logger logger = Logger.getLogger("org.teiid.jdbc");

    static EnhancedTimer cancellationTimer = new EnhancedTimer("Teiid Statement Timeout");

    private static final class QueryTimeoutCancelTask implements Runnable {
        private final WeakReference<StatementImpl> ref;

        private QueryTimeoutCancelTask(StatementImpl stmt) {
            this.ref = new WeakReference<>(stmt);
        }

        @Override
        public void run() {
            StatementImpl stmt = ref.get();
            if (stmt != null) {
                stmt.timeoutOccurred();
            }
        }
    }

    enum State {
        RUNNING,
        DONE,
        TIMED_OUT,
        CANCELLED
    }

    protected static final int NO_TIMEOUT = 0;

    // integer indicating no maximum limit - used in some metadata-ish methods.
    private static final int NO_LIMIT = 0;

    private final QueryTimeoutCancelTask cancelTask = new QueryTimeoutCancelTask(this);

    //######## Configuration state #############
    private final ConnectionImpl driverConnection;
    private final Properties execProps;

    // fetch size value. This is the default fetch size used by the server
    private int fetchSize = BaseDataSource.DEFAULT_FETCH_SIZE;

    // the fetch direction
    private final int fetchDirection = ResultSet.FETCH_FORWARD;

    // the result set type
    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
    private int resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;

    //######## Processing state #############

    // boolean to indicate if this statement object is closed
    private boolean isClosed = false;

    // Differentiate timeout from cancel in blocking async operation
    protected volatile State commandStatus = State.RUNNING;

    // number of seconds for the query to timeout.
    protected long queryTimeoutMS = NO_TIMEOUT;

    //########## Per-execution state ########

    // ID for current request
    protected long currentRequestID = -1;

    //  the last query plan description
    private PlanNode currentPlanDescription;

    // the last query debug log
    private String debugLog;

    // the last query annotations
    private Collection<Annotation> annotations;

    // resultSet object produced by execute methods on the statement.
    protected volatile ResultSetImpl resultSet;

    private List<Throwable> serverWarnings;

    // the per-execution security payload
    private Serializable payload;

    /**
     * List of INSERT, UPDATE, DELETE AND SELECT INTO commands
     */
    private List<String> batchedUpdates;

    /**
     * Array of update counts as returned by executeBatch()
     */
    protected int[] updateCounts;

    /**
     * default Calendar instance for converting date/time/timestamp values
     */
    private Calendar defaultCalendar;
    /**
     * Max rows to be returned by executing the statement
     */
    private int maxRows = NO_LIMIT;
    private int maxFieldSize = NO_LIMIT;

    //Map<out/inout/return param index --> index in results>
    protected Map<Integer, Integer> outParamIndexMap = new HashMap<>();
    protected Map<String, Integer> outParamByName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private boolean closeOnCompletion;

    static Pattern TRANSACTION_STATEMENT = Pattern.compile("\\s*((?:(?:commit|rollback|abort)(?:\\s+(?:transaction|work))?)|(start\\s+transaction(\\s+[^;]*)?))\\s*;?\\s*", Pattern.CASE_INSENSITIVE);
    static Pattern SET_STATEMENT = Pattern.compile("\\s*set(?:\\s+(payload))?\\s+((?:session authorization)|(?:[a-zA-Z]\\w*)|(?:\"[^\"]*\")+)\\s+(?:(?:to|=)\\s+)?((?:[^\\s]*)|(?:'[^']*')+)\\s*;?\\s*", Pattern.CASE_INSENSITIVE);
    static Pattern SET_CHARACTERISTIC_STATEMENT = Pattern.compile("\\s*set\\s+session\\s+characteristics\\s+as\\s+transaction\\s+isolation\\s+level\\s+((?:read\\s+(?:(?:committed)|(?:uncommitted)))|(?:repeatable\\s+read)|(?:serializable))\\s*", Pattern.CASE_INSENSITIVE);
    static Pattern SHOW_STATEMENT = Pattern.compile("\\s*show\\s+((?:transaction isolation level)|(?:[a-zA-Z]\\w*)|(?:\"[^\"]*\")+)\\s*;?\\s*", Pattern.CASE_INSENSITIVE);

    /**
     * MMStatement Constructor.
     */
    StatementImpl(ConnectionImpl driverConnection, int resultSetType, int resultSetConcurrency) {
        this.driverConnection = driverConnection;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.execProps = new Properties(this.driverConnection.getExecutionProperties());

        // Set initial fetch size
        String fetchSizeStr = this.execProps.getProperty(ExecutionProperties.PROP_FETCH_SIZE);
        if (fetchSizeStr != null) {
            try {
                this.fetchSize = Integer.parseInt(fetchSizeStr);
            } catch (Exception e) {
                // silently failover to default
            }
        }
        setTimeoutFromProperties();
    }

    private void setTimeoutFromProperties() {
        String queryTimeoutStr = this.execProps.getProperty(ExecutionProperties.QUERYTIMEOUT);
        if (queryTimeoutStr != null) {
            try {
                this.queryTimeoutMS = Integer.parseInt(queryTimeoutStr) * 1000L;
            } catch (Exception e) {
                // silently failover to default
            }
        }
    }

    protected DQP getDQP() {
        return this.driverConnection.getDQP();
    }

    protected ConnectionImpl getMMConnection() {
        return this.driverConnection;
    }

    protected TimeZone getServerTimeZone() throws SQLException {
        return this.driverConnection.getServerConnection().getLogonResult().getTimeZone();
    }

    /**
     * Reset all per-execution state - this should be done before executing
     * a new command.
     */
    protected synchronized void resetExecutionState() throws SQLException {
        this.currentRequestID = -1;

        this.currentPlanDescription = null;
        this.debugLog = null;
        this.annotations = null;

        if (this.resultSet != null) {
            ResultSet rs = this.resultSet;
            this.resultSet = null;
            rs.close();
            checkStatement();
        }

        this.serverWarnings = null;

        this.batchedUpdates = null;
        this.updateCounts = null;
        this.outParamIndexMap.clear();
        this.outParamByName.clear();
        this.commandStatus = State.RUNNING;
    }

    public void addBatch(String sql) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (batchedUpdates == null) {
            batchedUpdates = new ArrayList<>();
        }
        batchedUpdates.add(sql);
    }

    public void cancel() throws SQLException {
        /* Defect 19848 - Mark the statement cancelled before sending the CANCEL request.
         * Otherwise, it's possible get into a race where the server response is quicker
         * than the exception in the exception in the conditionalWait(), which results in
         * the statement.executeQuery() call throwing the server's exception instead of the
         * one generated by the conditionalWait() method.
         */
        long request;
        synchronized (this) {
            commandStatus = State.CANCELLED;
            checkStatement();
            request = currentRequestID;
            if (request == -1) {
                return;
            }
        }
        //cancel outside the lock
        try {
            this.getDQP().cancelRequest(request);
        } catch (TeiidProcessingException | TeiidComponentException e) {
            throw TeiidSQLException.create(e);
        }
    }

    public void clearWarnings() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();

        // clear all the warnings on this statement, after this, getWarnings() should return null
        serverWarnings = null;
    }

    public void clearBatch() {
        if (batchedUpdates != null) {
            batchedUpdates.clear();
        }
    }

    public void close() throws SQLException {
        if (isClosed) {
            return;
        }

        // close the server's statement object (if necessary)
        if (resultSet != null) {
            ResultSet rs = this.resultSet;
            resultSet = null;
            rs.close();

        }

        isClosed = true;

        // Remove link from connection to statement
        this.driverConnection.closeStatement(this);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(JDBCPlugin.Util.getString("MMStatement.Close_stmt_success"));
        }
    }

    /**
     * <p> This utility method checks if the jdbc statement is closed and
     * throws an exception if it is closed.
     *
     * @throws TeiidSQLException if the statement object is closed.
     */
    protected void checkStatement() throws TeiidSQLException {
        //Check to see the connection is closed and proceed if it is not
        driverConnection.checkConnection();
        if (isClosed) {
            throw new TeiidSQLException(JDBCPlugin.Util.getString("MMStatement.Stmt_closed"));
        }
    }

    @Override
    public void submitExecute(String sql, StatementCallback callback, RequestOptions options) throws SQLException {
        NonBlockingRowProcessor processor = new NonBlockingRowProcessor(this, callback);
        submitExecute(sql, options).addCompletionListener(processor);
    }

    public ResultsFuture<Boolean> submitExecute(String sql, RequestOptions options) throws SQLException {
        return executeSql(new String[]{sql}, false, RequestMessage.ResultsMode.EITHER, false, options);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return execute(sql, Statement.NO_GENERATED_KEYS);
    }

    @Override
    public int[] executeBatch() throws SQLException {
        if (batchedUpdates == null || batchedUpdates.isEmpty()) {
            return new int[0];
        }
        String[] commands = batchedUpdates.toArray(new String[0]);
        executeSql(commands, true, RequestMessage.ResultsMode.UPDATECOUNT, true, null);
        return updateCounts;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        executeSql(new String[]{sql}, false, RequestMessage.ResultsMode.RESULTSET, true, null);
        return resultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql, Statement.NO_GENERATED_KEYS);
    }

    protected boolean hasResultSet() throws SQLException {
        return updateCounts == null && resultSet != null && resultSet.getMetaData().getColumnCount() > 0;
    }

    protected void createResultSet(ResultsMessage resultsMsg) throws SQLException {
        //create out/return parameter index map if there is any
        List listOfParameters = resultsMsg.getParameters();
        if (listOfParameters != null) {
            //get the size of result set
            int resultSetSize = 0;
            Iterator iteratorOfParameters = listOfParameters.iterator();
            while (iteratorOfParameters.hasNext()) {
                ParameterInfo parameter = (ParameterInfo) iteratorOfParameters.next();
                if (parameter.getType() == ParameterInfo.RESULT_SET) {
                    resultSetSize = parameter.getNumColumns();
                    //one ResultSet only
                    break;
                }
            }

            //return needs to be the first
            int index = 0; //index in user call - {?=call sp(?)}
            int count = 0;
            iteratorOfParameters = listOfParameters.iterator();
            while (iteratorOfParameters.hasNext()) {
                ParameterInfo parameter = (ParameterInfo) iteratorOfParameters.next();
                if (parameter.getType() == ParameterInfo.RETURN_VALUE) {
                    count++;
                    index++;
                    int resultIndex = resultSetSize + count;
                    outParamIndexMap.put(index, resultIndex);
                    outParamByName.put(resultsMsg.getColumnNames()[resultIndex - 1].toUpperCase(), resultIndex);
                    break;
                }
            }

            iteratorOfParameters = listOfParameters.iterator();
            while (iteratorOfParameters.hasNext()) {
                ParameterInfo parameter = (ParameterInfo) iteratorOfParameters.next();
                if (parameter.getType() != ParameterInfo.RETURN_VALUE && parameter.getType() != ParameterInfo.RESULT_SET) {
                    index++;
                    if (parameter.getType() == ParameterInfo.OUT || parameter.getType() == ParameterInfo.INOUT) {
                        count++;
                        int resultIndex = resultSetSize + count;
                        outParamIndexMap.put(index, resultIndex);
                        outParamByName.put(resultsMsg.getColumnNames()[resultIndex - 1].toUpperCase(), resultIndex);
                    }
                }
            }
        }
        ResultSetMetaData metadata = null;
        if (updateCounts != null) {
            metadata = createResultSetMetaData(createMetadataMap(resultsMsg.getColumnNames(), resultsMsg.getDataTypes()));
        }
        resultSet = new ResultSetImpl(resultsMsg, this, metadata, outParamIndexMap.size());
        resultSet.setMaxFieldSize(this.maxFieldSize);
    }

    protected ResultsFuture<Boolean> executeSql(
            String[] commands,
            boolean isBatchedCommand,
            RequestMessage.ResultsMode resultsMode,
            boolean synch,
            RequestOptions options)
            throws SQLException {
        return executeSql(commands, isBatchedCommand, resultsMode, synch, options, false);
    }

    @SuppressWarnings("unchecked")
    protected ResultsFuture<Boolean> executeSql(
            String[] commands,
            boolean isBatchedCommand,
            RequestMessage.ResultsMode resultsMode,
            boolean synch,
            RequestOptions options,
            boolean autoGenerateKeys)
            throws SQLException {
        checkStatement();
        resetExecutionState();
        if (options != null) {
            if (options.isContinuous()) {
                if (!this.driverConnection.getServerConnection().supportsContinuous()) {
                    throw new TeiidSQLException(JDBCPlugin.Util.getString("JDBC.continuous"));
                }
                if (this.getResultSetType() != ResultSet.TYPE_FORWARD_ONLY) {
                    String msg = JDBCPlugin.Util.getString("JDBC.forward_only_resultset");
                    throw new TeiidSQLException(msg);
                }
                if (resultsMode == RequestMessage.ResultsMode.EITHER) {
                    resultsMode = RequestMessage.ResultsMode.RESULTSET;
                } else if (resultsMode == RequestMessage.ResultsMode.UPDATECOUNT) {
                    String msg = JDBCPlugin.Util.getString("JDBC.forward_only_resultset");
                    throw new TeiidSQLException(msg);
                }
            }
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Executing: requestID " + getCurrentRequestID() + " commands: " + Arrays.toString(commands) + " expecting: " + resultsMode);
        }
        if (commands.length == 1) {
            Matcher match = SET_STATEMENT.matcher(commands[0]);
            if (match.matches()) {
                if (resultsMode == RequestMessage.ResultsMode.RESULTSET) {
                    throw new TeiidSQLException(JDBCPlugin.Util.getString("StatementImpl.set_result_set"));
                }
                String val = match.group(2);
                String key = unescapeId(val);
                String value = match.group(3);
                if (value != null && value.startsWith("\'") && value.endsWith("\'")) {
                    value = value.substring(1, value.length() - 1);
                    value = StringUtil.replaceAll(value, "''", "'");
                }
                if (match.group(1) != null) {
                    //payload case
                    Properties p = this.getMMConnection().getPayload();
                    if (p == null) {
                        p = new Properties();
                        this.getMMConnection().setPayload(p);
                    }
                    p.setProperty(key, value);
                } else if (val.equals(key) && "SESSION AUTHORIZATION".equalsIgnoreCase(key)) {
                    this.getMMConnection().changeUser(value, this.getMMConnection().getPassword());
                } else if (key.equalsIgnoreCase(TeiidURL.CONNECTION.PASSWORD)) {
                    this.getMMConnection().setPassword(value);
                } else if (NEWINSTANCE.equalsIgnoreCase(key)) {
                    //no op - url based load-balancing removed
                } else {
                    this.driverConnection.setExecutionProperty(key, value);
                }
                this.updateCounts = new int[]{0};
                return booleanFuture(false);
            }
            match = SET_CHARACTERISTIC_STATEMENT.matcher(commands[0]);
            if (match.matches()) {
                String value = match.group(1);
                setIsolationLevel(value);
                this.updateCounts = new int[]{0};
                return booleanFuture(false);
            }
            match = TRANSACTION_STATEMENT.matcher(commands[0]);
            if (match.matches()) {
                logger.finer("Executing as transaction statement");
                if (resultsMode == RequestMessage.ResultsMode.RESULTSET) {
                    throw new TeiidSQLException(JDBCPlugin.Util.getString("StatementImpl.set_result_set"));
                }
                String command = match.group(1);
                Boolean commit = null;
                if (StringUtil.startsWithIgnoreCase(command, "start")) {
                    boolean success = false;
                    try {
                        String characteristic = match.group(3);
                        if (characteristic != null) {
                            //this does not match the per connection semantics of jdbc,
                            //as this is per transaction
                            characteristic = characteristic.trim();
                            this.getMMConnection().saveTransactionCharacteristics();
                            if (StringUtil.endsWithIgnoreCase(characteristic, "only")) {
                                this.getMMConnection().setReadOnly(true);
                            } else if (StringUtil.endsWithIgnoreCase(characteristic, "write")) {
                                this.getMMConnection().setReadOnly(false);
                            } else {
                                int i = characteristic.indexOf("level");
                                if (i > -1) {
                                    //remove isolation level
                                    characteristic = characteristic.substring(i + 5).trim();
                                }
                                setIsolationLevel(characteristic);
                            }
                        }
                        //TODO: this should force a start and through an exception if we're already in a txn
                        this.getConnection().setAutoCommit(false);
                        success = true;
                    } finally {
                        if (!success) {
                            this.getMMConnection().restoreTransactionCharacteristics();
                        }
                    }
                } else if (StringUtil.startsWithIgnoreCase(command, "commit")) {
                    commit = true;
                    if (synch) {
                        this.getConnection().setAutoCommit(true);
                    }
                } else if (StringUtil.startsWithIgnoreCase(command, "rollback") || StringUtil.startsWithIgnoreCase(command, "abort")) {
                    commit = false;
                    if (synch || !this.getConnection().isInLocalTxn()) {
                        this.getConnection().rollback(false);
                    }
                }
                this.updateCounts = new int[]{0};
                if (commit != null && !synch) {
                    ResultsFuture<?> pending = this.getConnection().submitSetAutoCommitTrue(commit);
                    final ResultsFuture<Boolean> result = new ResultsFuture<>();
                    pending.addCompletionListener((ResultsFuture.CompletionListener) future -> {
                        try {
                            future.get();
                            result.getResultsReceiver().receiveResults(false);
                        } catch (Throwable t) {
                            result.getResultsReceiver().exceptionOccurred(t);
                        }
                    });
                    return result;
                }
                return booleanFuture(false);
            }
            match = SHOW_STATEMENT.matcher(commands[0]);
            if (match.matches()) {
                logger.finer("Executing as show statement");
                if (resultsMode == RequestMessage.ResultsMode.UPDATECOUNT) {
                    throw new TeiidSQLException(JDBCPlugin.Util.getString("StatementImpl.show_update_count"));
                }
                return executeShow(match);
            }
        }

        final RequestMessage reqMessage = createRequestMessage(commands, isBatchedCommand, resultsMode);
        reqMessage.setReturnAutoGeneratedKeys(autoGenerateKeys);
        reqMessage.setRequestOptions(options);
        if (this.driverConnection.getServerConnection() != null && !this.driverConnection.getServerConnection().isLocal()) {
            String spanContext = TracingHelper.getSpanContext();
            if (spanContext != null) {
                reqMessage.setSpanContext(spanContext);
            }
        }
        ResultsFuture<ResultsMessage> pendingResult = execute(reqMessage, synch);
        final ResultsFuture<Boolean> result = new ResultsFuture<>();
        pendingResult.addCompletionListener(future -> {
            try {
                postReceiveResults(reqMessage, future.get());
                result.getResultsReceiver().receiveResults(hasResultSet());
            } catch (Throwable t) {
                result.getResultsReceiver().exceptionOccurred(t);
            }
        });
        if (synch) {
            try {
                pendingResult.get(queryTimeoutMS == 0 ? Integer.MAX_VALUE : queryTimeoutMS, TimeUnit.MILLISECONDS);
                result.get(); //throw an exception if needed
                return result;
            } catch (ExecutionException e) {
                if (e.getCause() instanceof SQLException) {
                    throw (SQLException) e.getCause();
                }
                if (e.getCause() != null) {
                    throw TeiidSQLException.create(e.getCause());
                }
                throw TeiidSQLException.create(e);
            } catch (InterruptedException | TimeoutException e) {
                timeoutOccurred();
            }
            throw new TeiidSQLException(JDBCPlugin.Util.getString("MMStatement.Timeout_before_complete"));
        }
        return result;
    }

    private void setIsolationLevel(String value) {
        if (StringUtil.endsWithIgnoreCase(value, "uncommitted")) {
            this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } else if (StringUtil.endsWithIgnoreCase(value, "committed")) {
            this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } else if (StringUtil.startsWithIgnoreCase(value, "repeatable")) {
            this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        } else if ("serializable".equalsIgnoreCase(value)) {
            this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        }
    }

    private String unescapeId(String key) {
        if (key.startsWith("\"")) {
            key = key.substring(1, key.length() - 1);
            key = StringUtil.replaceAll(key, "\"\"", "\"");
        }
        return key;
    }

    ResultsFuture<Boolean> executeShow(Matcher match)
            throws SQLException {
        String show = match.group(1);
        show = unescapeId(show);
        if (show.equalsIgnoreCase("PLAN")) {
            List<ArrayList<Object>> records = new ArrayList<>(1);
            PlanNode plan = driverConnection.getCurrentPlanDescription();
            String connDebugLog = driverConnection.getDebugLog();
            if (plan != null || connDebugLog != null) {
                ArrayList<Object> row = new ArrayList<>(3);
                if (plan != null) {
                    row.add(DataTypeTransformer.getClob(plan.toString()));
                    row.add(new SQLXMLImpl(plan.toXml()));
                } else {
                    row.add(null);
                    row.add(null);
                }
                row.add(DataTypeTransformer.getClob(connDebugLog));
                records.add(row);
            }
            createResultSet(records, new String[]{"PLAN_TEXT", "PLAN_XML", "DEBUG_LOG"},
                    new String[]{DataTypeManager.DefaultDataTypes.CLOB, DataTypeManager.DefaultDataTypes.XML, DataTypeManager.DefaultDataTypes.CLOB});
            return booleanFuture(true);
        }
        if (show.equalsIgnoreCase("ANNOTATIONS")) {
            List<ArrayList<Object>> records = new ArrayList<>(1);
            Collection<Annotation> annos = driverConnection.getAnnotations();
            for (Annotation annotation : annos) {
                ArrayList<Object> row = new ArrayList<>(4);
                row.add(annotation.getCategory());
                row.add(annotation.getPriority().name());
                row.add(annotation.getAnnotation());
                row.add(annotation.getResolution());
                records.add(row);
            }
            createResultSet(records, new String[]{"CATEGORY", "PRIORITY", "ANNOTATION", "RESOLUTION"},
                    new String[]{DataTypeManager.DefaultDataTypes.STRING,
                            DataTypeManager.DefaultDataTypes.STRING,
                            DataTypeManager.DefaultDataTypes.STRING,
                            DataTypeManager.DefaultDataTypes.STRING});
            return booleanFuture(true);
        }
        if (show.equalsIgnoreCase("ALL")) {
            List<ArrayList<Object>> records = new ArrayList<>(1);
            for (String key : driverConnection.getExecutionProperties().stringPropertyNames()) {
                ArrayList<Object> row = new ArrayList<>(4);
                row.add(key);
                row.add(driverConnection.getExecutionProperties().get(key));
                records.add(row);
            }
            createResultSet(records, new String[]{"NAME", "VALUE"},
                    new String[]{DataTypeManager.DefaultDataTypes.STRING, DataTypeManager.DefaultDataTypes.STRING});
            return booleanFuture(true);
        }
        if (show.equalsIgnoreCase("transaction isolation level")) {
            List<ArrayList<Object>> records = new ArrayList<>(1);
            ArrayList<Object> row = new ArrayList<>(1);
            switch (driverConnection.getTransactionIsolation()) {
                case Connection.TRANSACTION_READ_COMMITTED:
                    row.add("READ COMMITTED");
                    break;
                case Connection.TRANSACTION_READ_UNCOMMITTED:
                    row.add("READ UNCOMMITTED");
                    break;
                case Connection.TRANSACTION_REPEATABLE_READ:
                    row.add("REPEATABLE READ");
                    break;
                case Connection.TRANSACTION_SERIALIZABLE:
                    row.add("SERIALIZABLE");
                    break;
                default:
                    row.add("UNKNOWN");
            }
            records.add(row);
            createResultSet(records, new String[]{"TRANSACTION ISOLATION"},
                    new String[]{DataTypeManager.DefaultDataTypes.STRING});
            return booleanFuture(true);
        }
        List<List<String>> records = Collections.singletonList(Collections.singletonList(driverConnection.getExecutionProperty(show)));
        createResultSet(records, new String[]{show}, new String[]{DataTypeManager.DefaultDataTypes.STRING});
        return booleanFuture(true);
    }

    private ResultsFuture<ResultsMessage> execute(final RequestMessage reqMsg, boolean synch) throws SQLException,
            TeiidSQLException {
        this.getConnection().beginLocalTxnIfNeeded();
        this.currentRequestID = this.driverConnection.nextRequestID();
        // Create a request message
        if (this.payload != null) {
            reqMsg.setExecutionPayload(this.payload);
        } else {
            reqMsg.setExecutionPayload(this.getMMConnection().getPayload());
        }
        reqMsg.setDelaySerialization(true);
        reqMsg.setCursorType(this.resultSetType);
        reqMsg.setFetchSize(this.fetchSize);
        reqMsg.setRowLimit(this.maxRows);
        reqMsg.setTransactionIsolation(this.driverConnection.getTransactionIsolation());
        reqMsg.setSync(synch && useCallingThread());
        // Get connection properties and set them onto request message
        copyPropertiesToRequest(reqMsg);

        reqMsg.setExecutionId(this.currentRequestID);

        ResultsFuture.CompletionListener<ResultsMessage> compeletionListener = null;
        if (queryTimeoutMS > 0 && (!synch || this.driverConnection.getServerConnection().isLocal())) {
            final EnhancedTimer.Task c = cancellationTimer.add(cancelTask, queryTimeoutMS);
            compeletionListener = future -> c.cancel();
        }

        ResultsFuture<ResultsMessage> pendingResult;
        try {
            pendingResult = this.getDQP().executeRequest(this.currentRequestID, reqMsg);
        } catch (TeiidException e) {
            throw TeiidSQLException.create(e);
        }
        if (compeletionListener != null) {
            pendingResult.addCompletionListener(compeletionListener);
        }
        return pendingResult;
    }

    boolean useCallingThread() throws SQLException {
        if (this.getConnection().getServerConnection() == null || !this.getConnection().getServerConnection().isLocal()) {
            return false;
        }
        String useCallingThread = getExecutionProperty(LocalProfile.USE_CALLING_THREAD);
        return (useCallingThread == null || Boolean.parseBoolean(useCallingThread));
    }

    public static ResultsFuture<Boolean> booleanFuture(boolean isTrue) {
        ResultsFuture<Boolean> rs = new ResultsFuture<>();
        rs.getResultsReceiver().receiveResults(isTrue);
        return rs;
    }

    private synchronized void postReceiveResults(RequestMessage reqMessage,
                                                 ResultsMessage resultsMsg) throws SQLException {
        commandStatus = State.DONE;
        // warnings thrown
        List resultsWarning = resultsMsg.getWarnings();
        // save warnings if any
        if (resultsWarning != null) {
            accumulateWarnings(resultsWarning);
        }

        setAnalysisInfo(resultsMsg);

        //throw an exception unless this represents a batch update exception
        if (resultsMsg.getException() != null && (!resultsMsg.isUpdateResult() || resultsMsg.getResultsList() == null)) {
            throw TeiidSQLException.create(resultsMsg.getException());
        }

        resultsMsg.processResults();

        if (resultsMsg.isUpdateResult()) {
            List<? extends List<?>> results = resultsMsg.getResultsList();
            if (resultsMsg.getUpdateCount() == -1) {
                this.updateCounts = new int[results.size()];
                for (int i = 0; i < results.size(); i++) {
                    updateCounts[i] = (Integer) results.get(i).getFirst();
                }
            } else {
                this.updateCounts = new int[]{resultsMsg.getUpdateCount()};
                this.createResultSet(resultsMsg);
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Received update counts: " + Arrays.toString(updateCounts));
            }
            // In update scenarios close the statement implicitly - the server should have already done this
            try {
                getDQP().closeRequest(getCurrentRequestID());
            } catch (TeiidProcessingException | TeiidComponentException e) {
                throw TeiidSQLException.create(e);
            }

            //handle a batch update exception
            if (resultsMsg.getException() != null) {
                TeiidSQLException exe = TeiidSQLException.create(resultsMsg.getException());
                BatchUpdateException batchUpdateException =
                        new BatchUpdateException(exe.getMessage(), exe.getSQLState(), exe.getErrorCode(), updateCounts, exe);
                this.updateCounts = null;
                throw batchUpdateException;
            }
        } else {
            createResultSet(resultsMsg);
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(JDBCPlugin.Util.getString("MMStatement.Success_query", reqMessage.getCommandString()));
        }
    }

    protected RequestMessage createRequestMessage(String[] commands,
                                                  boolean isBatchedCommand, RequestMessage.ResultsMode resultsMode) {
        RequestMessage reqMessage = new RequestMessage();
        reqMessage.setCommands(commands);
        reqMessage.setBatchedUpdate(isBatchedCommand);
        reqMessage.setResultsMode(resultsMode);
        return reqMessage;
    }

    public int getFetchDirection() {
        return this.fetchDirection;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public int getMaxFieldSize() {
        return maxFieldSize;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public boolean getMoreResults() throws SQLException {
        return getMoreResults(Statement.CLOSE_CURRENT_RESULT);
    }

    public boolean getMoreResults(int current) throws SQLException {
        checkStatement();

        if ((current == CLOSE_ALL_RESULTS || current == CLOSE_CURRENT_RESULT) && resultSet != null) {
            resultSet.close();
            resultSet = null;
        }

        // indicate that there are no more results
        this.updateCounts = null;
        return false;
    }

    public int getQueryTimeout() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        return (int) this.queryTimeoutMS / 1000;
    }

    public ResultSetImpl getResultSet() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (!hasResultSet()) {
            return null;
        }
        return resultSet;
    }

    public int getResultSetConcurrency() {
        return this.resultSetConcurrency;
    }

    public int getResultSetType() {
        return this.resultSetType;
    }

    public int getUpdateCount() throws SQLException {
        checkStatement();
        if (this.updateCounts == null) {
            return -1;
        }
        if (this.updateCounts.length == 0) {
            return 0;
        }
        return this.updateCounts[0];
    }

    protected void accumulateWarnings(List<Throwable> serverWarnings) {
        if (serverWarnings == null || serverWarnings.isEmpty()) {
            return;
        }
        if (this.serverWarnings == null) {
            this.serverWarnings = new ArrayList<>();
        }
        this.serverWarnings.addAll(serverWarnings);
    }

    public SQLWarning getWarnings() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();

        if (serverWarnings != null && !serverWarnings.isEmpty()) {
            return WarningUtil.convertWarnings(serverWarnings);
        }
        return null;
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        // do nothing, escape processing is always enabled.
    }

    public void setFetchDirection(int direction) throws SQLException {
        checkStatement();
    }

    public void setFetchSize(int rows) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (rows < 0) {
            String msg = JDBCPlugin.Util.getString("MMStatement.Invalid_fetch_size");
            throw new TeiidSQLException(msg);
        }
        // sets the fetch size on this statement
        if (rows == 0) {
            this.fetchSize = BaseDataSource.DEFAULT_FETCH_SIZE;
        } else {
            this.fetchSize = rows;
        }
    }

    public void setMaxRows(int maxRows) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (maxRows < 0 || maxRows == Integer.MAX_VALUE) {
            maxRows = 0;
        }
        this.maxRows = maxRows;
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (seconds >= 0) {
            queryTimeoutMS = seconds * 1000L;
        } else {
            throw new TeiidSQLException(JDBCPlugin.Util.getString("MMStatement.Bad_timeout_value"));
        }
    }

    void setQueryTimeoutMS(int queryTimeoutMS) {
        this.queryTimeoutMS = queryTimeoutMS;
    }

    /**
     * Helper method for copy the connection properties to request message.
     *
     * @param res Request message that these properties to be copied to.
     */
    protected void copyPropertiesToRequest(RequestMessage res) throws TeiidSQLException {
        // Get partial mode
        String partial = getExecutionProperty(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE);
        res.setPartialResults(Boolean.parseBoolean(partial));

        // Get transaction auto-wrap mode
        String txnAutoWrapMode = getExecutionProperty(ExecutionProperties.PROP_TXN_AUTO_WRAP);
        try {
            res.setTxnAutoWrapMode(txnAutoWrapMode);
        } catch (TeiidProcessingException e) {
            throw TeiidSQLException.create(e);
        }

        // Get result set cache mode
        String rsCache = getExecutionProperty(ExecutionProperties.RESULT_SET_CACHE_MODE);
        res.setUseResultSetCache(Boolean.parseBoolean(rsCache));

        res.setAnsiQuotedIdentifiers(Boolean.parseBoolean(
                getExecutionProperty(ExecutionProperties.ANSI_QUOTED_IDENTIFIERS)));
        String showPlan = getExecutionProperty(ExecutionProperties.SQL_OPTION_SHOWPLAN);
        if (showPlan != null) {
            try {
                res.setShowPlan(RequestMessage.ShowPlan.valueOf(showPlan.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignored
            }
        }
        String noExec = getExecutionProperty(ExecutionProperties.NOEXEC);
        if (noExec != null) {
            res.setNoExec(noExec.equalsIgnoreCase("ON"));
        }
    }

    /**
     * Ends the command and sets the status to TIMED_OUT.
     */
    protected synchronized void timeoutOccurred() {
        if (this.commandStatus != State.RUNNING) {
            return;
        }
        logger.warning(JDBCPlugin.Util.getString("MMStatement.Timeout_ocurred_in_Statement."));
        try {
            cancel();
            commandStatus = State.TIMED_OUT;
            queryTimeoutMS = NO_TIMEOUT;
            setTimeoutFromProperties();
            currentRequestID = -1;
            if (this.resultSet != null) {
                this.resultSet.close();
            }
        } catch (SQLException se) {
            logger.log(Level.FINE, JDBCPlugin.Util.getString("MMStatement.Error_timing_out."), se);
        }
    }

    public void setPayload(Serializable payload) {
        this.payload = payload;
    }

    public void setExecutionProperty(String name, String value) {
        this.execProps.setProperty(name, value);
    }

    public String getExecutionProperty(String name) {
        return this.execProps.getProperty(name);
    }

    long getCurrentRequestID() {
        return this.currentRequestID;
    }

    public PlanNode getPlanDescription() {
        if (this.resultSet != null) {
            return this.resultSet.getUpdatedPlanDescription();
        }
        if (currentPlanDescription != null) {
            return this.currentPlanDescription;
        }
        return null;
    }

    public String getDebugLog() {
        return this.debugLog;
    }

    public Collection<Annotation> getAnnotations() {
        return this.annotations;
    }

    public String getRequestIdentifier() {
        if (this.currentRequestID >= 0) {
            return Long.toString(this.currentRequestID);
        }
        return null;
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    protected void setAnalysisInfo(ResultsMessage resultsMsg) {
        if (resultsMsg.getDebugLog() != null) {
            this.debugLog = resultsMsg.getDebugLog();
        }
        if (resultsMsg.getPlanDescription() != null) {
            this.currentPlanDescription = resultsMsg.getPlanDescription();
        }
        if (resultsMsg.getAnnotations() != null) {
            this.annotations = resultsMsg.getAnnotations();
        }
        this.driverConnection.setDebugLog(debugLog);
        this.driverConnection.setCurrentPlanDescription(currentPlanDescription);
        this.driverConnection.setAnnotations(annotations);
    }

    Calendar getDefaultCalendar() {
        if (defaultCalendar == null) {
            defaultCalendar = Calendar.getInstance();
        }
        return defaultCalendar;
    }

    void setDefaultCalendar(Calendar cal) {
        this.defaultCalendar = cal;
    }

    public boolean isPoolable() throws SQLException {
        checkStatement();
        return false;
    }

    public void setPoolable(boolean arg0) throws SQLException {
        checkStatement();
    }

    public ConnectionImpl getConnection() throws SQLException {
        return this.driverConnection;
    }

    public boolean execute(String sql, int autoGeneratedKeys)
            throws SQLException {
        executeSql(new String[]{sql}, false,
                RequestMessage.ResultsMode.EITHER, true, null,
                autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS);
        return hasResultSet();
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return execute(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public boolean execute(String sql, String[] columnNames)
            throws SQLException {
        return execute(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {
        executeSql(new String[]{sql}, false,
                RequestMessage.ResultsMode.UPDATECOUNT, true, null,
                autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS);
        if (this.updateCounts == null) {
            return 0;
        }
        return this.updateCounts[0];
    }

    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException {
        return executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException {
        return executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        if (this.updateCounts != null && this.resultSet != null) {
            return this.resultSet;
        }
        return createResultSet(Collections.emptyList(), new Map[0]);
    }

    public int getResultSetHoldability() throws SQLException {
        throw SqlUtil.createFeatureNotSupportedException();
    }

    public void setCursorName(String name) throws SQLException {
        throw SqlUtil.createFeatureNotSupportedException();
    }

    public void setMaxFieldSize(int max) throws SQLException {
        checkStatement();
        if (max < 0) {
            throw new TeiidSQLException(JDBCPlugin.Util.getString("MMStatement.Invalid_field_size", max));
        }
        this.maxFieldSize = max;
    }

    ResultSetImpl createResultSet(List records, String[] columnNames, String[] dataTypes) throws SQLException {
        Map[] metadata = createMetadataMap(columnNames, dataTypes);
        return createResultSet(records, metadata);
    }

    private Map[] createMetadataMap(String[] columnNames, String[] dataTypes)
            throws SQLException {
        Map[] metadata = new Map[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            metadata[i] = getColumnMetadata(null, columnNames[i], dataTypes[i],
                    ResultsMetadataConstants.NULL_TYPES.UNKNOWN, driverConnection);
        }
        return metadata;
    }

    ResultSetImpl createResultSet(List records, Map[] columnMetadata) throws SQLException {
        ResultSetMetaData rsmd = createResultSetMetaData(columnMetadata);

        return createResultSet(records, rsmd);
    }

    private ResultSetMetaData createResultSetMetaData(Map[] columnMetadata) {
        return new ResultSetMetaDataImpl(new MetadataProvider(columnMetadata),
                this.getExecutionProperty(ExecutionProperties.JDBC4COLUMNNAMEANDLABELSEMANTICS));
    }

    ResultSetImpl createResultSet(List records, ResultSetMetaData rsmd) throws SQLException {
        if (rsmd.getColumnCount() > 0) {
            rsmd.getScale(1); //force the load of the metadata
        }
        ResultsMessage resultsMsg = createDummyResultsMessage(null, null, records);
        resultSet = new ResultSetImpl(resultsMsg, this, rsmd, 0);
        resultSet.setMaxFieldSize(this.maxFieldSize);
        return resultSet;
    }

    static ResultsMessage createDummyResultsMessage(String[] columnNames, String[] dataTypes, List records) {
        ResultsMessage resultsMsg = new ResultsMessage();
        resultsMsg.setColumnNames(columnNames);
        resultsMsg.setDataTypes(dataTypes);
        resultsMsg.setFirstRow(1);
        resultsMsg.setLastRow(records.size());
        resultsMsg.setFinalRow(records.size());
        resultsMsg.setResults((List[]) records.toArray(new List[0]));
        return resultsMsg;
    }

    static Map<Integer, Object> getColumnMetadata(
            String tableName,
            String columnName,
            String dataType,
            Integer nullable,
            ConnectionImpl driverConnection) throws SQLException {
        return getColumnMetadata(
                tableName,
                columnName,
                dataType,
                nullable,
                ResultsMetadataConstants.SEARCH_TYPES.UNSEARCHABLE,
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.FALSE,
                driverConnection);
    }

    static Map<Integer, Object> getColumnMetadata(
            String tableName,
            String columnName,
            String dataType,
            Integer nullable,
            Integer searchable,
            Boolean writable,
            Boolean signed,
            Boolean caseSensitive,
            ConnectionImpl driverConnection) throws SQLException {

        // map that would contain metadata details
        Map<Integer, Object> metadataMap = new HashMap<>();

        /* ******************************************************
         HardCoding Column metadata details for the given column
        * *******************************************************/

        metadataMap.put(ResultsMetadataConstants.VIRTUAL_DATABASE_NAME, driverConnection.getVDBName());
        metadataMap.put(ResultsMetadataConstants.GROUP_NAME, tableName);
        metadataMap.put(ResultsMetadataConstants.ELEMENT_NAME, columnName);
        metadataMap.put(ResultsMetadataConstants.DATA_TYPE, dataType);
        metadataMap.put(ResultsMetadataConstants.PRECISION, JDBCSQLTypeInfo.getDefaultPrecision(dataType));
        metadataMap.put(ResultsMetadataConstants.RADIX, 10);
        metadataMap.put(ResultsMetadataConstants.SCALE, 0);
        metadataMap.put(ResultsMetadataConstants.AUTO_INCREMENTING, Boolean.FALSE);
        metadataMap.put(ResultsMetadataConstants.CASE_SENSITIVE, caseSensitive);
        metadataMap.put(ResultsMetadataConstants.NULLABLE, nullable);
        metadataMap.put(ResultsMetadataConstants.SEARCHABLE, searchable);
        metadataMap.put(ResultsMetadataConstants.SIGNED, signed);
        metadataMap.put(ResultsMetadataConstants.WRITABLE, writable);
        metadataMap.put(ResultsMetadataConstants.CURRENCY, Boolean.FALSE);
        metadataMap.put(ResultsMetadataConstants.DISPLAY_SIZE, JDBCSQLTypeInfo.getMaxDisplaySize(dataType));

        return metadataMap;
    }

    public void closeOnCompletion() {
        this.closeOnCompletion = true;
    }

    public boolean isCloseOnCompletion() {
        return closeOnCompletion;
    }
}