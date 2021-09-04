package pt.cloudmobility.idpmanagerservice.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.cloudmobility.idpmanagerservice.dto.UserEvent;
import pt.cloudmobility.idpmanagerservice.factory.KeycloakServiceFactory;
import pt.cloudmobility.idpmanagerservice.utils.UserEventFunctionHelper;

import java.util.function.Consumer;

@Configuration
public class UserEventConsumerConfiguration {

    //definition of the spring cloud function
    @Bean
    public Consumer<UserEvent> onUserEvent(final KeycloakServiceFactory keycloakFactory) {
        return UserEventFunctionHelper.onUserEvents(keycloakFactory);
    }
}
