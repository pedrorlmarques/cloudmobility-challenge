package pt.cloudmobility.userservice.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.service.UserService;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class DefaultUserHandler implements UserHandler {

    private final UserService userService;

    public DefaultUserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserDto.class)
                .flatMap(this.userService::createUser)
                .flatMap(user -> ServerResponse
                        .created(URI.create("/users/" + user.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(user)));

    }
}
