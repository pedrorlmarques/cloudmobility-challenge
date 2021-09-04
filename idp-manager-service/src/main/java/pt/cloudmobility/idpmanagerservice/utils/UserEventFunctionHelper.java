package pt.cloudmobility.idpmanagerservice.utils;

import pt.cloudmobility.idpmanagerservice.dto.UserEvent;
import pt.cloudmobility.idpmanagerservice.factory.KeycloakServiceFactory;
import pt.cloudmobility.idpmanagerservice.mapper.KeycloakUserInputRequestMapper;

import java.util.function.Consumer;

public final class UserEventFunctionHelper {

    private UserEventFunctionHelper() {
        //private constructor
    }

    public static Consumer<UserEvent> onUserEvents(final KeycloakServiceFactory keycloakFactory) {
        return userEvent ->
                keycloakFactory.getInstance(userEvent.getEventType())
                        .execute(KeycloakUserInputRequestMapper.INSTANCE.convertTo(userEvent));
    }
}
