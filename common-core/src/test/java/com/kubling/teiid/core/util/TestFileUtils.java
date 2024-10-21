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
import com.kubling.teiid.core.TeiidException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @since 4.0
 */
public final class TestFileUtils {

    private static final String FILE_NAME = UnitTestUtil.getTestDataPath() + File.separator + "fakeScript.txt";

    private final static String TEMP_DIR_NAME = "tempdir";
    File tempDir;
    public static final String TEMP_FILE = "delete.me";
    public static final String TEMP_FILE_RENAMED = "delete.me.old";
    private final static String TEMP_FILE_NAME = UnitTestUtil.getTestDataPath() + File.separator + "tempfile.txt";
    private final static String TEMP_FILE_NAME2 = "tempfile2.txt";

    @BeforeEach
    public void setUp() {

        //create a temp directory
        tempDir = new File(TEMP_DIR_NAME);
        tempDir.mkdir();
    }

    @AfterEach
    public void tearDown() {
        try {
            tempDir.delete();
        } catch (Exception e) {
            // Ignored
        }

        try {
            new File(TEMP_FILE_NAME).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new File(TEMP_FILE_NAME2).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests FileUtils.testDirectoryPermissions()
     *
     * @since 4.3
     */
    @Test
    public void testTestDirectoryPermissions() throws Exception {


        //positive case
        TestFileUtils.testDirectoryPermissions(TEMP_DIR_NAME);

        //negative case: dir doesn't exist
        try {
            TestFileUtils.testDirectoryPermissions("fakeDir");
            fail("Expected a MetaMatrixCoreException");
        } catch (TeiidException e) {
            // Ignored
        }
    }


    /**
     * Tests FileUtils.remove()
     *
     * @since 4.3
     */
    @Test
    public void testRemove() throws Exception {
        ObjectConverterUtil.write(new FileInputStream(FILE_NAME), TEMP_FILE_NAME);

        //positive case
        FileUtils.remove(new File(TEMP_FILE_NAME));
        assertFalse(new File(TEMP_FILE_NAME).exists(), "Expected File to not exist");


        //call again - this should not throw an exception
        FileUtils.remove(new File(TEMP_FILE_NAME));
    }

    /**
     * Test whether it's possible to read and write files in the specified directory.
     *
     * @param dirPath Name of the directory to test
     * @throws TeiidException
     * @since 4.3
     */
    public static void testDirectoryPermissions(String dirPath) throws TeiidException {

        //try to create a file
        File tmpFile = new File(dirPath + File.separatorChar + TestFileUtils.TEMP_FILE);
        boolean success = false;
        try {
            success = tmpFile.createNewFile();
        } catch (IOException e) {
            // Ignored
        }
        if (!success) {
            throw new TeiidException("cannot create file in " + dirPath);
        }

        //test if file can be written to
        if (!tmpFile.canWrite()) {
            throw new TeiidException("cannot write " + dirPath);
        }

        //test if file can be read
        if (!tmpFile.canRead()) {
            throw new TeiidException("cannot read " + dirPath);
        }

        //test if file can be renamed
        File newFile = new File(dirPath + File.separatorChar + TestFileUtils.TEMP_FILE_RENAMED);
        success = false;
        try {
            success = tmpFile.renameTo(newFile);
        } catch (Exception e) {
            // Ignored
        }
        if (!success) {
            throw new TeiidException("failed to rename " + dirPath);
        }

        //test if file can be deleted
        success = false;
        try {
            success = newFile.delete();
        } catch (Exception e) {
            // Ignored
        }
        if (!success) {
            final String msg = CorePlugin.Util.getString("FileUtils.Unable_to_delete_file_in", dirPath);
            throw new TeiidException(msg);
        }
    }

}
