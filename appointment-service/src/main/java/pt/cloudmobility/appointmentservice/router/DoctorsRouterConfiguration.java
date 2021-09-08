package pt.cloudmobility.appointmentservice.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.appointmentservice.handler.DoctorsHandler;

@Configuration(proxyBeanMethods = false)
public class DoctorsRouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> doctorsRouter(final DoctorsHandler doctorsHandler) {
        return RouterFunctions.route()
                .GET("/api/doctors/appointments", doctorsHandler::fetchAppointments)
                .PATCH("/api/doctors/unavailability", doctorsHandler::createUnavailability)
                .build();
    }
}
