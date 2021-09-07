package pt.cloudmobility.appointmentservice.service;

import org.springframework.stereotype.Service;
import pt.cloudmobility.appointmentservice.configuration.AppointmentProperties;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.mapper.SlotMapper;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;
import pt.cloudmobility.appointmentservice.utils.ScheduleUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.function.Function;

@Service
public class DefaultScheduleService implements ScheduleService {

    private final AppointmentProperties appointmentProperties;
    private final SlotRepository slotRepository;

    public DefaultScheduleService(AppointmentProperties appointmentProperties, SlotRepository slotRepository) {
        this.appointmentProperties = appointmentProperties;
        this.slotRepository = slotRepository;
    }

    @Override
    public Mono<Void> createDefaultWeekScheduleFor(Integer doctorId) {
        return Mono.justOrEmpty(doctorId)
                .publishOn(Schedulers.single())
                .flatMapIterable(doctor -> ScheduleUtils
                        .createDefaultWeekScheduleFor(appointmentProperties.getWeekdays(),
                                appointmentProperties.getStartSlotHour(),
                                appointmentProperties.getEndSlotHour(), doctor))
                .flatMap(this.slotRepository::save)
                .then();
    }

    @Override
    public Flux<SlotDto> fetchDoctorAvailability(Integer doctorId) {
        return Mono.justOrEmpty(doctorId)
                .flatMapMany(id -> this.slotRepository
                        .findAllByDoctorIdAndStatus(id, SlotStatus.OPEN))
                .map(SlotMapper.INSTANCE::convertTo);
    }

    @Override
    public Flux<SlotDto> fetchDoctorsAvailability() {
        return this.slotRepository
                .findAllByStatus(SlotStatus.OPEN)
                .map(SlotMapper.INSTANCE::convertTo);
    }

    @Override
    public Flux<SlotDto> fetchAppointments(Integer doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        return Mono.justOrEmpty(doctorId)
                .flatMapMany(id -> this.slotRepository
                        .findAllByDoctorIdAndStatusAndStartTimeIsBetween(startDate, endDate, SlotStatus.BOOKED, id))
                .map(SlotMapper.INSTANCE::convertTo);
    }

    @Override
    public Mono<SlotDto> reserveSlot(String slotId, Integer userId) {
        return Mono.justOrEmpty(slotId)
                .flatMap(this.slotRepository::findById)
                .switchIfEmpty(Mono.error(new IllegalAccessException("Slot doesn't exist")))
                .flatMap(reserveSlotFor(userId))
                .map(SlotMapper.INSTANCE::convertTo);
    }

    private Function<Slot, Mono<Slot>> reserveSlotFor(Integer userId) {
        return slot -> {
            slot.setUserId(userId);
            slot.setStatus(SlotStatus.BOOKED);
            return this.slotRepository.save(slot);
        };
    }
}
