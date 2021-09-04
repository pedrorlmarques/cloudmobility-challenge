package pt.cloudmobility.idpmanagerservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cloudmobility.idpmanagerservice.IdpManagerServiceApplication;
import pt.cloudmobility.idpmanagerservice.KafkaContainerTestingSupport;
import pt.cloudmobility.idpmanagerservice.KeycloakContainerTestingSupport;
import pt.cloudmobility.idpmanagerservice.dto.EventType;
import pt.cloudmobility.idpmanagerservice.dto.InternalRole;
import pt.cloudmobility.idpmanagerservice.dto.UserDto;
import pt.cloudmobility.idpmanagerservice.dto.UserEvent;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = IdpManagerServiceApplication.class)
class UserEventConsumerConfigurationITTest implements KafkaContainerTestingSupport, KeycloakContainerTestingSupport {

    public static final String ROLE_DOCTOR = "ROLE_DOCTOR";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${spring.cloud.stream.bindings.onUserEvent-in-0.destination}")
    private String userEventsTopic;

    private String userId;

    @AfterEach
    void deleteKeycloakUser() {
        var realm = this.keycloak.realm(this.realm);
        realm.users().delete(userId);
        realm.roles().deleteRole(ROLE_DOCTOR);
    }

    // By default each time the producer sends a message to non-existing topic kafka will create.
    @AfterEach
    public void teardownKafka() {
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            admin.deleteTopics(List.of(this.userEventsTopic));
        }
    }

    @Test
    void testGivenUserEventItShouldCreateUserOnKeycloak() throws Exception {

        //setup keycloak
        //setup Roles
        var roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(ROLE_DOCTOR);
        roleRepresentation.setContainerId(realm);
        keycloak.realm(realm).roles().create(roleRepresentation);

        var userEvent = new UserEvent();

        var userDto = new UserDto();
        userDto.setId(2);
        userDto.setFirstName("nam-1231e");
        userDto.setLastName("last");
        userDto.setIdentificationNumber("123");
        userDto.setRole(InternalRole.DOCTOR);
        userDto.setEmail("p@gmail.com");

        userEvent.setSubject(userDto);
        userEvent.setEventType(EventType.USER_CREATED);

        try (var userEventsProducer = createProducer()) {
            final var message = this.objectMapper.writeValueAsString(userEvent);
            final var record =
                    new ProducerRecord<String, Object>(this.userEventsTopic, "", message);
            userEventsProducer.send(record).get();
        }

        await().untilAsserted(() -> {
            var userOptional = keycloak.realm(realm)
                    .users().list()
                    .stream().filter(userRepresentation -> userRepresentation.getUsername().equals(userDto.getEmail())).findFirst();
            assertThat(userOptional).isPresent();
            this.userId = userOptional.get().getId();
        });
    }
}
