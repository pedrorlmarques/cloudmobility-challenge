package pt.cloudmobility.appointmentservice.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pt.cloudmobility.appointmentservice.dto.CreateUnavailabilityRequest;
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.security.SecurityUtils;
import pt.cloudmobility.appointmentservice.service.ScheduleService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class DefaultDoctorsHandler implements DoctorsHandler {

    private final ScheduleService scheduleService;

    public DefaultDoctorsHandler(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public Mono<ServerResponse> fetchAppointments(ServerRequest serverRequest) {
        return SecurityUtils
                .getUserId()
                .flatMap(userId -> ServerResponse.ok().body(BodyInserters
                                .fromProducer(this.scheduleService.fetchAppointments(Integer.valueOf(userId),
                                        serverRequest.queryParam("startDate")
                                                .map(LocalDateTime::parse)
                                                .orElseThrow(() -> new IllegalArgumentException("startDate mandatory")),
                                        serverRequest.queryParam("endDate")
                                                .map(LocalDateTime::parse)
                                                .orElseThrow(() -> new IllegalArgumentException("endDate mandatory"))), SlotDto.class)
                        )
                );
    }

    @Override
    public Mono<ServerResponse> createUnavailability(ServerRequest serverRequest) {
        return SecurityUtils
                .getUserId()
                .flatMap(userId -> serverRequest
                        .bodyToMono(CreateUnavailabilityRequest.class)
                        .flatMap(request -> this.scheduleService
                                .blockSlots(Integer.valueOf(userId),
                                        request.getStartDate(),
                                        request.getEndDate())
                        ).then(ServerResponse.noContent().build()));
    }
}
