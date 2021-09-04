package pt.cloudmobility.idpmanagerservice;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface KeycloakContainerTestingSupport {

    KeycloakContainer keycloak = new KeycloakContainer();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        keycloak.start();
        registry.add("keycloak.serverUrl", keycloak::getAuthServerUrl);
        registry.add("keycloak.realm", () -> "master");
        registry.add("keycloak.clientId", () -> "admin-cli");
    }

}
