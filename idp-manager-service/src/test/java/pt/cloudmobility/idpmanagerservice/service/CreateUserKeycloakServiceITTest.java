package pt.cloudmobility.idpmanagerservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cloudmobility.idpmanagerservice.IdpManagerServiceApplication;
import pt.cloudmobility.idpmanagerservice.KafkaContainerTestingSupport;
import pt.cloudmobility.idpmanagerservice.KeycloakContainerTestingSupport;
import pt.cloudmobility.idpmanagerservice.dto.InternalRole;
import pt.cloudmobility.idpmanagerservice.dto.KeycloakUserInputRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = IdpManagerServiceApplication.class)
class CreateUserKeycloakServiceITTest implements KeycloakContainerTestingSupport, KafkaContainerTestingSupport {

    @Autowired
    private CreateUserKeycloakService createUserKeycloakService;

    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    private String userId;

    @AfterEach
    void deleteKeycloakUser() {
        this.keycloak.realm(realm).users().delete(userId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"DOCTOR", "USER"})
    void testGivenKeycloakUserInputRequestItShouldCreateOnKeycloak(String role) {

        //setup Roles
        var roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("ROLE_" + role);
        roleRepresentation.setContainerId(realm);
        keycloak.realm(realm).roles().create(roleRepresentation);

        var keycloakUserInputRequest =
                new KeycloakUserInputRequest("2", "nam-1231e", "last",
                        (role.equals("USER") ? InternalRole.PATIENT.name() : InternalRole.DOCTOR.name()), "p@gmail.com");

        this.createUserKeycloakService.execute(keycloakUserInputRequest);

        await().untilAsserted(() -> {
            var userOptional = keycloak.realm(realm)
                    .users().list()
                    .stream().filter(userRepresentation -> userRepresentation.getUsername().equals(keycloakUserInputRequest.getEmail())).findFirst();
            assertThat(userOptional).isPresent();
            this.userId = userOptional.get().getId();
        });


    }
}
