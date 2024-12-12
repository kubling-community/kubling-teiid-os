package org.testcontainers.db;

import org.testcontainers.containers.KublingContainer;
import org.testcontainers.utility.MountableFile;

import java.util.Map;

public class AbstractKublingDatabaseTest extends AbstractContainerDatabaseTest {

    protected static String USER_DIR = System.getProperty("user.dir");
    protected static String DEFAULT_APP_CONFIG = "app-config.yaml";
    protected static String DEFAULT_BUNDLE = "descriptor.zip";
    protected static String DEFAULT_CONTAINER_CONFIG_DIR = "app_data";

    protected static String DEFAULT_CONTAINER_APP_CONFIG =
            String.format("/%s/%s", DEFAULT_CONTAINER_CONFIG_DIR, DEFAULT_APP_CONFIG);
    protected static String DEFAULT_CONTAINER_BUNDLE =
            String.format("/%s/%s", DEFAULT_CONTAINER_CONFIG_DIR, DEFAULT_BUNDLE);

    protected static Map<String, String> DEFAULT_ENV =
            Map.of("ENABLE_WEB_CONSOLE", "FALSE",
                    "SCRIPT_LOG_LEVEL", "DEBUG",
                    "APP_CONFIG", DEFAULT_CONTAINER_APP_CONFIG,
                    "DESCRIPTOR_BUNDLE", DEFAULT_CONTAINER_BUNDLE);

    protected KublingContainer copyDefaultFiles(KublingContainer mutable) {
        mutable.withCopyFileToContainer(
                MountableFile.forHostPath(String.format("%s/vdb/%s", USER_DIR, DEFAULT_APP_CONFIG)),
                DEFAULT_CONTAINER_APP_CONFIG);
        mutable.withCopyFileToContainer(
                MountableFile.forHostPath(String.format("%s/vdb/%s", USER_DIR, DEFAULT_BUNDLE)),
                DEFAULT_CONTAINER_BUNDLE);

        return mutable;
    }
}
