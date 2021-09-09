package pt.cloudmobility.appointmentservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.cloudmobility.appointmentservice.configuration.AppointmentProperties;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.error.BadRequestException;
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
                        .findAllByDoctorIdAndStatusOrderByStartTimeAsc(id, SlotStatus.OPEN))
                .map(SlotMapper.INSTANCE::convertTo);
    }

    @Override
    public Flux<SlotDto> fetchDoctorsAvailability() {
        return this.slotRepository
                .findAllByStatusOrderByStartTimeAsc(SlotStatus.OPEN)
                .map(SlotMapper.INSTANCE::convertTo);
    }

    @Override
    public Flux<SlotDto> fetchAppointments(Integer doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        return Mono.justOrEmpty(doctorId)
                .flatMapMany(id -> this.slotRepository
                        .findAllByDoctorIdAndStatusAndStartTimeIsBetween(startDate, endDate, SlotStatus.BOOKED, id))
                .map(SlotMapper.INSTANCE::convertTo);
    }

    @Transactional
    @Override
    public Mono<Void> reserveSlot(String slotId, Integer userId) {
        return Mono.justOrEmpty(slotId)
                .flatMap(this.slotRepository::findById)
                .switchIfEmpty(Mono.error(new BadRequestException("Slot doesn't exist", DefaultScheduleService.class.getSimpleName(), "validation")))
                .flatMap(verifyIfSlotIsAvailable())
                .flatMap(slot -> updateSlot(userId, slot, SlotStatus.BOOKED))
                .then();
    }

    private Function<Slot, Mono<? extends Slot>> verifyIfSlotIsAvailable() {
        return slot -> {
            if (!slot.getStatus().equals(SlotStatus.OPEN)) {
                return Mono.error(new BadRequestException("Slot is unavailable", DefaultScheduleService.class.getSimpleName(), "validation"));
            } else {
                return Mono.justOrEmpty(slot);
            }
        };
    }

    @Transactional
    @Override
    public Mono<Void> blockSlots(Integer doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        return Mono.justOrEmpty(doctorId)
                .flatMapMany(id -> this.slotRepository.findAllByDoctorIdAndStartTimeIsBetween(startDate, endDate, doctorId))
                .flatMap(slot -> updateSlot(null, slot, SlotStatus.UNAVAILABLE))
                .then();
    }

    private Mono<Slot> updateSlot(Integer userId, Slot slot, SlotStatus booked) {
        slot.setUserId(userId);
        slot.setStatus(booked);
        return this.slotRepository.save(slot);
    }
}
