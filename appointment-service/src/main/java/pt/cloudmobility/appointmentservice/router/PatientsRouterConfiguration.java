package pt.cloudmobility.appointmentservice.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.appointmentservice.handler.PatientsHandler;

@Configuration(proxyBeanMethods = false)
public class PatientsRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> patientsRouter(final PatientsHandler patientsHandler) {
        return RouterFunctions.route()
                .GET("/api/patients/doctors/availability", patientsHandler::fetchDoctorsAvailability)
                .build();
    }
}
