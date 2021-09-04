package pt.cloudmobility.userservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgreSQLContainerTestingSupport {

    private static final String POSTGRESQL_DOCKER_IMAGE_NAME = "postgres:12.3";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static PostgreSQLContainer postgreSQLContainer;

    @BeforeAll
    public static void setUp() {

        postgreSQLContainer = new PostgreSQLContainer(POSTGRESQL_DOCKER_IMAGE_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);

        postgreSQLContainer.withInitScript("schema.sql");
    }

    @AfterAll
    public static void tearDown() {
        postgreSQLContainer.stop();
    }

    @DynamicPropertySource
    static void postgreSQLContainer(DynamicPropertyRegistry registry) {

        postgreSQLContainer.start();
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + postgreSQLContainer.getContainerIpAddress() + ":"
                + postgreSQLContainer.getFirstMappedPort());
        registry.add("spring.r2dbc.name", postgreSQLContainer::getDatabaseName);
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    }
}
