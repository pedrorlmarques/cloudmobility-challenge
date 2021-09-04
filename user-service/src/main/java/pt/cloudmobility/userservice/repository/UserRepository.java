package pt.cloudmobility.userservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import pt.cloudmobility.userservice.domain.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Integer> {

    Mono<User> findByIdentificationNumber(String identificationNumber);
}
