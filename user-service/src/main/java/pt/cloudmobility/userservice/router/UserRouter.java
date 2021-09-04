package pt.cloudmobility.userservice.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.userservice.handler.UserHandler;

@Configuration(proxyBeanMethods = false)
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> route(final UserHandler userHandler) {
        return RouterFunctions.route()
                .POST("/api/users", userHandler::createUser)
                .build();
    }
}
