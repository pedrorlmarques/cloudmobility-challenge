package pt.cloudmobility.appointmentservice.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.security.SecurityUtils;
import pt.cloudmobility.appointmentservice.service.ScheduleService;
import reactor.core.publisher.Mono;

@Component
public class DefaultPatientsHandler implements PatientsHandler {

    private final ScheduleService scheduleService;

    public DefaultPatientsHandler(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public Mono<ServerResponse> fetchDoctorsAvailability(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(BodyInserters
                        .fromProducer(this.scheduleService.fetchDoctorsAvailability(), SlotDto.class));
    }

    @Override
    public Mono<ServerResponse> createAppointment(ServerRequest serverRequest) {
        return SecurityUtils
                .getUserId()
                .flatMap(userId -> this.scheduleService.reserveSlot(serverRequest
                        .pathVariable("appointmentId"), Integer.valueOf(userId)))
                .then(ServerResponse.noContent().build());
    }
}
