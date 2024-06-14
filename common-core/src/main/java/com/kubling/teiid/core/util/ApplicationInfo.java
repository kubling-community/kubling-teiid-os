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

package com.kubling.teiid.core.util;

import com.kubling.teiid.core.CorePlugin;
import com.kubling.teiid.core.TeiidRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;


public final class ApplicationInfo implements Serializable {

    public static final String APPLICATION_PRODUCT_INFORMATION = "Product Information";

    public static final String APPLICATION_BUILD_NUMBER_PROPERTY = "Build";

    private static final ApplicationInfo INSTANCE = new ApplicationInfo();

    private Properties props = new Properties();

    private ApplicationInfo() {
        InputStream is = this.getClass().getResourceAsStream("application.properties");
        try {
            try {
                props.load(is);
            } finally {
                is.close();
            }
        } catch (IOException e) {
              throw new TeiidRuntimeException(CorePlugin.Event.TEIID10045, e);
        }
    }

    public String getReleaseNumber() {
        return props.getProperty("build.releaseNumber");
    }

    public int getMajorReleaseVersion() {
        String version = getReleaseNumber().substring(0, getReleaseNumber().indexOf('.'));
        return Integer.parseInt(version);
    }

    public int getMinorReleaseVersion() {
        int majorIndex = getReleaseNumber().indexOf('.');
        String version =
                getReleaseNumber().substring(majorIndex+1, getReleaseNumber().indexOf('.', majorIndex+1));
        return Integer.parseInt(version);
    }

    public String getBuildNumber() {
        return props.getProperty("build.number");
    }

    public String getUrl() {
        return props.getProperty("url");
    }

    public String getCopyright() {
        return props.getProperty("copyright");
    }

    public String getBuildDate() {
        return props.getProperty("build.date");
    }

    /**
     * Get the application information instance for this VM.
     * @return the singleton instance for this VM; never null
     */
    public static ApplicationInfo getInstance() {
        return INSTANCE;
    }

}
