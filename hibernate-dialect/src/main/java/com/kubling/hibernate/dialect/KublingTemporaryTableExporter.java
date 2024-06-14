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

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.temptable.TemporaryTable;
import org.hibernate.dialect.temptable.TemporaryTableColumn;
import org.hibernate.dialect.temptable.TemporaryTableExporter;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.function.Function;

public class KublingTemporaryTableExporter implements TemporaryTableExporter {

    private final Dialect dialect;

    public KublingTemporaryTableExporter(Dialect dialect) {
        this.dialect = dialect;
    }

    protected String getCreateCommand() {
        return dialect.getTemporaryTableCreateCommand();
    }

    protected String getCreateOptions() {
        return dialect.getTemporaryTableCreateOptions();
    }

    protected String getDropCommand() {
        return dialect.getTemporaryTableDropCommand();
    }

    protected String getTruncateTableCommand() {
        return dialect.getTemporaryTableTruncateCommand();
    }

    @Override
    public String getSqlCreateCommand(TemporaryTable temporaryTable) {
        final StringBuilder buffer = new StringBuilder( getCreateCommand()).append( ' ');

        buffer.append(determineTableName(temporaryTable.getQualifiedTableName()));
        buffer.append('(');

        for ( TemporaryTableColumn column : temporaryTable.getColumns()) {
            buffer.append( column.getColumnName()).append(' ');
            final int sqlTypeCode = column.getJdbcMapping().getJdbcType().getDefaultSqlTypeCode();
            final String databaseTypeName = column.getSqlTypeDefinition();

            buffer.append(databaseTypeName);

            final String columnAnnotation = dialect.getCreateTemporaryTableColumnAnnotation( sqlTypeCode);
            if ( !columnAnnotation.isEmpty()) {
                buffer.append(' ').append( columnAnnotation);
            }

            if ( column.isNullable()) {
                final String nullColumnString = dialect.getNullColumnString(databaseTypeName);
                if (!databaseTypeName.contains(nullColumnString)) {
                    buffer.append(nullColumnString);
                }
            }
            else {
                buffer.append(" not null");
            }
            buffer.append(", ");
        }
        if (dialect.supportsTemporaryTablePrimaryKey()) {
            buffer.append("primary key (");
            for (TemporaryTableColumn column : temporaryTable.getColumns()) {
                if (column.isPrimaryKey()) {
                    buffer.append(column.getColumnName());
                    buffer.append(", ");
                }
            }
            buffer.setLength(buffer.length() - 2);
            buffer.append(')');
        }
        else {
            buffer.setLength(buffer.length() - 2);
        }
        buffer.append(')');

        final String createOptions = getCreateOptions();
        if (createOptions != null) {
            buffer.append(' ').append(createOptions);
        }

        return buffer.toString();
    }

    @Override
    public String getSqlDropCommand(TemporaryTable idTable) {
        return getDropCommand() + " " + determineTableName(idTable.getQualifiedTableName());
    }

    @Override
    public String getSqlTruncateCommand(
            TemporaryTable idTable,
            Function<SharedSessionContractImplementor, String> sessionUidAccess,
            SharedSessionContractImplementor session) {
        if (idTable.getSessionUidColumn() != null) {
            return getTruncateTableCommand() + " " + determineTableName(idTable.getQualifiedTableName())
                    + " where " + idTable.getSessionUidColumn().getColumnName() + " = ?";
        }
        else {
            return getTruncateTableCommand() + " " + determineTableName(idTable.getQualifiedTableName());
        }
    }

    private static String determineTableName(String tableName) {
        if (tableName.contains(".")) {
            // it is unfortunate that entityDescriptor is not exposed in TemporaryTable, therefore we can't
            // determine whether the catalog was added to the name, so here we can only assume.
            // If name contains dots '.' we assume that the string between the start and the first dot is actually
            // the catalog name and therefore we remove, since Teiid does not support schema based temp tables
            // TODO document this somewhere
            tableName = tableName.substring(tableName.indexOf('.')+1);
        }
        return tableName;
    }
}
