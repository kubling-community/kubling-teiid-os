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

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;

public class StandardXMLTranslator extends XMLTranslator {

    private static ThreadLocal<TransformerFactory> threadLocalTransformerFactory = new ThreadLocal<TransformerFactory>() {
        protected TransformerFactory initialValue() {
            return TransformerFactory.newInstance();
        }
    };

    public static TransformerFactory getThreadLocalTransformerFactory() {
        return threadLocalTransformerFactory.get();
    }

    private Source source;

    public StandardXMLTranslator(Source source) {
        this.source = source;
    }

    @Override
    public void translate(Writer writer) throws TransformerException, IOException {
        Transformer t = threadLocalTransformerFactory.get().newTransformer();
        t.transform(source, new StreamResult(writer));
    }

}