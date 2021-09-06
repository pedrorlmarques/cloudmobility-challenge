package pt.cloudmobility.appointmentservice.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.cloudmobility.appointmentservice.dto.UserEvent;
import pt.cloudmobility.appointmentservice.service.ScheduleService;
import pt.cloudmobility.appointmentservice.utils.UserEventFunctionHelper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class UserEventConsumerConfiguration {

    @Bean
    Function<Flux<UserEvent>, Mono<Void>> onUserEvent(final ScheduleService scheduleService) {
        return UserEventFunctionHelper.createDoctorWeekSchedule(scheduleService);
    }
}
