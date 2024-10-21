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
import com.kubling.teiid.core.types.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class StringToJsonTransform extends Transform {

    private static Method METHOD;

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
        String json = (String) value;
        if (METHOD == null) {
            try {
                Class<?> clazz = Class.forName("org.teiid.query.function.JSONFunctionMethods");
                METHOD = clazz.getMethod("jsonParse", ClobType.class, boolean.class);
            } catch (Exception e) {
                throw new TransformationException(e, CorePlugin.Util.gs(CorePlugin.Event.TEIID10084));
            }
        }
        try {
            return METHOD.invoke(null, new ClobType(new ClobImpl(json)), false);
        } catch (InvocationTargetException e) {
            throw new TransformationException(e.getTargetException());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new TransformationException(e);
        }
    }

    /**
     * Type of the incoming value.
     *
     * @return Source type
     */
    public Class<?> getSourceType() {
        return DataTypeManager.DefaultDataClasses.STRING;
    }

    /**
     * Type of the outgoing value.
     *
     * @return Target type
     */
    public Class<?> getTargetType() {
        return DataTypeManager.DefaultDataClasses.JSON;
    }

}
