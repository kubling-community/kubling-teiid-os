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

import com.kubling.teiid.core.types.InputStreamFactory.StorageMode;
import com.kubling.teiid.core.util.ExternalizeUtil;
import com.kubling.teiid.core.util.PropertiesUtils;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.SQLXML;

/**
 * This class represents the SQLXML object along with the Streamable interface.
 * <p>
 * NOTE that this representation of XML does not become unreadable after
 * read operations.
 */
public final class XMLType extends Streamable<SQLXML> implements SQLXML {

    public enum Type {
        UNKNOWN, DOCUMENT, CONTENT, ELEMENT, COMMENT, PI, TEXT
    }

    private static final long serialVersionUID = -7922647237095135723L;
    static final boolean SUPPORT_DTD =
            PropertiesUtils.getHierarchicalProperty("org.teiid.supportDTD", false, Boolean.class);

    private static final ThreadLocal<XMLInputFactory> threadLocalFactory = ThreadLocal.withInitial(XMLType::createXMLInputFactory);

    private static XMLInputFactory createXMLInputFactory()
            throws FactoryConfigurationError {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        if (!SUPPORT_DTD) {
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            // Next ones are somewhat redundant, we set them just in case the DTD support property is not respected
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            factory.setXMLResolver((arg0, arg1, arg2, arg3) -> {
                throw new XMLStreamException("Reading external entities is disabled");
            });
        }
        return factory;
    }

    private static final XMLInputFactory factory = createXMLInputFactory();
    private static Boolean factoriesTreadSafe;

    private transient Type type = Type.UNKNOWN;
    private String encoding;

    public static boolean isThreadSafeXmlFactories() {
        if (factoriesTreadSafe == null) {
            factoriesTreadSafe = factory.getClass().getName().contains(".wstx.");
        }
        return factoriesTreadSafe;
    }

    public static XMLInputFactory getXmlInputFactory() {
        if (isThreadSafeXmlFactories()) {
            return factory;
        }
        return threadLocalFactory.get();
    }

    public XMLType() {

    }

    public XMLType(SQLXML xml) {
        super(xml);
    }

    public InputStream getBinaryStream() throws SQLException {
        return this.reference.getBinaryStream();
    }

    public Reader getCharacterStream() throws SQLException {
        return this.reference.getCharacterStream();
    }

    public <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {
        return this.reference.getSource(sourceClass);
    }

    public String getString() throws SQLException {
        return this.reference.getString();
    }

    public OutputStream setBinaryStream() throws SQLException {
        return this.reference.setBinaryStream();
    }

    public Writer setCharacterStream() throws SQLException {
        return this.reference.setCharacterStream();
    }

    public void setString(String value) throws SQLException {
        this.reference.setString(value);
    }

    public void free() throws SQLException {
        this.reference.free();
    }

    public <T extends Result> T setResult(Class<T> resultClass)
            throws SQLException {
        return this.reference.setResult(resultClass);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getEncoding() {
        if (encoding == null) {
            this.encoding = getEncoding(this);
        }
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        readExternal(in, (byte) 0);
    }

    public void readExternal(ObjectInput in, byte version) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        try {
            this.encoding = (String) in.readObject();
        } catch (OptionalDataException e) {
            this.encoding = ENCODING;
        }
        try {
            if (version > 0) {
                this.type = ExternalizeUtil.readEnum(in, Type.class, Type.UNKNOWN);
            } else {
                this.type = (Type) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            this.type = Type.UNKNOWN;
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        writeExternal(out, (byte) 0);
    }

    public void writeExternal(ObjectOutput out, byte version) throws IOException {
        super.writeExternal(out);
        if (this.encoding == null) {
            this.encoding = getEncoding(this);
        }
        out.writeObject(this.encoding);
        if (version > 0) {
            ExternalizeUtil.writeEnum(out, this.type);
        } else {
            out.writeObject(this.type);
        }
    }

    /**
     * Returns the encoding or null if it cannot be determined
     *
     * @param xml
     */
    public static String getEncoding(SQLXML xml) {
        try {
            if (xml instanceof XMLType) {
                XMLType type = (XMLType) xml;
                if (type.encoding != null) {
                    return type.encoding;
                }
                xml = type.reference;
            }
            if (xml instanceof SQLXMLImpl) {
                Charset cs = ((SQLXMLImpl) xml).getCharset();
                if (cs != null) {
                    return cs.name();
                }
            }
            return getEncoding(xml.getBinaryStream());
        } catch (SQLException e) {
            return null;
        }
    }

    public static String getEncoding(InputStream is) {
        XMLStreamReader reader;
        try {
            reader = factory.createXMLStreamReader(is);
            return reader.getEncoding();
        } catch (XMLStreamException e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    @Override
    long computeLength() throws SQLException {
        if (this.reference instanceof SQLXMLImpl) {
            SQLXMLImpl impl = (SQLXMLImpl) this.reference;
            return impl.length();
        }
        return BaseLob.length(getBinaryStream());
    }

    @Override
    protected void readReference(ObjectInput in) throws IOException {
        byte[] bytes = new byte[(int) getLength()];
        in.readFully(bytes);
        this.reference = new SQLXMLImpl(bytes);
    }

    @Override
    protected void writeReference(final DataOutput out) throws IOException {
        try {
            BlobType.writeBinary(out, getBinaryStream(), (int) length);
        } catch (SQLException e) {
            throw new IOException();
        }
    }

    @Override
    public long length() throws SQLException {
        if (this.length != -1) {
            return length;
        }
        StorageMode storageMode = InputStreamFactory.getStorageMode(this);
        if (storageMode != StorageMode.OTHER) {
            return super.length();
        }
        throw new SQLException("Computing the length may leave the XML value unreadable");
    }
}
