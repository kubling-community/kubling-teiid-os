package com.kubling.teiid.core.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"nls","resource"})
public class TestInputStreamReader {

    @Test public void testMultiByte() throws Exception {
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(new byte[] {(byte)80, (byte)-61, (byte)-70}), Charset.forName("UTF-8").newDecoder(), 2);
        assertEquals(80, isr.read());
        assertEquals(250, isr.read());
    }

    @Test public void testMultiByte1() throws Exception {
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(new byte[] {(byte)80, (byte)-61, (byte)-70, (byte)-61, (byte)-70, (byte)80, (byte)-61, (byte)-70}), Charset.forName("UTF-8").newDecoder(), 4);
        int count = 0;
        while (isr.read() != -1) {
            count++;
        }
        assertEquals(5, count);
    }

    @Test public void testError() throws Exception {
        InputStreamReader isr = new InputStreamReader(
                new ByteArrayInputStream(new byte[] {(byte)80, (byte)-61, (byte)-70, (byte)-61, (byte)-70, (byte)80, (byte)-61, (byte)-70}),
                Charset.forName("ASCII").newDecoder(), 4);
        assertThrows(IOException.class, () -> isr.read());
    }

}
