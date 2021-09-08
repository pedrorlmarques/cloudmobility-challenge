package pt.cloudmobility.idpmanagerservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.cloudmobility.idpmanagerservice.dto.UserEvent;
import pt.cloudmobility.idpmanagerservice.factory.KeycloakServiceFactory;
import pt.cloudmobility.idpmanagerservice.mapper.KeycloakUserInputRequestMapper;

import java.util.function.Consumer;

public final class UserEventFunctionHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserEventFunctionHelper.class);

    private UserEventFunctionHelper() {
        //private constructor
    }

    public static Consumer<UserEvent> onUserEvents(final KeycloakServiceFactory keycloakFactory) {
        return userEvent -> {
            logger.info("Received {}", userEvent);
            keycloakFactory.getInstance(userEvent.getEventType())
                    .execute(KeycloakUserInputRequestMapper.INSTANCE.convertTo(userEvent));
        };
    }
}
