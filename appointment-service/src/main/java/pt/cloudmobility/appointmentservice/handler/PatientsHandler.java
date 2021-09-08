package pt.cloudmobility.appointmentservice.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface PatientsHandler {

    Mono<ServerResponse> fetchDoctorsAvailability(ServerRequest serverRequest);

}
