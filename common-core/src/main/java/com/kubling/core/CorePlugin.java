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

package com.kubling.core;

import java.util.ResourceBundle;

public class CorePlugin {

    /**
     * The plug-in identifier of this plugin
     */
    public static final String PLUGIN_ID = CorePlugin.class.getPackage().getName();

    public static final BundleUtil Util =
            new BundleUtil(PLUGIN_ID, PLUGIN_ID + ".i18n",
                    ResourceBundle.getBundle(PLUGIN_ID + ".i18n"));  

    public enum Event implements BundleUtil.Event {
        KBL10000,
        KBL10001,
        KBL10002,
        KBL10003,
        KBL10004,
        KBL10005,
        KBL10006,
        KBL10009,
        KBL10010,
        KBL10011,
        KBL10012,
        KBL10013,
        KBL10016,
        KBL10017,
        KBL10018,
        KBL10021,
        KBL10022,
        KBL10023,
        KBL10024,
        KBL10030,
        KBL10032,
        KBL10033,
        KBL10034,
        KBL10035,
        KBL10036,
        KBL10037,
        KBL10038,
        KBL10039,
        KBL10040,
        KBL10041,
        KBL10042,
        KBL10043,
        KBL10044,
        KBL10045,
        KBL10046,
        KBL10047,
        KBL10048,
        KBL10049,
        KBL10051,
        KBL10052,
        KBL10053,
        KBL10054,
        KBL10056,
        KBL10057,
        KBL10058,
        KBL10059,
        KBL10060,
        KBL10061,
        KBL10063,
        KBL10068,
        KBL10070,
        KBL10071,
        KBL10072,
        KBL10073,
        KBL10074,
        KBL10076,
        KBL10077,
        KBL10078,
        KBL10080,
        KBL10081,
        KBL10082,
        KBL10083,
        KBL10084,
        KBL10085,
    }
}
