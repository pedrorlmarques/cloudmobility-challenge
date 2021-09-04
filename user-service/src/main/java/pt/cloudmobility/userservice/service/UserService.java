package pt.cloudmobility.userservice.service;

import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.UserDto;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public interface UserService {

    Mono<User> createUser(UserDto userDto, Consumer<User> callback);
}
