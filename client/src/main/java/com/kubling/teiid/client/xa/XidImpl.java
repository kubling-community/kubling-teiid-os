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

package com.kubling.teiid.client.xa;

import javax.transaction.xa.Xid;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Teiid implementation of Xid.
 */
public class XidImpl implements Xid, Externalizable {
    private static final long serialVersionUID = -7078441828703404308L;

    private int formatID;
    private byte[] globalTransactionId;
    private byte[] branchQualifier;
    private String toString;

    public XidImpl() {
    }

    public XidImpl(Xid xid) {
        this.formatID = xid.getFormatId();
        this.globalTransactionId = xid.getGlobalTransactionId();
        this.branchQualifier = xid.getBranchQualifier();
    }

    public XidImpl(int formatID, byte[] globalTransactionId, byte[] branchQualifier){
        this.formatID = formatID;
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }

    /**
     * @see Xid#getFormatId()
     */
    public int getFormatId() {
        return formatID;
    }

    /**
     * @see Xid#getGlobalTransactionId()
     */
    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    /**
     * @see Xid#getBranchQualifier()
     */
    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    public boolean equals(Object obj){
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof XidImpl)){
            return false;
        }
        XidImpl that = (XidImpl)obj;
        return this.formatID == that.formatID
                && Arrays.equals(this.globalTransactionId, that.globalTransactionId)
                && Arrays.equals(this.branchQualifier, that.branchQualifier);
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        if (toString == null) {
            String sb = "Teiid-Xid global:" +
                    getByteArrayString(globalTransactionId) +
                    " branch:" +
                    getByteArrayString(branchQualifier) +
                    " format:" +
                    getFormatId();
            toString = sb;
        }
        return toString;
    }

    static String getByteArrayString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return new BigInteger(bytes).toString();
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.formatID = in.readInt();
        this.globalTransactionId = (byte[])in.readObject();
        this.branchQualifier = (byte[])in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.formatID);
        out.writeObject(this.globalTransactionId);
        out.writeObject(this.branchQualifier);
    }

}
