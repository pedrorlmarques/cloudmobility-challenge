package pt.cloudmobility.idpmanagerservice.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cloudmobility.idpmanagerservice.KafkaContainerTestingSupport;
import pt.cloudmobility.idpmanagerservice.dto.EventType;
import pt.cloudmobility.idpmanagerservice.service.CreateUserKeycloakService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class KeycloakServiceFactoryITTest implements KafkaContainerTestingSupport {

    @Autowired
    private KeycloakServiceFactory keycloakServiceFactory;

    @Test
    void testGivenEventTypeItShouldReturnKeycloakInstance() {
        assertThat(this.keycloakServiceFactory.getInstance(EventType.USER_CREATED)).isInstanceOf(CreateUserKeycloakService.class);
    }

}
