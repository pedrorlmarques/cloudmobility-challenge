package pt.cloudmobility.idpmanagerservice.service;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.cloudmobility.idpmanagerservice.dto.InternalRole;
import pt.cloudmobility.idpmanagerservice.dto.KeycloakUserInputRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CreateUserKeycloakServiceTest {

    private final String realm = "test";

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    private KeycloakService keycloakService;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.keycloakService = new CreateUserKeycloakService(keycloak, realm);
        this.logCaptor = LogCaptor.forClass(CreateUserKeycloakService.class);

    }

    @AfterEach
    void tearDown() {
        this.logCaptor.clearLogs();
        this.logCaptor.close();
    }

    @Test
    void testGivenAnExistingUserNameItShouldWarnUserAlreadyExists() {

        var keycloakUserInputRequest =
                new KeycloakUserInputRequest("2", "nam-1231e", "last",
                        InternalRole.DOCTOR.name(), "p@gmail.com");

        when(keycloak.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        var userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(keycloakUserInputRequest.getEmail());

        when(usersResource.list()).thenReturn(List.of(userRepresentation));

        this.keycloakService.execute(keycloakUserInputRequest);

        assertThat(logCaptor.getWarnLogs()).contains("User Already Exists");
    }
}
