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
package com.kubling.teiid.netty.handler.codec.serialization;

import com.kubling.teiid.core.util.ExternalizeUtil;
import com.kubling.teiid.core.util.MultiArrayOutputStream;

import java.io.*;


/**
 * An {@link ObjectOutput} which is interoperable with {@link ObjectDecoderInputStream}.
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 */
public class ObjectEncoderOutputStream extends ObjectOutputStream {

    private final DataOutputStream out;
    private final MultiArrayOutputStream baos;

    public ObjectEncoderOutputStream(DataOutputStream out, int initialBufferSize)
            throws SecurityException, IOException {
        super();
        this.out = out;
        baos = new MultiArrayOutputStream(initialBufferSize);
    }

    @Override
    final protected void writeObjectOverride(Object obj) throws IOException {
        baos.reset(4);
        CompactObjectOutputStream oout = new CompactObjectOutputStream(baos);
        oout.writeObject(obj);
        ExternalizeUtil.writeCollection(oout, oout.getReferences());
        oout.flush();
        oout.close();

        int val = baos.getCount() - 4;
        byte[] b = baos.getBuffers()[0];
        b[3] = (byte) (val >>> 0);
        b[2] = (byte) (val >>> 8);
        b[1] = (byte) (val >>> 16);
        b[0] = (byte) (val >>> 24);
        baos.writeTo(out);

        if (!oout.getStreams().isEmpty()) {
            baos.reset(0);
            byte[] chunk = new byte[(1 << 16)];
            for (InputStream is : oout.getStreams()) {
                while (true) {
                    int bytes = is.read(chunk, 2, chunk.length - 2);
                    int toWrite = Math.max(0, bytes);
                    chunk[1] = (byte) (toWrite >>> 0);
                    chunk[0] = (byte) (toWrite >>> 8);
                    if (baos.getIndex() + toWrite + 2 > b.length) {
                        //exceeds the first buffer
                        baos.writeTo(out);
                        baos.reset(0);
                        out.write(chunk, 0, toWrite + 2);
                    } else {
                        //buffer the small chunk
                        baos.write(chunk, 0, toWrite + 2);
                    }
                    if (bytes < 1) {
                        is.close();
                        break;
                    }
                }
            }
            if (baos.getIndex() > 0) {
                baos.writeTo(out);
            }
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void reset() {
        //automatically resets with each use
    }

}
