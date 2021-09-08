package pt.cloudmobility.appointmentservice.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.appointmentservice.dto.CreateUnavailabilityRequest;
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.handler.DoctorsHandler;

import java.time.LocalDateTime;

@Configuration(proxyBeanMethods = false)
public class DoctorsRouterConfiguration {

    @RouterOperations({
            @RouterOperation(path = "/api/doctors/appointments", beanClass = DoctorsHandler.class, beanMethod = "fetchAppointments",
                    operation = @Operation(operationId = "fetchAppointments", summary = "fetchAppointments", tags = {"Doctors API"},
                            parameters = {
                                    @Parameter(in = ParameterIn.QUERY, name = "startDate", description = "startDate", schema = @Schema(implementation = LocalDateTime.class)),
                                    @Parameter(in = ParameterIn.QUERY, name = "endDate", description = "endDate", schema = @Schema(implementation = LocalDateTime.class))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SlotDto.class))))
                            })),
            @RouterOperation(path = "/api/doctors/unavailability", beanClass = DoctorsHandler.class, beanMethod = "createUnavailability",
                    operation = @Operation(operationId = "createUnavailability", summary = "createUnavailability", tags = {"Doctors API"},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CreateUnavailabilityRequest.class)))
                            ,
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "successful operation")
                            })
            )
    })
    @Bean
    public RouterFunction<ServerResponse> doctorsRouter(final DoctorsHandler doctorsHandler) {
        return RouterFunctions.route()
                .GET("/api/doctors/appointments", doctorsHandler::fetchAppointments)
                .PATCH("/api/doctors/unavailability", doctorsHandler::createUnavailability)
                .build();
    }
}
