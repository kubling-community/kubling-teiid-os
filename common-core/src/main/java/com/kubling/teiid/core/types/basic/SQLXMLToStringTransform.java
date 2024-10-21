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

package com.kubling.teiid.core.types.basic;

import com.kubling.teiid.core.CorePlugin;
import com.kubling.teiid.core.types.DataTypeManager;
import com.kubling.teiid.core.types.Transform;
import com.kubling.teiid.core.types.TransformationException;
import com.kubling.teiid.core.types.XMLType;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;


public class SQLXMLToStringTransform extends AnyToStringTransform {

    public SQLXMLToStringTransform() {
        super(DataTypeManager.DefaultDataClasses.XML);
    }

    /**
     * This method transforms a value of the source type into a value
     * of the target type.
     *
     * @param value Incoming value of source type
     * @return Outgoing value of target type
     * @throws TransformationException if value is an incorrect input type or
     *                                 the transformation fails
     */
    public Object transformDirect(Object value) throws TransformationException {
        XMLType source = (XMLType) value;
        Reader reader = null;
        try {
            char[] result = new char[DataTypeManager.MAX_STRING_LENGTH];
            reader = source.getCharacterStream();
            int read = reader.read(result);
            return new String(result, 0, read);
        } catch (SQLException | IOException e) {
            throw new TransformationException(CorePlugin.Event.TEIID10080, e,
                    CorePlugin.Util.gs(CorePlugin.Event.TEIID10080, getSourceType().getName(), getTargetType().getName()));
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    /**
     * @see Transform#isExplicit()
     */
    public boolean isExplicit() {
        return true;
    }
}
