package pt.cloudmobility.idpmanagerservice.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.cloudmobility.idpmanagerservice.dto.InternalRole;
import pt.cloudmobility.idpmanagerservice.dto.KeycloakUserInputRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CreateUserKeycloakService implements KeycloakService {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserKeycloakService.class);
    private final Keycloak keycloak;
    private final String realm;

    public CreateUserKeycloakService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    @Override
    public void execute(KeycloakUserInputRequest userInputRequest) {

        // Get realm resource
        var realmResource = keycloak.realm(realm);
        var userRessource = realmResource.users();

        //verify if user exists
        userRessource.list()
                .stream()
                .filter(userRepresentation -> userRepresentation.getUsername() != null)
                .filter(userRepresentation -> userRepresentation.getUsername().equals(userInputRequest.getEmail()))
                .findFirst()
                .ifPresentOrElse(userRepresentation -> logger.warn("User Already Exists"),
                        () -> this.createNewUser(userInputRequest, realmResource, userRessource));
    }

    private void createNewUser(KeycloakUserInputRequest userInputRequest, RealmResource realmResource, UsersResource userRessource) {

        var userRepresentation = createUserRepresentation(userInputRequest.getEmail(),
                userInputRequest.getFirstName(), userInputRequest.getLastName(), userInputRequest.getUserId(),
                userInputRequest.getEmail());

        var userId = createUser(userRepresentation, userRessource);

        var roleRepresentation = createRoleRepresentation(userInputRequest.getRole(), realmResource);

        assignRole(roleRepresentation, userRessource, userId);

        var credentialRepresentation = createDefaultCredentialRepresentation();

        assignCredentials(userRessource, userId, credentialRepresentation);
    }

    private void assignCredentials(UsersResource userRessource, String userId, CredentialRepresentation credentials) {
        userRessource.get(userId).resetPassword(credentials);
    }

    private RoleRepresentation createRoleRepresentation(String internalRole, RealmResource realmResource) {

        var role = "ROLE_" + (internalRole.equals(InternalRole.DOCTOR.name()) ? InternalRole.DOCTOR.name()
                : "USER");

        return realmResource.roles()
                .get(role)
                .toRepresentation();
    }

    private String createUser(UserRepresentation user, UsersResource userRessource) {
        try (var response = userRessource.create(user)) {
            var userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            logger.info("User created with userId: {}", userId);
            return userId;
        }
    }

    private void assignRole(RoleRepresentation role, UsersResource userRessource, String userId) {
        //assign role to user
        userRessource.get(userId).roles().realmLevel().add(Arrays.asList(role));
    }

    private CredentialRepresentation createDefaultCredentialRepresentation() {
        // Define password credential
        var passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("password");
        return passwordCred;
    }

    private UserRepresentation createUserRepresentation(String userName, String firstName, String lastName, String userId, String email) {
        // Define user
        var user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setAttributes(Map.of("userId", List.of(userId)));
        return user;
    }
}
