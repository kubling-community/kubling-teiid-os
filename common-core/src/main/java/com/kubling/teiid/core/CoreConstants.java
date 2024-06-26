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

package com.kubling.teiid.core;

public interface CoreConstants {
    String SYSTEM_MODEL = "SYS";

    String SYSTEM_ADMIN_MODEL = "SYSADMIN";

    String ODBC_MODEL = "pg_catalog";

    String INFORMATION_SCHEMA = "information_schema";

    /**
     * Constant for the anonymous Teiid system username
     */
    String DEFAULT_ANON_USERNAME = "anonymous";
}
