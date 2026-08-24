package org.testcontainers.containers;

import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

public class KublingContainer<SELF extends KublingContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    public static final String DEFAULT_TAG = "latest";
    public static final int DEFAULT_NATIVE_PORT = 35482;
    public static final int DEFAULT_PG_PORT = 35432;
    public static final int DEFAULT_HTTP_PORT = 8282;

    private static final DockerImageName DEFAULT_IMAGE_NAME =
            DockerImageName.parse("kubling/kubling").withTag(DEFAULT_TAG);

    private int nativePort = DEFAULT_NATIVE_PORT;
    private int pgPort = DEFAULT_PG_PORT;
    private int httpPort = DEFAULT_HTTP_PORT;

    private String databaseName = "TestVDB";
    private String username = "test";
    private String password = "test";
    private boolean isSecured = false;

    public KublingContainer() {
        this(DEFAULT_IMAGE_NAME);
    }

    public KublingContainer(String image) {
        this(DockerImageName.parse(image));
    }

    public KublingContainer(DockerImageName dockerImageName) {
        super(dockerImageName);

        withExposedPorts(nativePort, pgPort, httpPort);

        // Default env vars
        withEnv("ENABLE_WEB_CONSOLE", "FALSE");
        withEnv("SCRIPT_LOG_LEVEL", "DEBUG");
    }

    // ============
    // Configuration
    // ============

    public SELF withNativePort(int port) {
        this.nativePort = port;
        return self();
    }

    public SELF withPgPort(int port) {
        this.pgPort = port;
        return self();
    }

    public SELF withHttpPort(int port) {
        this.httpPort = port;
        return self();
    }

    public SELF withSecured(boolean secured) {
        this.isSecured = secured;
        return self();
    }

    @Override
    public SELF withDatabaseName(String dbName) {
        this.databaseName = dbName;
        return self();
    }

    @Override
    public SELF withUsername(String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(String password) {
        this.password = password;
        return self();
    }

    // ============
    // Testcontainers Overrides
    // ============

    @Override
    public String getDriverClassName() {
        return "com.kubling.jdbc.KublingDriver";
    }

    @Override
    public String getJdbcUrl() {
        String params = constructUrlParameters("?", "&");

        return "jdbc:kubling:" + databaseName +
                "@mm" + (isSecured ? "s" : "") + "://" +
                getHost() +
                ":" + getMappedPort(nativePort) +
                ";" + params;
    }

    @Override
    protected WaitStrategy getWaitStrategy() {
        return Wait.forHttp("/observe/health")
                .forStatusCode(200)
                .allowInsecure()
                .forPort(httpPort);
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return Set.of(getMappedPort(httpPort));
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }
}
