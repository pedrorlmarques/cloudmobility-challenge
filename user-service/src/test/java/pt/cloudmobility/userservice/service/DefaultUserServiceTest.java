package pt.cloudmobility.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.cloudmobility.userservice.domain.InternalRole;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.EventType;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.dto.UserEvent;
import pt.cloudmobility.userservice.repository.UserRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private Consumer<User> userCreationCallback;

    @Mock
    private Sinks.Many<UserEvent> userEventSink;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        this.userService = new DefaultUserService(userRepository);
        this.userCreationCallback = new UserCreationCallback(userEventSink);
    }

    @Test
    void testGivenUserDtoItShouldPersist() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");
        userToCreate.setEmail("p@gmail.com");

        when(this.userRepository.findByIdentificationNumber(userToCreate.getIdentificationNumber())).thenReturn(Mono.empty());

        var savedUser = new User(1, userToCreate.getFirstName(), userToCreate.getLastName(), userToCreate.getIdentificationNumber(), userToCreate.getRole(), userToCreate.getEmail());

        when(this.userRepository.save(any())).thenReturn(Mono.just(savedUser));

        var event = new UserEvent(savedUser, EventType.USER_CREATED);

        doNothing().when(this.userEventSink).emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);

        StepVerifier.create(
                        this.userService.createUser(userToCreate, userCreationCallback))
                .expectSubscription()
                .assertNext(userDto -> assertThat(userDto.getId()).isNotNull().isEqualTo(savedUser.getId()))
                .verifyComplete();

        verify(this.userEventSink, times(1)).emitNext(any(), any());

    }

    @Test
    void testGivenUserDtoAlreadyExistsItShouldThrowError() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");
        userToCreate.setEmail("p@gmail.com");

        var user = new User();
        user.setId(1);
        user.setFirstName("name");
        user.setLastName("name");
        user.setRole(InternalRole.DOCTOR);
        user.setIdentificationNumber("1");

        when(this.userRepository.findByIdentificationNumber(userToCreate.getIdentificationNumber())).thenReturn(Mono.just(user));

        StepVerifier.create(
                        this.userService.createUser(userToCreate, userCreationCallback))
                .expectSubscription()
                .expectError(IllegalStateException.class)
                .verify();

        verify(this.userEventSink, times(0)).emitNext(any(), any());
    }


}
