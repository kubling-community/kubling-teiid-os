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

import com.kubling.teiid.core.BundleUtil;
import com.kubling.teiid.core.TeiidProcessingException;

/**
 * The exception is thrown when an error occurs during data transformation,
 * typically a formatting error or an invalid input type.
 */
public class TransformationException extends TeiidProcessingException {

    private static final long serialVersionUID = -4112567582638012800L;

    /**
     * No-Arg Constructor
     */
    public TransformationException() {
        super();
    }

    /**
     * Construct an instance with the message specified.
     *
     * @param message A message describing the exception
     */
    public TransformationException(String message) {
        super(message);
    }

    public TransformationException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance from a message and an exception to chain to this one.
     *
     * @param message A message describing the exception
     * @param e       An exception to nest within this one
     */
    public TransformationException(Throwable e, String message) {
        super(e, message);
    }

    public TransformationException(BundleUtil.Event event, String message) {
        super(event, message);
    }

    public TransformationException(BundleUtil.Event event, Throwable t, String message) {
        super(event, t, message);
    }

}

