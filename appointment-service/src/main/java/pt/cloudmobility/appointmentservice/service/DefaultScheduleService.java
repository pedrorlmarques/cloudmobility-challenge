package pt.cloudmobility.appointmentservice.service;

import org.springframework.stereotype.Service;
import pt.cloudmobility.appointmentservice.configuration.AppointmentProperties;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;
import pt.cloudmobility.appointmentservice.utils.ScheduleUtils;
import reactor.core.publisher.Mono;

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
                .flatMapIterable(doctor -> ScheduleUtils
                        .createDefaultWeekScheduleFor(appointmentProperties.getWeekdays(),
                                appointmentProperties.getStartSlotHour(),
                                appointmentProperties.getEndSlotHour(), doctor))
                .flatMap(this.slotRepository::save)
                .then();
    }
}
