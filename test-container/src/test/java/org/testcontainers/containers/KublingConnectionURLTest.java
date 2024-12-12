package org.testcontainers.containers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KublingConnectionURLTest {

    @Test
    public void shouldReturnOriginalURLWhenEmptyQueryString() {
        KublingContainer<?> postgres = new FixedJdbcUrlKublingSQLContainer();
        String connectionUrl = postgres.constructUrlForConnection("");

        assertThat(postgres.getJdbcUrl()).as("Query String remains unchanged").isEqualTo(connectionUrl);
    }

    static class FixedJdbcUrlKublingSQLContainer extends KublingContainer<FixedJdbcUrlKublingSQLContainer> {

        public FixedJdbcUrlKublingSQLContainer() {
            super();
        }

        @Override
        public String getHost() {
            return "localhost";
        }

        @Override
        public Integer getMappedPort(int originalPort) {
            return 35482;
        }
    }
}
