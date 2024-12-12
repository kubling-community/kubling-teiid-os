package org.testcontainers.containers;

import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

public class KublingContainer<SELF extends KublingContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    public static final String NAME = "kubling";
    public static final String IMAGE = "kubling-ce";
    public static final String DEFAULT_TAG = "latest";
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("kubling/kubling-ce");
    public static final Integer DEFAULT_NATIVE_PORT = 35482;
    public static final Integer DEFAULT_HTTP_PORT = 8282;
    static final String DEFAULT_USER = "test";
    static final String DEFAULT_PASSWORD = "test";

    private int nativePort = DEFAULT_NATIVE_PORT;
    private int httpPort = DEFAULT_HTTP_PORT;
    private String databaseName = "TestVDB";
    private String username = DEFAULT_USER;
    private String password = DEFAULT_PASSWORD;


    public KublingContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    public KublingContainer(final String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    public KublingContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        addFixedExposedPort(nativePort, nativePort);
        addFixedExposedPort(httpPort, httpPort);
    }

    @Override
    protected void configure() {
        addEnv("ENABLE_WEB_CONSOLE", "FALSE");
        addEnv("SCRIPT_LOG_LEVEL", "DEBUG");
    }

    @Override
    public String getDriverClassName() {
        return "com.kubling.teiid.jdbc.TeiidDriver";
    }

    @Override
    public String getJdbcUrl() {
        String additionalUrlParams = constructUrlParameters("?", "&");
        return (
                "jdbc:teiid:" + databaseName + "@mm://" +
                        getHost() +
                        ":" +
                        getMappedPort(nativePort) +
                        ";" +
                        additionalUrlParams
        );
    }

    @Override
    protected WaitStrategy getWaitStrategy() {
        return new HttpWaitStrategy()
                .allowInsecure()
                .forPort(getMappedPort(httpPort))
                .forPath("/observe/health")
                .forResponsePredicate(r -> r.equals("{\"status\":\"UP\"}"));
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

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
        return self();
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return Set.of(httpPort);
    }
}