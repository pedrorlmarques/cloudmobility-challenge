package pt.cloudmobility.userservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.mapper.UserMapper;
import pt.cloudmobility.userservice.repository.UserRepository;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Mono<User> createUser(UserDto userDto, Consumer<User> callback) {
        return this.userRepository
                .findByIdentificationNumber(userDto.getIdentificationNumber())
                .flatMap(user -> Mono.error(new IllegalStateException("User " + user.getIdentificationNumber() + " already exists")))
                .switchIfEmpty(Mono.just(userDto)
                        .map(UserMapper.INSTANCE::convertTo)
                        .flatMap(userRepository::save))
                .cast(User.class)
                .delayUntil(user -> Mono.fromRunnable(() -> callback.accept(user)));
    }
}
