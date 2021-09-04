package pt.cloudmobility.userservice.router;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import pt.cloudmobility.userservice.PostgreSQLContainerTestingSupport;
import pt.cloudmobility.userservice.UserServiceApplication;
import pt.cloudmobility.userservice.domain.InternalRole;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.repository.UserRepository;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureWebTestClient
@SpringBootTest(classes = UserServiceApplication.class)
class UserRouterITTest extends PostgreSQLContainerTestingSupport {

    public static final String API_USERS = "/api/users";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    void deleteDatabase() {
        this.userRepository.deleteAll().block();
    }

    @Test
    void testGivenUserDtoItShouldPersist() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");

        this.webTestClient.post().uri(API_USERS).contentType(APPLICATION_JSON)
                .body(Mono.just(userToCreate), UserDto.class)
                .exchange()
                .expectStatus()
                .isCreated();

        await("verify that the user exists on the database")
                .untilAsserted(() ->
                        assertThat(this.userRepository
                                .findByIdentificationNumber(userToCreate.getIdentificationNumber())
                                .block())
                                .isNotNull());
    }

    @Test
    void testGivenUserDtoAlreadyExistsItShouldThrowError() {

        var userToCreate = new UserDto();
        userToCreate.setFirstName("name");
        userToCreate.setLastName("name");
        userToCreate.setRole(InternalRole.DOCTOR);
        userToCreate.setIdentificationNumber("1");

        var user = new User();
        user.setFirstName("name");
        user.setLastName("name");
        user.setRole(InternalRole.DOCTOR);
        user.setIdentificationNumber("1");

        this.userRepository.save(user).block();


        this.webTestClient.post().uri(API_USERS).contentType(APPLICATION_JSON)
                .body(Mono.just(userToCreate), UserDto.class)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
