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

import java.io.IOException;


/**
 * An interface for generating or producing the LobChunks from a remote or local
 * location. A LobChunk is part or whole of a LOB (clob, blob, xml) object.
 *
 * @see LobChunk
 */
public interface LobChunkProducer {
    /**
     * Gets the next LobChunk from the source, based on the chunk size configured
     *
     * @return LobChunk at position in the streamable object.
     */
    LobChunk getNextChunk() throws IOException;

    /**
     * Close the underlying stream of producing the chunks
     */
    void close() throws IOException;
}
