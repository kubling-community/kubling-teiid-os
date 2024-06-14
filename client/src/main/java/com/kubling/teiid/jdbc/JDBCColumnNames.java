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

/* <p> This class contains constants indicating names of the columns in the
 *  result sets returned by methods on DatabaseMetaData. Each inner class represents
 *  a particular method and the class attributes give the names of the columns on
 *  methods ResultSet.
 */

interface JDBCColumnNames {

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getCatalogs method on DatabaseMetaData. These constant values
     * are be used for the column names used in constructing the ResultSet obj.
     */
    interface CATALOGS {
        //  name of the column containing catalog or Virtual database name.
        static final String TABLE_CAT = "TABLE_CAT";
    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getColumns method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in construction the ResultSet obj.
     */
    interface COLUMNS {

        //  name of the column containing catalog or Virtual database name.
        static final String TABLE_CAT = "TABLE_CAT";

        //  name of the column containing schema or Virtual database version.
        static final String TABLE_SCHEM = "TABLE_SCHEM";

        //  name of the column containing table or group name.
        static final String TABLE_NAME = "TABLE_NAME";

        //  name of the column containing column or element name.
        static final String COLUMN_NAME = "COLUMN_NAME";

        /** name of column that contains SQL type from java.sql.Types for column's data type. */
        static final String DATA_TYPE = "DATA_TYPE";

        /** name of column that contains local type name used by the data source. */
        static final String TYPE_NAME = "TYPE_NAME";

        //  name of the column containing column size.
        static final String COLUMN_SIZE = "COLUMN_SIZE";

        /** name of column that is not used will contain nulls */
        static final String BUFFER_LENGTH = "BUFFER_LENGTH";

        //  name of the column containing number of digits to right of decimal
        static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";

        //  name of the column containing column's Radix.
        static final String NUM_PREC_RADIX = "NUM_PREC_RADIX";

        /** name of column that has an String value indicating nullablity */
        static final String NULLABLE = "NULLABLE";

        /** name of column containing explanatory notes. */
        static final String REMARKS = "REMARKS";

        /** name of column which contails default value for the column. */
        static final String COLUMN_DEF = "COLUMN_DEF";

        /** name of column that not used will contain nulls */
        static final String SQL_DATA_TYPE = "SQL_DATA_TYPE";

        /** name of column that not used will contain nulls */
        static final String SQL_DATETIME_SUB = "SQL_DATETIME_SUB";

        /** name of column that stores the max number of bytes in the column */
        static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";

        /** name of column that stores the index of a column in the table */
        static final String ORDINAL_POSITION = "ORDINAL_POSITION";

        /** name of column that has an String value indicating nullablity */
        static final String IS_NULLABLE = "IS_NULLABLE";

        /** name of column that is the scope of a reference attribute (null if DATA_TYPE isn't REF)*/
        static final String SCOPE_CATLOG = "SCOPE_CATLOG";

        /** name of column that is the scope of a reference attribute (null if the DATA_TYPE isn't REF) */
        static final String SCOPE_SCHEMA = "SCOPE_SCHEMA";

        /** name of column that is the scope of a reference attribure (null if the DATA_TYPE isn't REF) */
        static final String SCOPE_TABLE = "SCOPE_TABLE";

        /**
         * name of column that is source type of a distinct type or user-generated Ref type, SQL type
         * from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF)
         */
        static final String SOURCE_DATA_TYPE = "SOURCE_DATA_TYPE";

        /** name of column that has an String value indicating format */
        static final String FORMAT = "FORMAT";

        /** name of column that has an String value indicating minimum range */
        static final String MIN_RANGE = "MIN_RANGE";

        /** name of column that has an String value indicating maximum range */
        static final String MAX_RANGE = "MAX_RANGE";
     }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getColumnPrivileges and getTablePrivileges methods on
     * DatabaseMetaData. These constant values are be used to hardcode the column
     * names used in constructin the ResultSet obj.
     */
    interface PRIVILEGES {

        //  name of the column containing catalog or Virtual database name.
        static final String TABLE_CAT = "TABLE_CAT";

        //  name of the column containing schema or Virtual database version.
        static final String TABLE_SCHEM = "TABLE_SCHEM";

        //  name of the column containing table or group name.
        static final String TABLE_NAME = "TABLE_NAME";

        //  name of the column containing column or element name.
        static final String COLUMN_NAME = "COLUMN_NAME";

        //  name of the column containing Grantor name
        static final String GRANTOR = "GRANTOR";

        //  name of the column containing Grantee name
        static final String GRANTEE = "GRANTEE";

        //  name of the column containing privilege name
        static final String PRIVILEGE = "PRIVILEGE";

