package pt.cloudmobility.appointmentservice.service;

import pt.cloudmobility.appointmentservice.dto.SlotDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScheduleService {

    Mono<Void> createDefaultWeekScheduleFor(Integer doctorId);

    Flux<SlotDto> fetchDoctorAvailability(Integer doctorId);
}
