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
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.pagination.AbstractSimpleLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.dialect.temptable.TemporaryTableExporter;
import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.query.spi.QueryEngine;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.StandardBasicTypes;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hibernate.type.SqlTypes.*;

public class KublingDialect extends Dialect {

    @Override
    public void initializeFunctionRegistry(QueryEngine queryEngine) {
        super.initializeFunctionRegistry(queryEngine);
        queryEngine.getSqmFunctionRegistry()
                .register("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("atan2", new StandardSQLFunction("atan2", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("ceil", new StandardSQLFunction("ceiling"));
        queryEngine.getSqmFunctionRegistry()
                .register("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("floor", new StandardSQLFunction("floor"));
        queryEngine.getSqmFunctionRegistry()
                .register("formatbigdecimal",
                        new StandardSQLFunction("formatbigdecimal", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("formatbiginteger",
                        new StandardSQLFunction("formatbiginteger", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("formatdouble",
                        new StandardSQLFunction("formatdouble", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("formatfloat",
                        new StandardSQLFunction("formatfloat", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("formatinteger",
                        new StandardSQLFunction("formatinteger", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("formatlong",
                        new StandardSQLFunction("formatlong", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("mod", new StandardSQLFunction("mod"));
        queryEngine.getSqmFunctionRegistry()
                .register("parsebigdecimal",
                        new StandardSQLFunction("parsebigdecimal", StandardBasicTypes.BIG_DECIMAL));
        queryEngine.getSqmFunctionRegistry()
                .register("parsebiginteger",
                        new StandardSQLFunction("parsebiginteger", StandardBasicTypes.BIG_INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("parsedouble",
                        new StandardSQLFunction("parsedouble", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("parsefloat",
                        new StandardSQLFunction("parsefloat", StandardBasicTypes.FLOAT));
        queryEngine.getSqmFunctionRegistry()
                .register("parseinteger",
                        new StandardSQLFunction("parseinteger", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("parselong",
                        new StandardSQLFunction("parselong", StandardBasicTypes.LONG));
        queryEngine.getSqmFunctionRegistry()
                .register("pi", new StandardSQLFunction("pi", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("power", new StandardSQLFunction("power", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("round", new StandardSQLFunction("round"));
        queryEngine.getSqmFunctionRegistry()
                .register("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        queryEngine.getSqmFunctionRegistry()
                .register("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));

        queryEngine.getSqmFunctionRegistry()
                .register("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        queryEngine.getSqmFunctionRegistry()
                .register("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));
        queryEngine.getSqmFunctionRegistry()
                .register("initcap", new StandardSQLFunction("initcap", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("insert", new StandardSQLFunction("insert", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("lcase", new StandardSQLFunction("lcase", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("left", new StandardSQLFunction("left", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("locate", new StandardSQLFunction("locate", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("ltrim", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("repeat", new StandardSQLFunction("repeat", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("right", new StandardSQLFunction("right", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("substring",
                        new StandardSQLFunction("substring", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("translate",
                        new StandardSQLFunction("translate", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("ucase", new StandardSQLFunction("ucase", StandardBasicTypes.STRING));

        queryEngine.getSqmFunctionRegistry()
                .register("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("dayofmonth",
                        new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("dayofweek",
                        new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("dayofyear",
                        new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("formatdate",
                        new StandardSQLFunction("formatdate", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("formattime",
                        new StandardSQLFunction("formattime", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("formattimestamp",
                        new StandardSQLFunction("formattimestamp", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("monthname",
                        new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("parsedate",
                        new StandardSQLFunction("parsedate", StandardBasicTypes.DATE));
        queryEngine.getSqmFunctionRegistry()
                .register("parsetime",
                        new StandardSQLFunction("parsetime", StandardBasicTypes.TIME));
        queryEngine.getSqmFunctionRegistry()
                .register("parsetimestamp",
                        new StandardSQLFunction("parsetimestamp", StandardBasicTypes.TIMESTAMP));
        queryEngine.getSqmFunctionRegistry()
                .register("second",
                        new StandardSQLFunction("second", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("timestampcreate",
                        new StandardSQLFunction("timestampcreate", StandardBasicTypes.TIMESTAMP));
        queryEngine.getSqmFunctionRegistry()
                .register("timestampAdd", new StandardSQLFunction("timestampAdd"));
        queryEngine.getSqmFunctionRegistry()
                .register("timestampDiff",
                        new StandardSQLFunction("timestampDiff", StandardBasicTypes.LONG));
        queryEngine.getSqmFunctionRegistry()
                .register("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        queryEngine.getSqmFunctionRegistry()
                .register("modifytimezone",
                        new StandardSQLFunction("modifytimezone", StandardBasicTypes.TIMESTAMP));

        queryEngine.getSqmFunctionRegistry()
                .register("convert", new StandardSQLFunction("convert"));

        queryEngine.getSqmFunctionRegistry()
                .register("to_bytes", new StandardSQLFunction("to_bytes", StandardBasicTypes.BLOB));
        queryEngine.getSqmFunctionRegistry()
                .register("to_chars", new StandardSQLFunction("to_chars", StandardBasicTypes.CLOB));
        queryEngine.getSqmFunctionRegistry()
                .register("from_unittime",
                        new StandardSQLFunction("from_unittime", StandardBasicTypes.TIMESTAMP));
        queryEngine.getSqmFunctionRegistry()
                .register("session_id",
                        new StandardSQLFunction("session_id", StandardBasicTypes.STRING));

        queryEngine.getSqmFunctionRegistry()
                .register("uuid", new StandardSQLFunction("uuid", StandardBasicTypes.STRING));
        queryEngine.getSqmFunctionRegistry()
                .register("unescape",
                        new StandardSQLFunction("unescape", StandardBasicTypes.STRING));

        queryEngine.getSqmFunctionRegistry()
                .register("array_get",
                        new StandardSQLFunction("array_get", StandardBasicTypes.OBJECT_TYPE));
        queryEngine.getSqmFunctionRegistry()
                .register("array_length",
                        new StandardSQLFunction("array_length", StandardBasicTypes.INTEGER));
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
                return columnType( CHAR );
            case NVARCHAR:
                return columnType( VARCHAR );
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
        return super.columnType( sqlTypeCode );
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
        return "now"; //$NON-NLS-1$
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

    public boolean supportsUnionAll() {
        return true;
    }

    public boolean supportsUnique() {
        return false;
    }

    public String toBooleanValueString(boolean arg0) {
        if (arg0) {
            return "{b'true'}"; //$NON-NLS-1$
        }
        return "{b'false'}"; //$NON-NLS-1$
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
        ResultSet rs = ps.getResultSet();
        return rs;
    }

    /**
     * @see Dialect#registerResultSetOutParameter(CallableStatement, int)
     */
    public int registerResultSetOutParameter(CallableStatement statement,
                                             int col) throws SQLException {
        return col;
    }

    public String getForUpdateNowaitString() {
        return ""; //$NON-NLS-1$
    }

    public String getForUpdateNowaitString(String aliases) {
        return "";         //$NON-NLS-1$
    }

    public String getForUpdateString() {
        return ""; //$NON-NLS-1$
    }

    public String getForUpdateString(LockMode lockMode) {
        return ""; //$NON-NLS-1$
    }

    public String getForUpdateString(String aliases) {
        return ""; //$NON-NLS-1$
    }

    @Override
    public String getSelectGUIDString() {
        return "select uuid()"; //$NON-NLS-1$
    }

    @Override
    public SequenceSupport getSequenceSupport() {
        return new SequenceSupport() {
            @Override
            public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
                return sequenceName + "_nextval()";
            }

            @Override
            public boolean supportsSequences() {
                return true;
            }

            @Override
            public boolean supportsPooledSequences() {
                return true;
            }

            @Override
            public String getSequenceNextValString(String sequenceName) throws MappingException {
                return "select " + getSelectSequenceNextValString( sequenceName );
            }
        };
    }

    @Override
    public String getTemporaryTableCreateCommand() {
        final TemporaryTableKind kind = getSupportedTemporaryTableKind();
        switch ( kind ) {
            case PERSISTENT:
                return "create temporary table";
            case LOCAL:
                return "create local temporary table";
            case GLOBAL:
                return "create global temporary table";
        }
        throw new UnsupportedOperationException( "Unsupported kind: " + kind );
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

