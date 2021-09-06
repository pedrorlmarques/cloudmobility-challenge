package pt.cloudmobility.userservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import pt.cloudmobility.userservice.PostgreSQLContainerTestingSupport;
import pt.cloudmobility.userservice.domain.InternalRole;
import pt.cloudmobility.userservice.domain.User;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class UserRepositoryITTest extends PostgreSQLContainerTestingSupport {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void deleteDatabase() {
        this.userRepository.deleteAll().block();
    }

    @Test
    void testGivenIdentificationNumberItShouldReturnUser() {

        var email = "p@gmail.com";

        this.userRepository.save(new User(null, "", "", "123", InternalRole.DOCTOR, email)).block();

        StepVerifier.create(this.userRepository.findByEmail(email))
                .expectSubscription()
                .assertNext(user -> assertThat(user.getEmail()).isNotNull().isEqualTo(email))
                .verifyComplete();
    }

    @Test
    void testGivenNonExistingIdentificationNumberItShouldReturnEmpty() {

        var email = "p@gmail.com";

        this.userRepository.save(new User(null, "", "", "", InternalRole.DOCTOR, "p21@gmail.com")).block();

        StepVerifier.create(this.userRepository.findByEmail(email))
                .expectSubscription()
                .verifyComplete();
    }

}
