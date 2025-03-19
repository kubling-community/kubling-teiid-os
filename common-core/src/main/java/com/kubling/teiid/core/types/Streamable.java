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

import com.kubling.teiid.core.CorePlugin;
import com.kubling.teiid.core.util.MultiArrayOutputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A large value object which can be streamable in chunks of data each time
 *
 * <p>A reference stream id is tuple source id for a Streamable object where the
 * object is in buffer manager, but the contents will never be written to disk;
 * this is the ID that client needs to reference to get the chunk of data.
 */
public abstract class Streamable<T> implements Externalizable {

    private static final Logger logger = Logger.getLogger(Streamable.class.getName());

    @Serial
    private static final long serialVersionUID = -8252488562134729374L;

    private static final AtomicLong counter = new AtomicLong();

    public static final String ENCODING = "UTF-8";
    public static final Charset CHARSET = Charset.forName(ENCODING);
    public static final int STREAMING_BATCH_SIZE_IN_BYTES = 102400; // 100K

    private String referenceStreamId = String.valueOf(counter.getAndIncrement());
    protected transient volatile T reference;
    protected long length = -1;

    public Streamable() {

    }

    public Streamable(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException(CorePlugin.Util.getString("Streamable.isNUll"));
        }

        this.reference = reference;
    }

    /**
     * Returns the cached length.  May be binary or character based.
     */
    public long getLength() {
        return length;
    }

    abstract long computeLength() throws SQLException;

    public long length() throws SQLException {
        if (length == -1) {
            if (InputStreamFactory.getStorageMode(this.reference) == InputStreamFactory.StorageMode.FREE) {
                throw new SQLException("Already freed or streaming");
            }
            length = computeLength();
        }
        return length;
    }

    public T getReference() {
        return reference;
    }

    public void setReference(T reference) {
        this.reference = reference;
    }

    public String getReferenceStreamId() {
        return this.referenceStreamId;
    }

    public void setReferenceStreamId(String id) {
        this.referenceStreamId = id;
    }

    @Override
    public String toString() {
        if (reference == null) {
            return getClass().getName() + " " + this.referenceStreamId;
        }
        return reference.toString();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        length = in.readLong();
        this.referenceStreamId = (String) in.readObject();
        if (referenceStreamId == null) {
            //we expect the data inline
            readReference(in);
        }
    }

    protected abstract void readReference(ObjectInput in) throws IOException;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        try {
            length();
        } catch (SQLException e) {
            // Ignored
        }
        out.writeLong(length);
        boolean writeBuffer = false;
        MultiArrayOutputStream baos = null;
        if (referenceStreamId == null) {
            //TODO: detect when this buffering is not necessary
            if (length > Integer.MAX_VALUE) {
                throw new AssertionError("Should not inline a lob of length " + length);
            }
            if (length > 0) {
                baos = new MultiArrayOutputStream((int) length);
            } else {
                baos = new MultiArrayOutputStream(256);
            }
            DataOutputStream dataOutput = new DataOutputStream(baos);
            try {
                writeReference(dataOutput);
                dataOutput.close();
                writeBuffer = true;
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
                referenceStreamId = "error";
            }
        }
        out.writeObject(referenceStreamId);
        if (writeBuffer) {
            baos.writeTo(out);
        }
    }

    protected boolean isBinary() {
        return true;
    }

    protected abstract void writeReference(DataOutput out) throws IOException;

}
