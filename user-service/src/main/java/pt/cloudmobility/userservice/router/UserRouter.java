package pt.cloudmobility.userservice.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.userservice.dto.UserDto;
import pt.cloudmobility.userservice.handler.UserHandler;

@Configuration(proxyBeanMethods = false)
public class UserRouter {

    @RouterOperation(path = "/api/users", beanClass = UserHandler.class, beanMethod = "createUser",
            operation = @Operation(operationId = "createUser", summary = "createUser", tags = {"User API"},
                    requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserDto.class)))
                    ,
                    responses = {
                            @ApiResponse(responseCode = "201", description = "successful operation")
                    })
    )
    @Bean
    public RouterFunction<ServerResponse> route(final UserHandler userHandler) {
        return RouterFunctions.route()
                .POST("/api/users", userHandler::createUser)
                .build();
    }
}
