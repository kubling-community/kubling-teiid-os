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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MixinProxy implements InvocationHandler {

    private static class Target {
        Object obj;
        Method m;
    }

    private final Object[] delegates;
    private final Map<Method, Target> methodMap = new HashMap<>();

    public MixinProxy(Object... delegates) {
        this.delegates = delegates;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Target t = methodMap.get(method);
        if (t == null) {
            for (Object object : delegates) {
                try {
                    Method m = object.getClass().getMethod(method.getName(), method.getParameterTypes());
                    t = new Target();
                    t.m = m;
                    t.obj = object;
                    methodMap.put(method, t);
                    break;
                } catch (NoSuchMethodException e) {
                    // Ignored
                }
            }
        }
        if (t != null) {
            try {
                return t.m.invoke(t.obj, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        return noSuchMethodFound(proxy, method, args);
    }

    protected Object noSuchMethodFound(Object proxy, Method method, Object[] args) throws Throwable {
        throw new RuntimeException("Could not determine target delegate for method " + method);
    }

}
