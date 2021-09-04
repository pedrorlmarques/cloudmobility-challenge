package pt.cloudmobility.idpmanagerservice.factory;

import org.springframework.stereotype.Component;
import pt.cloudmobility.idpmanagerservice.dto.EventType;
import pt.cloudmobility.idpmanagerservice.service.KeycloakService;

import java.util.Map;

@Component
public class KeycloakServiceFactory {

    private final Map<String, KeycloakService> keycloakServiceMap;

    public KeycloakServiceFactory(Map<String, KeycloakService> keycloakServiceMap) {
        this.keycloakServiceMap = keycloakServiceMap;
    }

    public KeycloakService getInstance(EventType eventType) {

        if (eventType.equals(EventType.USER_CREATED)) {
            return keycloakServiceMap.get("createUserKeycloakService");
        }
        return null;
    }
}
