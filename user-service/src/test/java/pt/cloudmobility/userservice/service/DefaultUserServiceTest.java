package pt.cloudmobility.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.cloudmobility.userservice.domain.InternalRole;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.repository.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class DefaultUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        this.userService = new DefaultUserService(userRepository);
    }

    @Test
    void testGivenUserDtoItShouldPersist() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");

        when(this.userRepository.findByIdentificationNumber(userToCreate.getIdentificationNumber())).thenReturn(Mono.empty());

        var savedUser = new User(1, userToCreate.getFirstName(), userToCreate.getLastName(), userToCreate.getIdentificationNumber(), userToCreate.getRole());

        when(this.userRepository.save(any())).thenReturn(Mono.just(savedUser));

        StepVerifier.create(
                        this.userService.createUser(userToCreate))
                .expectSubscription()
                .assertNext(userDto -> assertThat(userDto.getId()).isNotNull().isEqualTo(savedUser.getId()))
                .verifyComplete();
    }

    @Test
    void testGivenUserDtoAlreadyExistsItShouldThrowError() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");

        var user = new User();
        user.setId(1);
        user.setFirstName("name");
        user.setLastName("name");
        user.setRole(InternalRole.DOCTOR);
        user.setIdentificationNumber("1");


        when(this.userRepository.findByIdentificationNumber(userToCreate.getIdentificationNumber())).thenReturn(Mono.just(user));

        StepVerifier.create(
                        this.userService.createUser(userToCreate))
                .expectSubscription()
                .expectError(IllegalStateException.class)
                .verify();

    }


}
