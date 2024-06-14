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

package com.kubling.teiid.client.lob;

import com.kubling.teiid.client.DQP;
import com.kubling.teiid.core.TeiidException;
import com.kubling.teiid.core.types.Streamable;
import com.kubling.teiid.jdbc.JDBCPlugin;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


public class StreamingLobChunckProducer implements LobChunkProducer {

    public static class Factory {
        private final Streamable<?> streamable;
        private final DQP dqp;
        private final long requestId;

        public Factory(DQP dqp,
                long requestId, Streamable<?> streamable) {
            super();
            this.dqp = dqp;
            this.requestId = requestId;
            this.streamable = streamable;
        }

        public StreamingLobChunckProducer getLobChunkProducer() {
            return new StreamingLobChunckProducer(dqp, requestId, streamable);
        }
    }

    private static AtomicInteger REQUEST_SEQUENCE = new AtomicInteger(0);

    private final Streamable<?> streamable;
    private final DQP dqp;
    private final long requestId;
    private final int streamRequestId = REQUEST_SEQUENCE.getAndIncrement();

    public StreamingLobChunckProducer(DQP dqp, long requestId,
            Streamable<?> streamable) {
        this.dqp = dqp;
        this.requestId = requestId;
        this.streamable = streamable;
    }

    public LobChunk getNextChunk() throws IOException {
        try {
            Future<LobChunk> result =
                    dqp.requestNextLobChunk(streamRequestId, requestId, streamable.getReferenceStreamId());
            return result.get();
        } catch (Exception e) {
            IOException ex = new IOException(JDBCPlugin.Util.getString("StreamImpl.Unable_to_read_data_from_stream", e.getMessage())); //$NON-NLS-1$
            ex.initCause(e);
            throw ex;
        }
    }

    public void close() throws IOException {
        try {
            dqp.closeLobChunkStream(streamRequestId, requestId, streamable.getReferenceStreamId());
        } catch (TeiidException e) {
            IOException ex = new IOException(e.getMessage(), e);
            throw  ex;
        }
    }
}
