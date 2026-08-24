package com.kubling.hibernate.dialect;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KublingContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.MountableFile;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public abstract class BaseDialectTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDialectTest.class);

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

    protected Configuration configuration;
    protected ServiceRegistry serviceRegistry;

    public static final KublingContainer<?> kubling = new KublingContainer<>()
            .withEnv(DEFAULT_ENV)
            .withCopyFileToContainer(
                    MountableFile.forHostPath(String.format("%s/vdb/%s", USER_DIR, DEFAULT_APP_CONFIG)),
                    DEFAULT_CONTAINER_APP_CONFIG)
            .withCopyFileToContainer(
                    MountableFile.forHostPath(String.format("%s/vdb/%s", USER_DIR, DEFAULT_BUNDLE)),
                    DEFAULT_CONTAINER_BUNDLE)
            .withLogConsumer(new Slf4jLogConsumer(LOGGER))
            .withExposedPorts(KublingContainer.DEFAULT_NATIVE_PORT, KublingContainer.DEFAULT_HTTP_PORT);

    @BeforeAll
    public static void initTestContainer() {
        Startables.deepStart(Stream.of(kubling)).join();
    }

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setProperties(hibernateProperties());
        serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
    }

    @AfterEach
    public void tearDown() {
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "com.kubling.hibernate.dialect.KublingDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.connection.driver_class", "com.kubling.jdbc.KublingDriver");
        properties.setProperty("hibernate.connection.url", "jdbc:kubling:TestVDB@mm://localhost:" + getKublingPort());
        properties.setProperty("hibernate.connection.username", "sa");
        properties.setProperty("hibernate.connection.password", "sa");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }

    @Entity(name = "BaseDialectTest.Application")
    @Table(name = "APPLICATION", schema = "test_schema")
    public static class Application {
        @Id
        private String id;
        private String name;

        public Application() {
        }

        public Application(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static int getKublingPort() {
        return kubling.getMappedPort(KublingContainer.DEFAULT_NATIVE_PORT);
    }

}
