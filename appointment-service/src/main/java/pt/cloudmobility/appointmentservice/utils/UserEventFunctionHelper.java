package pt.cloudmobility.appointmentservice.utils;

import pt.cloudmobility.appointmentservice.dto.EventType;
import pt.cloudmobility.appointmentservice.dto.InternalRole;
import pt.cloudmobility.appointmentservice.dto.UserEvent;
import pt.cloudmobility.appointmentservice.service.ScheduleService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

public final class UserEventFunctionHelper {

    private UserEventFunctionHelper() {
        //private constructor
    }

    public static Function<Flux<UserEvent>, Mono<Void>> createDoctorWeekSchedule(ScheduleService scheduleService) {
        return userEventFlux -> userEventFlux
                .filter(onlyUserCreationEvents())
                .filter(onlyDoctorAllowed())
                .flatMap(userEvent -> scheduleService.createDefaultWeekScheduleFor(userEvent.getSubject().getId()))
                .then();
    }

    private static Predicate<UserEvent> onlyDoctorAllowed() {
        return userEvent -> userEvent.getSubject().getRole().equals(InternalRole.DOCTOR);
    }

    private static Predicate<UserEvent> onlyUserCreationEvents() {
        return userEvent -> userEvent.getEventType().equals(EventType.USER_CREATED);
    }
}
