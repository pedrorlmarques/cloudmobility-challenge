package pt.cloudmobility.idpmanagerservice.mapper;

import org.junit.jupiter.api.Test;
import pt.cloudmobility.idpmanagerservice.dto.EventType;
import pt.cloudmobility.idpmanagerservice.dto.InternalRole;
import pt.cloudmobility.idpmanagerservice.dto.UserDto;
import pt.cloudmobility.idpmanagerservice.dto.UserEvent;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUserInputRequestMapperTest {


    @Test
    void testGivenUserEventItShouldMapToKeycloakUserInput() {

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

        var keycloakUserInputRequest = KeycloakUserInputRequestMapper.INSTANCE.convertTo(userEvent);

        assertThat(keycloakUserInputRequest).isNotNull();
        assertThat(keycloakUserInputRequest.getUserId()).isNotNull().isEqualTo(userDto.getId().toString());
        assertThat(keycloakUserInputRequest.getEmail()).isNotNull().isEqualTo(userDto.getEmail());
        assertThat(keycloakUserInputRequest.getFirstName()).isNotNull().isEqualTo(userDto.getFirstName());
        assertThat(keycloakUserInputRequest.getLastName()).isNotNull().isEqualTo(userDto.getLastName());
        assertThat(keycloakUserInputRequest.getRole()).isNotNull().isEqualTo(userDto.getRole().name());
    }

}
