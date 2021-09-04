package pt.cloudmobility.userservice.service;

import pt.cloudmobility.userservice.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserDto> createUser(UserDto userDto);
}
