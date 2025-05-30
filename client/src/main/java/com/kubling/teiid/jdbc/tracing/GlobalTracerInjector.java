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

package com.kubling.teiid.jdbc.tracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format.Builtin;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.util.GlobalTracer;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses the opentracing library to create a json string representation of the span context
 * and provides a way to manipulate a static tracer without using the GlobalTracer registration
 */
public class GlobalTracerInjector implements TracingHelper.Injector {

    private static Tracer TRACER = GlobalTracer.get();

    @Override
    public String getSpanContext() {
        return getSpanContext(TRACER);
    }

    protected static String getSpanContext(Tracer tracer) {
        Span span = tracer.activeSpan();
        if (span == null) {
            return null;
        }
        Map<String, String> spanMap = new HashMap<>();
        tracer.inject(span.context(), Builtin.TEXT_MAP, new TextMapAdapter(spanMap));

        //simple json creation
        StringBuilder json = new StringBuilder();
        json.append('{');
        boolean first = true;
        for (Map.Entry<String, String> entry : spanMap.entrySet()) {
            if (!first) {
                json.append(',');
            } else {
                first = false;
            }
            json.append('"').append(entry.getKey().replace("\"", "\\\""))
                    .append("\":\"")
                    .append(entry.getValue().replace("\"", "\\\"")).append('"');
        }
        json.append('}');
        return json.toString();
    }

    /*
     * Used to work around that the GlobalTracer can only be registered once.
     */

    public static Tracer getTracer() {
        return TRACER;
    }

    public static void setTracer(Tracer tracer) {
        TRACER = tracer;
    }

}