        //  name of the column containing privilage grantable info
        static final String IS_GRANTABLE = "IS_GRANTABLE";
    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getCrossReference, getExportedKeys, and getImportedKeys methods
     * on DatabaseMetaData. These constant values are be used to hardcode the
     * column names used in constructin the ResultSet obj.
     */
    interface REFERENCE_KEYS {

        //  name of the column containing catalog or Virtual database name for primary key's table.
        static final String PKTABLE_CAT = "PKTABLE_CAT";

        //  name of the column containing schema or Virtual database version for primary key's table.
        static final String PKTABLE_SCHEM = "PKTABLE_SCHEM";

        //  name of the column containing table or group name for primary key's table.
        static final String PKTABLE_NAME = "PKTABLE_NAME";

        // name of the column containing column or element name of the primary key.
        static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";

        // name of the column containing catalog or Virtual database name for foreign key's table.
        static final String FKTABLE_CAT = "FKTABLE_CAT";

        // name of the column containing schema or Virtual database version for foreign key's table.
        static final String FKTABLE_SCHEM = "FKTABLE_SCHEM";

        // name of the column containing table or group name for foreign key's table.
        static final String FKTABLE_NAME = "FKTABLE_NAME";

        // name of the column containing column or element name of the foreign key.
        static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";

        // name of the column containing sequence number within the foreign key
        static final String KEY_SEQ = "KEY_SEQ";

        // name of the column containing effect on foreign key when PK is updated.
        static final String UPDATE_RULE = "UPDATE_RULE";

        // name of the column containing effect on foreign key when PK is deleted.
        static final String DELETE_RULE = "DELETE_RULE";

        // name of the column containing name of the foreign key.
        static final String FK_NAME = "FK_NAME";

        // name of the column containing name of the primary key.
        static final String PK_NAME = "PK_NAME";

        // name of the column containing deferability of foreign key constraStrings.
        static final String DEFERRABILITY = "DEFERRABILITY";
        static final String FKPOSITION = "FKPOSITION";
    }


    /**
     * This class contains constants representing column names on ResultSet
     * returned by getPrimaryKeys method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface PRIMARY_KEYS {

        // name of the column containing catalog or Virtual database name.
        static final String TABLE_CAT = "TABLE_CAT";

        // name of the column containing schema or Virtual database version.
        static final String TABLE_SCHEM = "TABLE_SCHEM";

        // name of the column containing table or group name.
        static final String TABLE_NAME = "TABLE_NAME";

        // name of the column containing column or element name.
        static final String COLUMN_NAME = "COLUMN_NAME";

        // name of the column containing sequence number within the primary key
        static final String KEY_SEQ = "KEY_SEQ";

        // name of the column containing name of the primary key.
        static final String PK_NAME = "PK_NAME";
        static final String POSITION = "POSITION";
    }


    /**
     * This class contains constants representing column names on ResultSet
     * returned by getProcedureColumns method on DatabaseMetaData. These constant
     * values are be used to hardcode the column names used in constructin the
     * ResultSet obj.
     */
    interface PROCEDURE_COLUMNS {

        // name of the column containing procedure catalog or Virtual database name.
        static final String PROCEDURE_CAT = "PROCEDURE_CAT";

        // name of the column containing schema or Virtual database version.
        static final String PROCEDURE_SCHEM = "PROCEDURE_SCHEM";

        // name of the column containing table or group name.
        static final String PROCEDURE_NAME = "PROCEDURE_NAME";

        // name of the column containing column or element name.
        static final String COLUMN_NAME = "COLUMN_NAME";

        // name of the column containing column or element type.
        static final String COLUMN_TYPE = "COLUMN_TYPE";

        /** name of column that contains SQL type from java.sql.Types for column's data type. */
        static final String DATA_TYPE = "DATA_TYPE";

        /** name of column that contains local type name used by the data source. */
        static final String TYPE_NAME = "TYPE_NAME";

        // name of the column containing number of digits to right of decimal
        static final String PRECISION = "PRECISION";

        /** name of column that that contains length of data in bytes */
        static final String LENGTH = "LENGTH";

        // constant indiacting column's Radix.
        static final String SCALE = "SCALE";        

        // constant indiacting column's Radix.
        static final String RADIX = "RADIX";

        /** name of column that has an String value indicating nullablity */
        static final String NULLABLE = "NULLABLE";

        /** name of column containing explanatory notes. */
        static final String REMARKS = "REMARKS";
        static final String POSITION = "POSITION";
    }


    /**
     * This class contains constants representing column names on ResultSet
     * returned by getProcedures method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface PROCEDURES {

        // name of the column containing procedure catalog or Virtual database name.
        static final String PROCEDURE_CAT = "PROCEDURE_CAT";

        // name of the column containing schema or Virtual database version.
        static final String PROCEDURE_SCHEM = "PROCEDURE_SCHEM";

        // name of the column containing table or group name.
        static final String PROCEDURE_NAME = "PROCEDURE_NAME";

        // name of the column containing name of the column which is reserved
        //static final String RESERVED = "RESERVED";

        /** name of column containing explanatory notes. */
        static final String REMARKS = "REMARKS";

