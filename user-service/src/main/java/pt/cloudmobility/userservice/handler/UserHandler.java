package pt.cloudmobility.userservice.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface UserHandler {

    Mono<ServerResponse> createUser(ServerRequest request);
}
