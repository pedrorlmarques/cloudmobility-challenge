package pt.cloudmobility.idpmanagerservice.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.cloudmobility.idpmanagerservice.dto.EventType;
import pt.cloudmobility.idpmanagerservice.dto.InternalRole;
import pt.cloudmobility.idpmanagerservice.dto.UserDto;
import pt.cloudmobility.idpmanagerservice.dto.UserEvent;
import pt.cloudmobility.idpmanagerservice.factory.KeycloakServiceFactory;
import pt.cloudmobility.idpmanagerservice.service.CreateUserKeycloakService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserEventFunctionHelperTest {

    @Mock
    private KeycloakServiceFactory keycloakServiceFactory;

    @Mock
    private CreateUserKeycloakService createUserKeycloakService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGivenUserEventTypeItShouldCallCreateUserService() {

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

        when(this.keycloakServiceFactory.getInstance(EventType.USER_CREATED)).thenReturn(this.createUserKeycloakService);

        doNothing().when(this.createUserKeycloakService).execute(any());

        UserEventFunctionHelper.onUserEvents(this.keycloakServiceFactory).accept(userEvent);

        verify(this.keycloakServiceFactory, times(1)).getInstance(any());
        verify(this.createUserKeycloakService, times(1)).execute(any());

    }
}