        /** name of column indicating kind of the procedure. */
        static final String PROCEDURE_TYPE = "PROCEDURE_TYPE";

        static final String RESERVED_1 = "RESERVED_1";
        static final String RESERVED_2 = "RESERVED_2";
        static final String RESERVED_3 = "RESERVED_3";  
    }


    /**
     * This class contains constants representing column names on ResultSet
     * returned by getSchemas method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface SCHEMAS {

        // name of the column containing procedure catalog or Virtual database name.
        static final String TABLE_SCHEM = "TABLE_SCHEM";

        // name of the column containing schema or Virtual database version.
        static final String TABLE_CATALOG = "TABLE_CATALOG";

    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getTables and getTableTypes methods on DatabaseMetaData. These
     * constant values are be used to hardcode the column names used in construction
     * the ResultSet obj.
     */
    interface TABLES {

        // name of the column containing catalog or Virtual database name.
        static final String TABLE_CAT = "TABLE_CAT";

        // name of the column containing schema or Virtual database version.
        static final String TABLE_SCHEM = "TABLE_SCHEM";

        // name of the column containing table or group name.
        static final String TABLE_NAME = "TABLE_NAME";

        // name of the column containing table or group type.
        static final String TABLE_TYPE = "TABLE_TYPE";

        /** name of column containing explanatory notes. */
        static final String REMARKS = "REMARKS";
        static final String TYPE_CAT = "TYPE_CAT";
        static final String TYPE_SCHEM = "TYPE_SCHEM";
        static final String TYPE_NAME = "TYPE_NAME";
        static final String SELF_REFERENCING_COL_NAME = "SELF_REFERENCING_COL_NAME";
        static final String REF_GENERATION = "REF_GENERATION";
        static final String ISPHYSICAL = "ISPHYSICAL";

    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getTables and getTableTypes methods on DatabaseMetaData. These
     * constant values are be used to hardcode the column names used in construction
     * the ResultSet obj.
     */
    interface TABLE_TYPES {

        // name of the column containing table or group type.
        static final String TABLE_TYPE = "TABLE_TYPE";
    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getTypeInfo method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface TYPE_INFO {

        /** name of column that contains local type name used by the data source. */
        static final String TYPE_NAME = "TYPE_NAME";

        /** name of column that contains SQL type from java.sql.Types for column's data type. */
        static final String DATA_TYPE = "DATA_TYPE";

        // name of the column containing number of digits to right of decimal
        static final String PRECISION = "PRECISION";

        // name of the column containing prefix used to quote a literal
        static final String LITERAL_PREFIX = "LITERAL_PREFIX";

        // name of the column containing suffix used to quote a literal
        static final String LITERAL_SUFFIX = "LITERAL_SUFFIX";

        // name of the column containing params used in creating the type
        static final String CREATE_PARAMS = "CREATE_PARAMS";

        /** name of column that has an String value indicating nullablity */
        static final String NULLABLE = "NULLABLE";

        /** name of column that has an String value indicating case sensitivity */
        static final String CASE_SENSITIVE = "CASE_SENSITIVE";

        /** name of column that has an String value indicating searchability */
        static final String SEARCHABLE = "SEARCHABLE";

        /** name of column that has an String value indicating searchability */
        static final String UNSIGNED_ATTRIBUTE = "UNSIGNED_ATTRIBUTE";

        /** name of column that contains info if the column is a currency value */
        static final String FIXED_PREC_SCALE = "FIXED_PREC_SCALE";

        /** name of column that contains info whether the column is autoincrementable */
        static final String AUTOINCREMENT = "AUTO_INCREMENT";

        /** name of column that localised version of type name */
        static final String LOCAL_TYPE_NAME = "LOCAL_TYPE_NAME";

        /** name of column that gives the min scale supported */
        static final String MINIMUM_SCALE = "MINIMUM_SCALE";

        /** name of column that gives the max scale supported */
        static final String MAXIMUM_SCALE = "MAXIMUM_SCALE";

        /** name of column that not used will contain nulls */
        static final String SQL_DATA_TYPE = "SQL_DATA_TYPE";

        /** name of column that not used will contain nulls */
        static final String SQL_DATETIME_SUB = "SQL_DATETIME_SUB";

        // constant indiacting column's Radix.
        static final String NUM_PREC_RADIX = "NUM_PREC_RADIX";
    }


