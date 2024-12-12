package org.testcontainers.jdbc.junit.kubling;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KublingContainer;
import org.testcontainers.db.AbstractKublingDatabaseTest;
import org.testcontainers.utility.MountableFile;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleKublingTest extends AbstractKublingDatabaseTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleKublingTest.class);

    @Test
    public void testSimple() throws SQLException {
        try (KublingContainer<?> kubling = new KublingContainer<>()) {

            kubling.withEnv(DEFAULT_ENV);
            kubling.withCopyFileToContainer(
                    MountableFile.forHostPath(String.format("%s/vdb/%s", USER_DIR, DEFAULT_APP_CONFIG)),
                    DEFAULT_CONTAINER_APP_CONFIG);
            kubling.withCopyFileToContainer(
                    MountableFile.forHostPath(String.format("%s/vdb/%s", USER_DIR, DEFAULT_BUNDLE)),
                    DEFAULT_CONTAINER_BUNDLE);
            kubling.withExposedPorts(KublingContainer.DEFAULT_NATIVE_PORT, KublingContainer.DEFAULT_HTTP_PORT);
            kubling.start();

            ResultSet resultSet = performQuery(kubling, "SELECT 1");
            int resultSetInt = resultSet.getInt(1);
            assertThat(resultSetInt).as("A basic SELECT query succeeds").isEqualTo(1);
            assertHasCorrectExposedAndLivenessCheckPorts(kubling);
        }
    }

    private void assertHasCorrectExposedAndLivenessCheckPorts(KublingContainer<?> kubling) {
        assertThat(kubling.getExposedPorts()).contains(KublingContainer.DEFAULT_NATIVE_PORT, KublingContainer.DEFAULT_HTTP_PORT);
        assertThat(kubling.getLivenessCheckPortNumbers())
                .containsExactly(kubling.getMappedPort(KublingContainer.DEFAULT_HTTP_PORT));
    }

}
