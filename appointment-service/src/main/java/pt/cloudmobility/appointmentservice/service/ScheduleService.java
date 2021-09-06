package pt.cloudmobility.appointmentservice.service;

import reactor.core.publisher.Mono;

public interface ScheduleService {

    Mono<Void> createDefaultWeekScheduleFor(Integer doctorId);
}
