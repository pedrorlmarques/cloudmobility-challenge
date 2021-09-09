package pt.cloudmobility.appointmentservice.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.handler.PatientsHandler;

@Configuration(proxyBeanMethods = false)
public class PatientsRouterConfiguration {

    @RouterOperations({
            @RouterOperation(path = "/api/patients/doctors/availability",
                    beanClass = PatientsHandler.class, beanMethod = "fetchDoctorsAvailability",
                    operation =
                    @Operation(operationId = "fetchAppointments", summary = "fetchAppointments", tags = {"Patients API"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "successful operation",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SlotDto.class))))
                            })),
            @RouterOperation(path = "/api/patients/appointments/{appointmentId}",
                    beanClass = PatientsHandler.class, beanMethod = "createAppointment",
                    operation =
                    @Operation(operationId = "createAppointment", summary = "createAppointment", tags = {"Patients API"},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "appointmentId", description = "appointmentId", schema = @Schema(implementation = String.class))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "successful operation")
                            })
            )
    })
    @Bean
    public RouterFunction<ServerResponse> patientsRouter(final PatientsHandler patientsHandler) {
        return RouterFunctions.route()
                .GET("/api/patients/doctors/availability", patientsHandler::fetchDoctorsAvailability)
                .PATCH("/api/patients/appointments/{appointmentId}", patientsHandler::createAppointment)
                .build();
    }
}
