package pt.cloudmobility.userservice.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.service.UserService;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@Component
public class DefaultUserHandler implements UserHandler {

    private final UserService userService;
    private final Consumer<User> userCreationCallback;

    public DefaultUserHandler(UserService userService, Consumer<User> userCreationCallback) {
        this.userService = userService;
        this.userCreationCallback = userCreationCallback;
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserDto.class)
                .flatMap(userDto -> this.userService.createUser(userDto, userCreationCallback))
                .flatMap(user -> ServerResponse
                        .created(URI.create("/users/" + user.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(user)));

    }
}
