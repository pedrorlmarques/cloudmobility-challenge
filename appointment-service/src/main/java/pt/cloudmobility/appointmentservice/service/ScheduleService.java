package pt.cloudmobility.appointmentservice.service;

import pt.cloudmobility.appointmentservice.dto.SlotDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ScheduleService {

    Mono<Void> createDefaultWeekScheduleFor(Integer doctorId);

    Flux<SlotDto> fetchDoctorAvailability(Integer doctorId);

    Flux<SlotDto> fetchDoctorsAvailability();

    Flux<SlotDto> fetchAppointments(Integer doctorId, LocalDateTime startDate, LocalDateTime endDate);

    Mono<SlotDto> reserveSlot(String slotId, Integer userId);
}
