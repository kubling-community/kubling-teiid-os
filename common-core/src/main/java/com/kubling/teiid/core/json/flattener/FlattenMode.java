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

/**
 * {@link FlattenMode} lists all acceptable modes of the {@link JsonFlattener}.
 *
 * @author Wei-Ming Wu
 */
public enum FlattenMode {

    /**
     * Flattens every object.
     */
    NORMAL,

    /**
     * Flattens every object except arrays.
     */
    KEEP_ARRAYS,

    /**
     * Conforms to MongoDB dot.notation to update also nested documents.
     */
    MONGODB,

    /**
     * Flattens every object except arrays which contain only primitive types(strings, numbers,
     * booleans, and null).
     */
    KEEP_PRIMITIVE_ARRAYS

}