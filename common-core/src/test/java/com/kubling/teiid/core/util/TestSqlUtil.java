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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class TestSqlUtil {

    public void helpTest(String sql, boolean isUpdate) {
        boolean actual = SqlUtil.isUpdateSql(sql);
        assertEquals(isUpdate, actual);
    }

    @Test
    public void testSelect() {
        helpTest("SELECT x FROM y", false);
    }

    @Test
    public void testInsert() {
        helpTest("Insert INTO g (a) VALUES (1)", true);
    }

    @Test
    public void testUpdate() {
        helpTest("upDate x set a=5", true);
    }

    @Test
    public void testDelete() {
        helpTest("delete FROM x", true);
    }

    @Test
    public void testInsertWithWhitespace() {
        helpTest("\nINSERT INTO g (a) VALUES (1)", true);
    }

    @Test
    public void testExec() {
        helpTest("exec sq1()", false);
    }

    @Test
    public void testXquery() {
        helpTest("<i/>", false);
    }

    @Test
    public void testSelectInto1() {
        helpTest("SELECT x INTO z FROM y", true);
    }

    @Test
    public void testSelectInto2() {
        helpTest("SELECT x, INTOz FROM y", false);
    }

    @Test
    public void testSelectInto3() {
        helpTest("SELECT x into z FROM y", true);
    }

    @Test
    public void testSelectInto4() {
        helpTest("SELECT x into z", true);
    }

    @Test
    public void testSelectInto5() {
        helpTest("SELECT x, ' into ' from z", false);
    }

    @Test
    public void testCreate() {
        helpTest(" create table x", true);
    }

    @Test
    public void testDrop() {
        helpTest("/* */ drop table x", true);
    }

}
