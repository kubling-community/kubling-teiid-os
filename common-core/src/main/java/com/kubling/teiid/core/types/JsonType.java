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

package com.kubling.teiid.core.types;

import java.io.Serial;
import java.sql.Clob;


/**
 * This is wrapper on top of clob functionality
 */
public final class JsonType extends BaseClobType {

    @Serial
    private static final long serialVersionUID = 2753412502127824104L;

    public JsonType() {
    }

    public JsonType(Clob clob) {
        super(clob);
    }

}
