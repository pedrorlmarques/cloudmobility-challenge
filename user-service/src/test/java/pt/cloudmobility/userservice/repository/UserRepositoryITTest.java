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

        var identificationNumber = "123";

        this.userRepository.save(new User(null, "", "", identificationNumber, InternalRole.DOCTOR, "p@gmail.com")).block();

        StepVerifier.create(this.userRepository.findByIdentificationNumber(identificationNumber))
                .expectSubscription()
                .assertNext(user -> assertThat(user.getIdentificationNumber()).isNotNull().isEqualTo(identificationNumber))
                .verifyComplete();
    }

    @Test
    void testGivenNonExistingIdentificationNumberItShouldReturnEmpty() {

        var identificationNumber = "123";

        this.userRepository.save(new User(null, "", "", identificationNumber, InternalRole.DOCTOR, "")).block();

        StepVerifier.create(this.userRepository.findByIdentificationNumber("124"))
                .expectSubscription()
                .verifyComplete();
    }

}