    /**
     * This class contains constants representing column names on ResultSet
     * returned by getUDTS method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface UDTS {

        // name of the column containing table or Groups name in which UDTS are present.
        static final String TABLE_NAME = "UDTS";

        // name of the column containing catalog or Virtual database name.
        static final String TYPE_CAT = "TYPE_CAT";

        // name of the column containing schema or Virtual database version.
        static final String TYPE_SCHEM = "TYPE_SCHEM";

        // name of the column containing name of type name column.
        static final String TYPE_NAME = "TYPE_NAME";

        // name of the column containing class name column.
        static final String CLASS_NAME = "CLASS_NAME";

        // name of the column containing name of sql datatype code column
        static final String DATA_TYPE = "DATA_TYPE";

        // name of the column containing comments column
        static final String REMARKS = "REMARKS";
        static final String BASE_TYPE = "BASE_TYPE";
    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getIndexInfo method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface INDEX_INFO {

        // name of the column containing tables catalog name on which the index is present
        static final String TABLE_CAT = "TABLE_CAT";

        // name of the column containing tables schema name on which the index is present
        static final String TABLE_SCHEM = "TABLE_SCHEM";

        // name of the column containing table or group name.
        static final String TABLE_NAME = "TABLE_NAME";

        // name of the column containing name of column showing if an index in non-unique
        static final String NON_UNIQUE = "NON_UNIQUE";

        // name of the column containing name of column containing index_qualifier string
        static final String INDEX_QUALIFIER = "INDEX_QUALIFIER";

        // name of the column containing name of column containing index names
        static final String INDEX_NAME = "INDEX_NAME";

        // name of the column containing name of column containing index types
        static final String TYPE = "TYPE";

        // name of the column containing name of the column containing column position.
        static final String ORDINAL_POSITION = "ORDINAL_POSITION";

        // name of the column containing name of the column containing column names.
        static final String COLUMN_NAME = "COLUMN_NAME";

        // name of the column containing name of column containing info if the index is asc or desc.
        static final String ASC_OR_DESC = "ASC_OR_DESC";

        // name of the column containing name of the column containing number of unique values in index.
        static final String CARDINALITY = "CARDINALITY";

        // name of the column containing name of the column giving number od pages used for the current index.
        static final String PAGES = "PAGES";

        // name of the column containing name of the column giving filter condition.
        static final String FILTER_CONDITION = "FILTER_CONDITION";
    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getBestRowIdentifier method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface BEST_ROW {

        // name of the column containing SCOPE of the identifier
        static final String SCOPE = "SCOPE";

        // name of the column containing column name
        static final String COLUMN_NAME = "COLUMN_NAME";

        // name of the column containing data type code
        static final String DATA_TYPE = "DATA_TYPE";

        // name of the column containing data type name
        static final String TYPE_NAME = "TYPE_NAME";

        // name of the column containing size of the column
        static final String COLUMN_SIZE = "COLUMN_SIZE";

        // name of the column containing buffer length
        static final String BUFFER_LENGTH = "BUFFER_LENGTH";

        // name of the column containing decimal digits/ scale
        static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";

        // name of the column containing name of the column containing column position.
        static final String ORDINAL_POSITION = "ORDINAL_POSITION";

        // name of the column containing pseudo column
        static final String PSEUDO_COLUMN = "PSEUDO_COLUMN";
    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getSuperTables method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */
    interface SUPER_TABLES {
        // name of the column containing catalog or Virtual database name.
        static final String TABLE_CAT = "TABLE_CAT";
        // name of the column containing schema or Virtual database version.
        static final String TABLE_SCHEM = "TABLE_SCHEM";
        // name of the column containing table or Groups name .
        static final String TABLE_NAME = "TABLE_NAME";
        // name of the column containing super table.
        static final String SUPERTABLE_NAME = "SUPERTABLE_NAME";
    }

    /**
     * This class contains constants representing column names on ResultSet
     * returned by getSuperTypes method on DatabaseMetaData. These constant values
     * are be used to hardcode the column names used in constructin the ResultSet obj.
     */

    interface SUPER_TYPES {

        // name of the column containing catalog or Virtual database name.
        static final String TYPE_CAT = "TYPE_CAT";
        // name of the column containing schema or Virtual database version.
        static final String TYPE_SCHEM = "TYPE_SCHEM";
        // name of the column containing name of type name column.
        static final String TYPE_NAME = "TYPE_NAME";
        //  name of the column containing super catalog or Virtual database name.
        static final String SUPERTYPE_CAT = "SUPERTYPE_CAT";
        // name of the column containing super schema or Virtual database version.
        static final String SUPERTYPE_SCHEM = "SUPERTYPE_SCHEM";
        // name of the column containing name of super type name column.
        static final String SUPERTYPE_NAME = "SUPERTYPE_NAME";

    }

}
