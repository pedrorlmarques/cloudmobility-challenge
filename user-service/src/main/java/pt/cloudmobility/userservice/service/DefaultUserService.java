package pt.cloudmobility.userservice.service;

import org.springframework.stereotype.Service;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.mapper.UserMapper;
import pt.cloudmobility.userservice.repository.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDto> createUser(UserDto userDto) {
        return this.userRepository
                .findByIdentificationNumber(userDto.getIdentificationNumber())
                .flatMap(user -> Mono.error(new IllegalStateException("User " + user.getIdentificationNumber() + " already exists")))
                .switchIfEmpty(Mono.just(userDto)
                        .map(UserMapper.INSTANCE::convertTo)
                        .flatMap(userRepository::save)
                        .map(UserMapper.INSTANCE::convertTo))
                .cast(UserDto.class);
    }
}
