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

import com.kubling.teiid.core.util.ArgCheck;

import java.sql.SQLException;
import java.sql.Wrapper;


public class WrapperImpl implements Wrapper {
    public boolean isWrapperFor(Class<?> iface) {
        ArgCheck.isNotNull(iface);

        return iface.isInstance(this);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (!isWrapperFor(iface)) {
            throw new SQLException(JDBCPlugin.Util.getString("WrapperImpl.wrong_class", iface));
        }

        return iface.cast(this);
    }

}
