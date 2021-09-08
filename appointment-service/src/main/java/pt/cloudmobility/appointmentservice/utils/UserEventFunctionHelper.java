package pt.cloudmobility.appointmentservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.cloudmobility.appointmentservice.dto.EventType;
import pt.cloudmobility.appointmentservice.dto.InternalRole;
import pt.cloudmobility.appointmentservice.dto.UserEvent;
import pt.cloudmobility.appointmentservice.service.ScheduleService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

public final class UserEventFunctionHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserEventFunctionHelper.class);

    private UserEventFunctionHelper() {
        //private constructor
    }

    public static Function<Flux<UserEvent>, Mono<Void>> createDoctorWeekSchedule(final ScheduleService scheduleService) {
        return userEventFlux -> userEventFlux
                .filter(onlyUserCreationEvents())
                .filter(onlyDoctorAllowed())
                .doOnNext(userEvent -> logger.info("Received {}", userEvent))
                .flatMap(userEvent -> scheduleService
                        .createDefaultWeekScheduleFor(userEvent.getSubject().getId()))
                .then();
    }

    private static Predicate<UserEvent> onlyDoctorAllowed() {
        return userEvent -> userEvent.getSubject() != null &&
                userEvent.getSubject().getRole() != null &&
                userEvent.getSubject().getRole().equals(InternalRole.DOCTOR);
    }

    private static Predicate<UserEvent> onlyUserCreationEvents() {
        return userEvent -> userEvent.getEventType() != null &&
                userEvent.getEventType().equals(EventType.USER_CREATED);
    }
}
