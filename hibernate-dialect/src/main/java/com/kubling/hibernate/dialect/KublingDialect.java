/*
 * Copyright Kubling and/or its affiliates
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

package com.kubling.hibernate.dialect;

import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.pagination.AbstractSimpleLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.dialect.temptable.TemporaryTableExporter;
import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.StandardBasicTypes;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hibernate.type.SqlTypes.*;

public class KublingDialect extends Dialect {

    public KublingDialect() {
        super();
    }

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {

        super.initializeFunctionRegistry(functionContributions);
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();

        // Mathematical functions
        functionRegistry.register("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        functionRegistry.register("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        functionRegistry.register("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        functionRegistry.register("atan2", new StandardSQLFunction("atan2", StandardBasicTypes.DOUBLE));
        functionRegistry.register("ceil", new StandardSQLFunction("ceiling"));
        functionRegistry.register("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        functionRegistry.register("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        functionRegistry.register("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        functionRegistry.register("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        functionRegistry.register("floor", new StandardSQLFunction("floor"));
        functionRegistry.register("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        functionRegistry.register("mod", new StandardSQLFunction("mod"));
        functionRegistry.register("pi", new StandardSQLFunction("pi", StandardBasicTypes.DOUBLE));
        functionRegistry.register("power", new StandardSQLFunction("power", StandardBasicTypes.DOUBLE));
        functionRegistry.register("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        functionRegistry.register("round", new StandardSQLFunction("round"));
        functionRegistry.register("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        functionRegistry.register("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        functionRegistry.register("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));

        // String functions
        functionRegistry.register("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        functionRegistry.register("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        functionRegistry.register("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));
        functionRegistry.register("initcap", new StandardSQLFunction("initcap", StandardBasicTypes.STRING));
        functionRegistry.register("insert", new StandardSQLFunction("insert", StandardBasicTypes.STRING));
        functionRegistry.register("lcase", new StandardSQLFunction("lcase", StandardBasicTypes.STRING));
        functionRegistry.register("left", new StandardSQLFunction("left", StandardBasicTypes.STRING));
        functionRegistry.register("locate", new StandardSQLFunction("locate", StandardBasicTypes.INTEGER));
        functionRegistry.register("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));
        functionRegistry.register("ltrim", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING));
        functionRegistry.register("repeat", new StandardSQLFunction("repeat", StandardBasicTypes.STRING));
        functionRegistry.register("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        functionRegistry.register("right", new StandardSQLFunction("right", StandardBasicTypes.STRING));
        functionRegistry.register("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));
        functionRegistry.register("rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));
        functionRegistry.register("substring", new StandardSQLFunction("substring", StandardBasicTypes.STRING));
        functionRegistry.register("translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING));
        functionRegistry.register("ucase", new StandardSQLFunction("ucase", StandardBasicTypes.STRING));

        // Date/Time functions
        functionRegistry.register("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        functionRegistry.register("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        functionRegistry.register("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        functionRegistry.register("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        functionRegistry.register("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));
        functionRegistry.register("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        functionRegistry.register("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER));
        functionRegistry.register("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        functionRegistry.register("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));

        // Custom functions
        functionRegistry.register("formatbigdecimal", new StandardSQLFunction("formatbigdecimal", StandardBasicTypes.STRING));
        functionRegistry.register("formatbiginteger", new StandardSQLFunction("formatbiginteger", StandardBasicTypes.STRING));
        functionRegistry.register("formatdouble", new StandardSQLFunction("formatdouble", StandardBasicTypes.STRING));
        functionRegistry.register("formatfloat", new StandardSQLFunction("formatfloat", StandardBasicTypes.STRING));
        functionRegistry.register("formatinteger", new StandardSQLFunction("formatinteger", StandardBasicTypes.STRING));
        functionRegistry.register("formatlong", new StandardSQLFunction("formatlong", StandardBasicTypes.STRING));
        functionRegistry.register("convert", new StandardSQLFunction("convert"));
        functionRegistry.register("uuid", new StandardSQLFunction("uuid", StandardBasicTypes.STRING));
        functionRegistry.register("unescape", new StandardSQLFunction("unescape", StandardBasicTypes.STRING));
        functionRegistry.register("modifytimezone", new StandardSQLFunction("modifytimezone", StandardBasicTypes.TIMESTAMP));
        functionRegistry.register("session_id", new StandardSQLFunction("session_id", StandardBasicTypes.STRING));

        // Binary and array functions
        functionRegistry.register("to_bytes", new StandardSQLFunction("to_bytes", StandardBasicTypes.BLOB));
        functionRegistry.register("to_chars", new StandardSQLFunction("to_chars", StandardBasicTypes.CLOB));
        functionRegistry.register("array_get", new StandardSQLFunction("array_get", StandardBasicTypes.OBJECT_TYPE));
        functionRegistry.register("array_length", new StandardSQLFunction("array_length", StandardBasicTypes.INTEGER));

        // Document functions
        functionRegistry.register("jsonParse", new StandardSQLFunction("jsonParse", StandardBasicTypes.CLOB));
        functionRegistry.register("jsonObject", new StandardSQLFunction("jsonObject", StandardBasicTypes.CLOB));
        functionRegistry.register("yamlAsJSON", new StandardSQLFunction("yamlAsJSON", StandardBasicTypes.CLOB));
        functionRegistry.register("jsonPath", new StandardSQLFunction("jsonPath", StandardBasicTypes.CLOB));
        functionRegistry.register("jsonJq", new StandardSQLFunction("jsonJq", StandardBasicTypes.CLOB));
        functionRegistry.register("jsonPathAsString", new StandardSQLFunction("jsonPathAsString", StandardBasicTypes.STRING));
        functionRegistry.register("jsonJqAsString", new StandardSQLFunction("jsonJqAsString", StandardBasicTypes.STRING));
    }

    @Override
    protected String columnType(int sqlTypeCode) {

        switch (sqlTypeCode) {
            case BIT:
                return "boolean";
            case TINYINT:
                return "byte";
            case SMALLINT:
                return "short";
            case SqlTypes.INTEGER:
                return "integer";
            case CHAR:
                return "char";
            case NCHAR:
                return columnType(CHAR);
            case NVARCHAR:
                return columnType(VARCHAR);
            case LONG32VARCHAR:
            case LONG32NVARCHAR:
            case VARCHAR:
                return "string";
            case SqlTypes.BLOB:
            case BINARY:
            case LONG32VARBINARY:
                return "blob";
            case SqlTypes.CLOB:
            case NCLOB:
                return "clob";
            // use bytea as the "long" binary type (that there is no
            // real VARBINARY type in Postgres, so we always use this)
            case VARBINARY:
                return "varbinary";

            case BIGINT:
                return "long";

            case TIMESTAMP_UTC:
            case TIMESTAMP:
                return "timestamp";
            case SqlTypes.TIME:
                return "time";
            case SqlTypes.DATE:
                return "date";
            case JAVA_OBJECT:
                return "object";
            case REAL:
            case SqlTypes.FLOAT:
                return "float";
            case SqlTypes.DOUBLE:
                return "double";
            case NUMERIC:
                return "bigdecimal";
        }
        return super.columnType(sqlTypeCode);
    }

    public boolean dropConstraints() {
        return false;
    }

    public boolean hasAlterTable() {
        return false;
    }

    public boolean supportsColumnCheck() {
        return false;
    }

    public boolean supportsCascadeDelete() {
        return false;
    }

    public String getCurrentTimestampSQLFunctionName() {
        return "now";
    }

    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    public boolean supportsLimit() {
        return true;
    }

    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    public boolean supportsTableCheck() {
        return false;
    }

    public boolean supportsUnique() {
        return false;
    }

    public String toBooleanValueString(boolean arg0) {
        if (arg0) {
            return "{b'true'}";
        }
        return "{b'false'}";
    }

    @Override
    public LimitHandler getLimitHandler() {
        return new AbstractSimpleLimitHandler() {
            @Override
            protected String limitClause(boolean hasFirstRow) {
                return hasFirstRow ? " limit ?, ?" : " limit ?";
            }
        };
    }

    /**
     * @see Dialect#getResultSet(CallableStatement)
     */
    public ResultSet getResultSet(CallableStatement ps) throws SQLException {
        boolean isResultSet = ps.execute();
        while (!isResultSet && ps.getUpdateCount() != -1) {
            isResultSet = ps.getMoreResults();
        }
        return ps.getResultSet();
    }

    /**
     * @see Dialect#registerResultSetOutParameter(CallableStatement, int)
     */
    public int registerResultSetOutParameter(CallableStatement statement, int col) {
        return col;
    }

    public String getForUpdateNowaitString() {
        return "";
    }

    public String getForUpdateNowaitString(String aliases) {
        return "";
    }

    public String getForUpdateString() {
        return "";
    }

    public String getForUpdateString(LockMode lockMode) {
        return "";
    }

    public String getForUpdateString(String aliases) {
        return "";
    }

    @Override
    public String getSelectGUIDString() {
        return "select uuid()";
    }

    @Override
    public SequenceSupport getSequenceSupport() {
        return new SequenceSupport() {
            @Override
            public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
                return sequenceName + "_nextval()";
            }

            @Override
            public boolean supportsPooledSequences() {
                return true;
            }

            @Override
            public String getSequenceNextValString(String sequenceName) throws MappingException {
                return "select " + getSelectSequenceNextValString(sequenceName);
            }
        };
    }

    @Override
    public String getTemporaryTableCreateCommand() {
        final TemporaryTableKind kind = getSupportedTemporaryTableKind();
        switch (kind) {
            case PERSISTENT:
                return "create temporary table";
            case LOCAL:
                return "create local temporary table";
            case GLOBAL:
                return "create global temporary table";
        }
        throw new UnsupportedOperationException("Unsupported kind: " + kind);
    }

    @Override
    public TemporaryTableExporter getTemporaryTableExporter() {
        return new KublingTemporaryTableExporter(this);
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.SCHEMA;
    }
}

