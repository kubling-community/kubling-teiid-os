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
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.collections.Stack;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.sql.ast.Clause;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.SqlAstTranslatorFactory;
import org.hibernate.sql.ast.spi.AbstractSqlAstTranslator;
import org.hibernate.sql.ast.tree.MutationStatement;
import org.hibernate.sql.ast.tree.Statement;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.SqlTuple;
import org.hibernate.sql.ast.tree.predicate.InListPredicate;
import org.hibernate.sql.ast.tree.select.SelectStatement;
import org.hibernate.sql.exec.spi.JdbcOperation;
import org.hibernate.sql.exec.spi.JdbcOperationQueryMutation;
import org.hibernate.sql.exec.spi.JdbcOperationQuerySelect;
import org.hibernate.sql.exec.spi.JdbcParameterBindings;
import org.hibernate.sql.model.ast.TableMutation;
import org.hibernate.sql.model.jdbc.JdbcMutationOperation;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.StandardBasicTypes;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

        return switch (sqlTypeCode) {
            case BIT -> "boolean";
            case TINYINT -> "byte";
            case SMALLINT -> "short";
            case SqlTypes.INTEGER -> "integer";
            case CHAR -> "char";
            case NCHAR -> columnType(CHAR);
            case NVARCHAR -> columnType(VARCHAR);
            case LONG32VARCHAR, LONG32NVARCHAR, VARCHAR -> "string";
            case SqlTypes.BLOB, BINARY, LONG32VARBINARY -> "blob";
            case SqlTypes.CLOB, NCLOB -> "clob";
            // use bytea as the "long" binary type (that there is no
            // real VARBINARY type in Postgres, so we always use this)
            case VARBINARY -> "varbinary";
            case BIGINT -> "long";
            case TIMESTAMP_UTC, TIMESTAMP -> "timestamp";
            case SqlTypes.TIME -> "time";
            case SqlTypes.DATE -> "date";
            case JAVA_OBJECT -> "object";
            case REAL, SqlTypes.FLOAT -> "float";
            case SqlTypes.DOUBLE -> "double";
            case NUMERIC -> "bigdecimal";
            default -> super.columnType(sqlTypeCode);
        };
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
        return switch (kind) {
            case PERSISTENT -> "create temporary table";
            case LOCAL -> "create local temporary table";
            case GLOBAL -> "create global temporary table";
        };
    }

    @Override
    public TemporaryTableExporter getTemporaryTableExporter() {
        return new KublingTemporaryTableExporter(this);
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.SCHEMA;
    }

    @Override
    public SqlAstTranslatorFactory getSqlAstTranslatorFactory() {
        return new SqlAstTranslatorFactory() {
            @Override
            public SqlAstTranslator<JdbcOperationQuerySelect> buildSelectTranslator(
                    SessionFactoryImplementor sessionFactory, SelectStatement statement) {
                return new KublingSqlAstTranslator<>(sessionFactory, statement);
            }

            @Override
            public SqlAstTranslator<? extends JdbcOperationQueryMutation> buildMutationTranslator(
                    SessionFactoryImplementor sessionFactory, MutationStatement statement) {
                return new KublingSqlAstTranslator<>(sessionFactory, statement);
            }

            @Override
            public <O extends JdbcMutationOperation> SqlAstTranslator<O> buildModelMutationTranslator(
                    TableMutation<O> mutation, SessionFactoryImplementor sessionFactory) {
                return new KublingSqlAstTranslator<>(sessionFactory, mutation);
            }
        };
    }

    private static class KublingSqlAstTranslator<T extends JdbcOperation> extends AbstractSqlAstTranslator<T> {

        public KublingSqlAstTranslator(SessionFactoryImplementor sessionFactory, Statement statement) {
            super(sessionFactory, statement);
        }

        @Override
        public void visitInListPredicate(InListPredicate predicate) {

            if (predicate.getTestExpression() instanceof SqlTuple) {
                List<? extends Expression> expressions = ((SqlTuple) predicate.getTestExpression()).getExpressions();
                List<Expression> values = predicate.getListExpressions();

                appendSql("(");
                for (int i = 0; i < values.size(); i++) {
                    if (i > 0) {
                        appendSql(" OR ");
                    }
                    appendSql("(");
                    Iterator<? extends Expression> columnIterator = expressions.iterator();
                    Iterator<? extends Expression> valueIterator = ((SqlTuple) values.get(i)).getExpressions().iterator();
                    while (columnIterator.hasNext() && valueIterator.hasNext()) {
                        columnIterator.next().accept(this);
                        appendSql(" = ");
                        valueIterator.next().accept(this);
                        if (columnIterator.hasNext()) {
                            appendSql(" AND ");
                        }
                    }
                    appendSql(")");
                }
                appendSql(")");
            } else {
                // ✅ Fallback for simple IN clauses
                super.visitInListPredicate(predicate);
            }
        }

        @Override
        public Stack<Clause> getCurrentClauseStack() {
            return super.getCurrentClauseStack();
        }


        @Override
        public Set<String> getAffectedTableNames() {
            return Set.of();
        }

        @Override
        public T translate(JdbcParameterBindings jdbcParameterBindings, QueryOptions queryOptions) {
            return super.translate(jdbcParameterBindings, queryOptions);
        }
    }
}

