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

package com.kubling.teiid.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public final class ObjectInputStreamWithClassloader extends
        ObjectInputStream {
    private final ClassLoader cl;

    public ObjectInputStreamWithClassloader(InputStream in,
                                            ClassLoader cl) throws IOException {
        super(in);
        this.cl = cl;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {

        //see java bug id 6434149
        try {
            checkClass(desc.getName());
            return Class.forName(desc.getName(), false, cl);
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }

    public static void checkClass(String name) throws ClassNotFoundException {
        // Deny the resolving of classes that can cause security issues when deserialized
        // TODO Review whether it is better to consider no deserialization at all
        if (name.endsWith("functors.InvokerTransformer")
                || name.endsWith("functors.InstantiateTransformer")
                || name.equals("org.​apache.​commons.​collections.​Transformer")
                || name.equals("org.codehaus.groovy.runtime.ConvertedClosure")
                || name.equals("org.codehaus.groovy.runtime.MethodClosure")
                || name.equals("org.springframework.beans.factory.ObjectFactory")
                || name.endsWith(".​trax.​TemplatesImpl")) {
            throw new ClassNotFoundException(name);
        }
    }
}