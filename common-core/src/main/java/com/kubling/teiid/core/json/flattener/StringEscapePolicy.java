/*
 *
 * Copyright 2016 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.kubling.teiid.core.json.flattener;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.*;

import java.util.Collections;
import java.util.HashMap;

import static java.util.Collections.unmodifiableMap;

/**
 * {@link StringEscapePolicy} lists all acceptable JSON string escape policy of the
 * {@link JsonFlattener}.
 *
 * @author Wei-Ming Wu
 */
public enum StringEscapePolicy implements CharSequenceTranslatorFactory {

    /**
     * Escapes all JSON special characters but Unicode.
     *
     * @deprecated for removal in 0.17.0 in favor of {@link StringEscapePolicy#ALL_BUT_UNICODE}
     */
    @Deprecated
    NORMAL(new AggregateTranslator(new LookupTranslator(new HashMap<>() {
        private static final long serialVersionUID = 1L;

        {
            put("\"", "\\\"");
            put("\\", "\\\\");
            put("/", "\\/");
        }
    }), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE))),

    /**
     * Escapes all JSON special characters and Unicode.
     *
     * @deprecated for removal in 0.17.0 in favor of {@link StringEscapePolicy#ALL}
     */
    @Deprecated
    ALL_UNICODES(StringEscapeUtils.ESCAPE_JSON),

    /**
     * Escapes all JSON special characters and Unicode.
     */
    ALL(StringEscapeUtils.ESCAPE_JSON),

    /**
     * Escapes all JSON special characters and Unicode but slash('/').
     */
    ALL_BUT_SLASH(new AggregateTranslator(
            new LookupTranslator(unmodifiableMap(new HashMap<>() {
                private static final long serialVersionUID = 1L;

                {
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE),
            JavaUnicodeEscaper.outsideOf(32, 0x7f))),

    /**
     * Escapes all JSON special characters but Unicode.
     */
    ALL_BUT_UNICODE(new AggregateTranslator(
            new LookupTranslator(unmodifiableMap(new HashMap<>() {
                private static final long serialVersionUID = 1L;

                {
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                    put("/", "\\/");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE))),

    /**
     * Escapes all JSON special characters but slash('/') and Unicode.
     */
    ALL_BUT_SLASH_AND_UNICODE(new AggregateTranslator(
            new LookupTranslator(Collections.unmodifiableMap(new HashMap<>() {
                private static final long serialVersionUID = 1L;

                {
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE))),

    /**
     * Escapes all JSON special characters but slash('/') and Unicode.
     */
    DEFAULT(new AggregateTranslator(
            new LookupTranslator(unmodifiableMap(new HashMap<>() {
                private static final long serialVersionUID = 1L;

                {
                    put("\"", "\\\"");
                    put("\\", "\\\\");
                }
            })), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE)));

    private final CharSequenceTranslator translator;

    StringEscapePolicy(CharSequenceTranslator translator) {
        this.translator = translator;
    }

    @Override
    public CharSequenceTranslator getCharSequenceTranslator() {
        return translator;
    }

}