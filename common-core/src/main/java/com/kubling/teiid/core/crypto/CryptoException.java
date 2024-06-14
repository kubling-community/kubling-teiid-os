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

package com.kubling.teiid.core.crypto;

import com.kubling.teiid.core.BundleUtil;
import com.kubling.teiid.core.TeiidException;

/**
 * A catch-all exception for any exceptions related to encryption and decryption
 * operations.
 */
public class CryptoException extends TeiidException {

    /**
     * No-Arg Constructor
     */
    public CryptoException(  ) {
        super( );
    }
    /**
     * Construct an instance with the message specified.
     *
     * @param message A message describing the exception
     */
    public CryptoException( String message ) {
        super( message );
    }

    /**
     * Construct an instance with a linked exception specified.
     *
     * @param e An exception to chain to this exception
     */
    public CryptoException( Throwable e ) {
        super( e );
    }

    /**
     * Construct an instance with the message and error code specified.
     *
     * @param message A message describing the exception
     * @param code The error code
     */
    public CryptoException(BundleUtil.Event code, String message ) {
        super( code, message );
    }

    /**
     * Construct an instance from a message and an exception to chain to this one.
     *
     * @param message A code denoting the exception
     * @param e An exception to nest within this one
     */
    public CryptoException( Throwable e, String message ) {
        super( e, message );
    }

    /**
     * Construct an instance from a message and a code and an exception to
     * chain to this one.
     *
     * @param e An exception to nest within this one
     * @param message A message describing the exception
     * @param code A code denoting the exception
     */
    public CryptoException(BundleUtil.Event code, Throwable e, String message ) {
        super(code, e, message);
    }

    public CryptoException(BundleUtil.Event code, Throwable e) {
        super(code, e);
    }

}
