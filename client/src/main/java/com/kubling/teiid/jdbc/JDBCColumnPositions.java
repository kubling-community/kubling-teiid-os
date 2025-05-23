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

/**
 * <p> This class provides constants indicating positions of columns in the
 * <code>ResultSets</code> returned by methods on <code>MMDatabaseMetaData</code>
 * object. The inner classes represent the methods while attributes represent the
 * column positions. The name of the constant explains the column content.
 * <p> The constants in the inner classes could include column positions for columns
 * that are hardcoded, columns positions of some columns on server's
 * <code>Results</code> object.
 * <p> Each of the inner classes have a constant <code>MAX_COLUMNS</code> that
 * represents the number of columns to be read from the server's <code>Results</code>
 * object.
 * <p> All the column positions are one based. </code>
 */

interface JDBCColumnPositions {

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface SCHEMAS {
        /**
         * Number of columns to be read from results returned getElements method.
         */
        int MAX_COLUMNS = 2;
        int TABLE_CATALOG = 2;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface CATALOGS {

        /**
         * Number of columns to be read from results returned getElements method.
         */
        int MAX_COLUMNS = 1;
    }


    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface COLUMNS {

        /**
         * Number of columns to be read from results returned getElements method.
         */
        int MAX_COLUMNS = 23;

        /**
         * Position of column that contains catalog name in which the table for the column is present.
         */
        int TABLE_CAT = 1;

        int TABLE_SCHEM = 2;

        int TABLE_NAME = 3;

        int COLUMN_NAME = 4;

        /**
         * Position of column that contains SQL type from java.sql.Types for column's data type.
         */
        int DATA_TYPE = 5;

        /**
         * Position of column that contains local type name used by the data source.
         */
        int TYPE_NAME = 6;

        int COLUMN_SIZE = 7;

        /**
         * Position of column that is not used will contain nulls
         */
        int BUFFER_LENGTH = 8;

        int DECIMAL_DIGITS = 9;

        int NUM_PREC_RADIX = 10;

        /**
         * Position of column that has an int value indicating nullablity
         */
        int NULLABLE = 11;

        /**
         * Position of column containing explanatory notes.
         */
        int REMARKS = 12;

        int COLUMN_DEF = 13;

        /**
         * Position of column that not used will contain nulls
         */
        int SQL_DATA_TYPE = 14;

        /**
         * Position of column that not used will contain nulls
         */
        int SQL_DATETIME_SUB = 15;

        int CHAR_OCTET_LENGTH = 16;

        int ORDINAL_POSITION = 17;

        /**
         * Position of column that has a String value indicating nullablity
         */
        int IS_NULLABLE = 18;

        int SCOPE_CATALOG = 19;

        int SCOPE_SCHEMA = 20;

        int SCOPE_TABLE = 21;

        int SOURCE_DATA_TYPE = 22;

        int IS_AUTOINCREMENT = 23;

    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface TABLES {

        /**
         * Number of columns to be read from results returned getTables method.
         */
        int MAX_COLUMNS = 11;

        /**
         * Position of column that contains catalog name in which the table is present.
         */
        int TABLE_CAT = 1;
        int TYPE_CAT = 6;
        int TYPE_SCHEM = 7;
        int TYPE_NAME = 8;
        int SELF_REFERENCING_COL_NAME = 9;
        int REF_GENERATION = 10;
        int ISPHYSICAL = 11;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface TYPE_INFO {

        /**
         * Number of columns to be read from results returned getElements method.
         */
        int MAX_COLUMNS = 18;

        /**
         * Position of column that contains local type name used by the data source.
         */
        int TYPE_NAME = 1;

        /**
         * Position of column that contains SQL type from java.sql.Types for column's data type.
         */
        int DATA_TYPE = 2;

        int PRECISION = 3;
        /**
         * Position of column that contains prefix used to quote a literal.
         */
        int LITERAL_PREFIX = 4;

        /**
         * Position of column that contains suffix used to quote a literal.
         */
        int LITERAL_SUFFIX = 5;

        /**
         * Position of column that contains params used in creating the type.
         */
        int CREATE_PARAMS = 6;

        /**
         * Position of column that contains the nullable value.
         */
        int NULLABLE = 7;

        int CASE_SENSITIVE = 8;

        /**
         * Position of column that contains the searchable value.
         */
        int SEARCHABLE = 9;

        /**
         * Position of column that contains the unsigned value.
         */
        int UNSIGNED_ATTRIBUTE = 10;

        int FIXED_PREC_SCALE = 11;

        int AUTO_INCREMENT = 12;

        /**
         * Position of column that contains local type name used by the data source.
         */
        int LOCAL_TYPE_NAME = 13;

        /**
         * Position of column that contains the min scale value.
         */
        int MINIMUM_SCALE = 14;

        /**
         * Position of column that contains the max scale value.
         */
        int MAXIMUM_SCALE = 15;

        /**
         * Position of column that not used will contain nulls
         */
        int SQL_DATA_TYPE = 16;

        /**
         * Position of column that not used will contain nulls
         */
        int SQL_DATETIME_SUB = 17;

        int NUM_PREC_RADIX = 18;

        /**
         * Position of column in server's results containing name of the datatype.
         */
        int NAME = 19;

        /**
         * Position of column in server's results containing isSigned value.
         */
        int IS_SIGNED = 20;

        /**
         * Position of column in server's results containing nullType name.
         */
        int NULL_TYPE_NAME = 21;

        /**
         * Position of column in server's results containing search type name.
         */
        int SEARCH_TYPE_NAME = 22;
    }

    /**
     * This class contains constants representing column positions on ResultSet
     * returned by getUDTS method on DatabaseMetaData. These constant values
     * are be used to hardcode the column values used in constructin the ResultSet obj.
     */
    interface UDTS {

        /**
         * Number of columns to be read from results returned getUserDefinedTypes method.
         */
        int MAX_COLUMNS = 7;

        // name of the column containing table or Groups name in which UDTS are present.
        int TABLE_NAME = 3;

        // name of the column containing catalog or Virtual database name.
        int TYPE_CAT = 1;

        // name of the column containing schema or Virtual database version.
        int TYPE_SCHEM = 2;

        // name of the column containing name of type name column.
        int TYPE_NAME = 9;

        // name of the column containing class name column.
        int CLASS_NAME = 4;

        // name of the column containing name of sql datatype code column
        int DATA_TYPE = 5;

        // name of the column containing comments column
        int REMARKS = 6;

        int BASE_TYPE = 7;
        /**
         * Position of column in server's results containing java class name.
         */
        int JAVA_CLASS = 8;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getIndexInfo method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from the query against System.KeyElements table.
     */
    interface INDEX_INFO {

        /**
         * Number of columns to be read from results returned by server results.
         */
        int MAX_COLUMNS = 13;

        /**
         * Position of column that contains catalog name of the table.
         */
        int TABLE_CAT = 1;

        int TABLE_SCHEM = 2;

        int TABLE_NAME = 3;

        /**
         * Position of column that contains non uniqueness of the index.
         */
        int NON_UNIQUE = 4;

        /**
         * Position of column that contains qualifier for the index.
         */
        int INDEX_QUALIFIER = 5;

        int INDEX_NAME = 6;

        /**
         * Position of column that contains type of index.
         */
        int TYPE = 7;

        int ORDINAL_POSITION = 8;

        int COLUMN_NAME = 9;

        /**
         * Position of column that contains desc if index is ascending or descending.
         */
        int ASC_OR_DESC = 10;

        /**
         * Position of column that contains cardinality of the index.
         */
        int CARDINALITY = 11;

        /**
         * Position of column that contains pages oocupied by table.
         */
        int PAGES = 12;

        /**
         * Position of column that contains any filter condition.
         */
        int FILTER_CONDITION = 13;

    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface PRIMARY_KEYS {

        /**
         * Number of columns to be read from results returned by getPrimaryKeys.
         */
        int MAX_COLUMNS = 6;

        /**
         * Position of column that contains catalog name of the primaryTable.
         */
        int TABLE_CAT = 1;
        int TABLE_SCHEM = 2;
        int TABLE_NAME = 3;
        int COLUMN_NAME = 4;
        int KEY_SEQ = 5;
        int PK_NAME = 6;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getCrossReferences method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getCrossReferences method on server's <code>Metadata object</code>.
     */
    interface REFERENCE_KEYS {

        /**
         * Number of columns to be read from results returned any of the 3 methods.
         */
        int MAX_COLUMNS = 14;

        /**
         * Position of column that contains catalog name of the primaryTable.
         */
        int PKTABLE_CAT = 1;

        /**
         * Position of column that contains scheam name of the primaryTable.
         */
        int PKTABLE_SCHEM = 2;

        int PKTABLE_NAME = 3;

        int PKCOLUMN_NAME = 4;

        /**
         * Position of column that contains catalog name of the foreignTable.
         */
        int FKTABLE_CAT = 5;

        /**
         * Position of column that contains schema name of the foreignTable.
         */
        int FKTABLE_SCHEM = 6;

        int FKTABLE_NAME = 7;

        int FKCOLUMN_NAME = 8;

        int KEY_SEQ = 9;

        /**
         * Position of column that determines how foreign key changes if PK is updated.
         */
        int UPDATE_RULE = 10;

        /**
         * Position of column that determines how foreign key changes if PK is deleted.
         */
        int DELETE_RULE = 11;

        int FK_NAME = 12;

        int PK_NAME = 13;

        /**
         * Position of column that determines if foreign key constraints can be deferred until commit.
         */
        int DEFERRABILITY = 14;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getProcedures method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface PROCEDURES {

        /**
         * Number of columns to be read from results returned getCrossReferences method.
         */
        int MAX_COLUMNS = 9;

        /**
         * Position of column that contains catalog name of the procedure.
         */
        int PROCEDURE_CAT = 1;
        int PROCEDURE_SCHEM = 2;
        int PROCEDURE_NAME = 3;

        /**
         * Position of column the is reserved for future use.
         */
        int RESERVED_1 = 4;

        /**
         * Position of column the is reserved for future use.
         */
        int RESERVED_2 = 5;

        /**
         * Position of column the is reserved for future use.
         */
        int RESERVED_3 = 6;

        int REMARKS = 7;

        /**
         * Position of column Procedure type.
         */
        int PROCEDURE_TYPE = 8;

        int SPECIFIC_NAME = 9;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     * <code>JAVA_CLASS</code> is the column position for element data type on
     * server's Results object.
     */
    interface PROCEDURE_COLUMNS {

        /**
         * Number of columns to be read from results returned getProcedureColumns method.
         */
        int MAX_COLUMNS = 20;

        /**
         * Position of column that contains catalog name of the procedure.
         */
        int PROCEDURE_CAT = 1;

        int PROCEDURE_SCHEM = 2;
        int PROCEDURE_NAME = 3;
        int COLUMN_NAME = 4;

        /**
         * Position of the column containing column or element type.
         */
        int COLUMN_TYPE = 5;

        /**
         * Position of column that contains SQL type from java.sql.Types for column's data type.
         */
        int DATA_TYPE = 6;

        /**
         * Position of column that contains local type name used by the data source.
         */
        int TYPE_NAME = 7;

        int PRECISION = 8;
        int LENGTH = 9;
        int SCALE = 10;
        int RADIX = 11;

        /**
         * Position of column that contains the nullable value.
         */
        int NULLABLE = 12;

        /**
         * Position of column that contains comments.
         */
        int REMARKS = 13;
        int COLUMN_DEF = 14;

        int SQL_DATA_TYPE = 15;
        int SQL_DATETIME_SUB = 16;
        int CHAR_OCTET_LENGTH = 17;

        int ORDINAL_POSITION = 18;

        int IS_NULLABLE = 19;

        int SPECIFIC_NAME = 20;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class also has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     */
    interface TABLE_PRIVILEGES {

        /**
         * Number of columns to be read from results returned getGroupEntitlements method.
         */
        int MAX_COLUMNS = 6;

        /**
         * Position of VirtualDatabaseName column in server's results object returned by
         * getGroupEntitlements method in User API
         */
        int VIRTUAL_DATABASE_NAME = 0;

        /**
         * Position of VirtualDatabaseVersion column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int VIRTUAL_DATABASE_VERSION = 1;

        /**
         * Position of GroupName column in server's results object returned by
         * getGroupEntitlements method in User API
         */
        int GROUP_NAME = 2;

        /**
         * Position of Grantor column in server's results object returned by
         * getGroupEntitlements method in User API
         */
        int GRANTOR = 3;

        /**
         * Position of Grantee column in server's results object returned by
         * getGroupEntitlements method in User API
         */
        int GRANTEE = 4;

        /**
         * Position of Permission column in server's results object returned by
         * getGroupEntitlements method in User API
         */
        int PERMISSION = 5;

        /**
         * Position of the column containing catalog name info.
         */
        int TABLE_CAT = 0;

        /**
         * Position of the column containing privilage grantable info.
         */
        int IS_GRANTABLE = 6;
    }

    /**
     * <p>This class contains constants representing column positions on ResultSet
     * returned by getColumns method on DatabaseMetaData. The class also has constants
     * for columns whose values are to be hardcoded in MMDatabaseMetaData object.
     * <code>MAX_COLUMNS</code> is the number of columns to be read from server's
     * results from getElements method on <code>Metadata object</code>.
     */
    interface COLUMN_PRIVILEGES {

        /**
         * Number of columns to be read from results returned getElementEntitlements method.
         */
        int MAX_COLUMNS = 7;

        /**
         * Position of VirtualDatabaseName column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int VIRTUAL_DATABASE_NAME = 0;

        /**
         * Position of VirtualDatabaseVersion column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int VIRTUAL_DATABASE_VERSION = 1;

        /**
         * Position of GroupName column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int GROUP_NAME = 2;

        /**
         * Position of ElementName column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int ELEMENT_NAME = 3;

        /**
         * Position of Grantor column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int GRANTOR = 4;

        /**
         * Position of Grantee column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int GRANTEE = 5;

        /**
         * Position of Permission column in server's results object returned by
         * getElementEntitlements method in User API
         */
        int PERMISSION = 6;

        /**
         * Position of the column containing catalog name info.
         */
        int TABLE_CAT = 0;

        /**
         * Position of the column containing privilege grantable info.
         */
        int IS_GRANTABLE = 7;
    }

}
