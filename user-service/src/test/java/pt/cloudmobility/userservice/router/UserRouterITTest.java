package pt.cloudmobility.userservice.router;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import pt.cloudmobility.userservice.KafkaContainerTestingSupport;
import pt.cloudmobility.userservice.PostgreSQLContainerTestingSupport;
import pt.cloudmobility.userservice.UserServiceApplication;
import pt.cloudmobility.userservice.domain.InternalRole;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.repository.UserRepository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureWebTestClient
@SpringBootTest(classes = UserServiceApplication.class)
class UserRouterITTest extends PostgreSQLContainerTestingSupport implements KafkaContainerTestingSupport {

    public static final String API_USERS = "/api/users";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${spring.cloud.stream.bindings.userEventSupplier-out-0.destination}")
    private String userEventsTopic;

    @AfterEach
    void deleteDatabase() {
        this.userRepository.deleteAll().block();
    }

    // By default each time the producer sends a message to non-existing topic kafka will create.
    @AfterEach
    public void teardownKafka() {
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            admin.deleteTopics(List.of(this.userEventsTopic));
        }
    }

    @Test
    void testGivenUserDtoItShouldPersistAndSendUserEvent() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");

        this.webTestClient.post().uri(API_USERS).contentType(APPLICATION_JSON)
                .body(Mono.just(userToCreate), UserDto.class)
                .exchange()
                .expectStatus()
                .isCreated();

        await("verify that the user exists on the database")
                .untilAsserted(() ->
                        assertThat(this.userRepository
                                .findByIdentificationNumber(userToCreate.getIdentificationNumber())
                                .block())
                                .isNotNull());

        try (var consumer = this.createConsumer(UUID.randomUUID().toString(), this.userEventsTopic)) {
            await("verify that user event is sent").untilAsserted(() -> {
                var records = consumer.poll(Duration.ofSeconds(1));
                assertThat(records).hasSize(1);
                assertThat(records).allSatisfy(record ->
                        assertThat(record.value()).isNotNull().contains(userToCreate.getIdentificationNumber()));
            });
        }
    }

    @Test
    void testGivenUserDtoAlreadyExistsItShouldThrowError() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");

        var user = new User();
        user.setFirstName("name");
        user.setLastName("name");
        user.setRole(InternalRole.DOCTOR);
        user.setIdentificationNumber("1");

        this.userRepository.save(user).block();


        this.webTestClient.post().uri(API_USERS).contentType(APPLICATION_JSON)
                .body(Mono.just(userToCreate), UserDto.class)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
