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

package com.kubling.teiid.net.socket;

import com.kubling.teiid.core.util.UnitTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("nls")
public class TestServiceInvocationStruct {

    @Test
    public void testSerialize() throws Exception {
        ServiceInvocationStruct struct = new ServiceInvocationStruct(new Object[] {Integer.valueOf(1), "hello"}, "doSomething", TestServiceInvocationStruct.class);

        Object serialized = UnitTestUtil.helpSerialize(struct);
        assertNotNull(serialized);
        assertTrue(serialized instanceof ServiceInvocationStruct);
        ServiceInvocationStruct copy = (ServiceInvocationStruct)serialized;
        assertTrue(Arrays.equals(struct.args, copy.args));
        assertEquals(struct.methodName, copy.methodName);
        assertEquals(struct.targetClass, copy.targetClass);
    }
}
