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

import com.kubling.teiid.core.CorePlugin;
import com.kubling.teiid.core.util.Assertion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class LobSearchUtil {

    private static final int MOD = 37;

    public interface StreamProvider {

        InputStream getBinaryStream() throws SQLException;

    }

    static long position(
            StreamProvider pattern,
            long patternLength,
            StreamProvider target,
            long targetLength,
            long start,
            int bytesPerComparison) throws SQLException {
        if (pattern == null) {
            return -1;
        }

        patternLength *= bytesPerComparison;
        targetLength *= bytesPerComparison;

        if (start < 1) {
            Object[] params = new Object[]{start};
            throw new SQLException(CorePlugin.Util.getString("MMClob_MMBlob.2", params));
        }

        start = (start - 1) * bytesPerComparison;

        if (start + patternLength > targetLength) {
            return -1;
        }
        /*
         * Use karp-rabin matching to reduce the cost of back tracing for failed matches
         *
         * TODO: optimize for patterns that are small enough to fit in a reasonable buffer
         */
        try {
            try (InputStream patternStream = pattern.getBinaryStream();
                 InputStream targetStream = target.getBinaryStream();
                 InputStream laggingTargetStream = target.getBinaryStream()) {
                int patternHash = computeStreamHash(patternStream, patternLength);
                int lastMod = 1;
                for (int i = 0; i < patternLength; i++) {
                    lastMod *= MOD;
                }
                Assertion.assertTrue(targetStream.skip(start) == start);
                Assertion.assertTrue(laggingTargetStream.skip(start) == start);

                long position = start + 1;

                int streamHash = computeStreamHash(targetStream, patternLength);

                do {
                    if ((position - 1) % bytesPerComparison == 0 && patternHash == streamHash && validateMatch(pattern, target, position)) {
                        return (position - 1) / bytesPerComparison + 1;
                    }

                    streamHash = MOD * streamHash + targetStream.read() - lastMod * laggingTargetStream.read();
                    position++;

                } while (position + patternLength - 1 <= targetLength);

                return -1;
            }
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    /**
     * validate that the pattern matches the given position.
     * <p>
     * TODO: optimize to reuse the same targetstream/buffer for small patterns
     */
    static private boolean validateMatch(StreamProvider pattern, StreamProvider target, long position)
            throws IOException, SQLException {

        try (InputStream targetStream = target.getBinaryStream(); InputStream patternStream = pattern.getBinaryStream()) {
            Assertion.assertTrue(targetStream.skip(position - 1) == position - 1);
            int value;
            while ((value = patternStream.read()) != -1) {
                if (value != targetStream.read()) {
                    return false;
                }
            }
        }
        return true;
    }

    static private int computeStreamHash(InputStream is, long length) throws IOException {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = result * MOD + is.read();
        }
        return result;
    }

}
