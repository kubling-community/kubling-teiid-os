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

/*
 * This file was modified as part of the Kubling project.
 */

package com.kubling.client;

import com.kubling.client.lob.LobChunk;
import com.kubling.client.metadata.MetadataResult;
import com.kubling.client.security.Secure;
import com.kubling.client.util.ResultsFuture;
import com.kubling.client.xa.XATransactionException;
import com.kubling.client.xa.XidImpl;
import com.kubling.core.KublingComponentException;
import com.kubling.core.KublingProcessingException;

import javax.transaction.xa.Xid;


public interface DQP {

    @Secure(optional = true)
    ResultsFuture<ResultsMessage> executeRequest(long reqID, RequestMessage message)
            throws KublingProcessingException, KublingComponentException;

    ResultsFuture<ResultsMessage> processCursorRequest(long reqID, int batchFirst, int fetchSize)
            throws KublingProcessingException;

    ResultsFuture<?> closeRequest(long requestID) throws KublingProcessingException, KublingComponentException;

    boolean cancelRequest(long requestID) throws KublingProcessingException, KublingComponentException;

    ResultsFuture<?> closeLobChunkStream(int lobRequestId, long requestId, String streamId)
            throws KublingProcessingException, KublingComponentException;

    ResultsFuture<LobChunk> requestNextLobChunk(int lobRequestId, long requestId, String streamId)
            throws KublingProcessingException, KublingComponentException;

    MetadataResult getMetadata(long requestID) throws KublingComponentException, KublingProcessingException;

    MetadataResult getMetadata(long requestID, String preparedSql, boolean allowDoubleQuotedVariable)
            throws KublingComponentException, KublingProcessingException;

    // local transaction

    ResultsFuture<?> begin() throws XATransactionException;

    ResultsFuture<?> commit() throws XATransactionException;

    ResultsFuture<?> rollback() throws XATransactionException;

    // XA

    ResultsFuture<?> start(XidImpl xid,
                           int flags,
                           int timeout) throws XATransactionException;

    ResultsFuture<?> end(XidImpl xid,
                         int flags) throws XATransactionException;

    ResultsFuture<Integer> prepare(XidImpl xid) throws XATransactionException;

    ResultsFuture<?> commit(XidImpl xid, boolean onePhase) throws XATransactionException;

    ResultsFuture<?> rollback(XidImpl xid) throws XATransactionException;

    ResultsFuture<?> forget(XidImpl xid) throws XATransactionException;

    ResultsFuture<Xid[]> recover(int flag) throws XATransactionException;

}
