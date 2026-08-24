package com.kubling.query.unittest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourcesUtil {

    public static String getClassPathResource(String resourceName) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream(resourceName)) {
            if (is == null) throw new FileNotFoundException(resourceName);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
