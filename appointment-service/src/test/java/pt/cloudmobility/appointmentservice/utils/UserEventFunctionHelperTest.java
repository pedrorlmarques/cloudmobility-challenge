package pt.cloudmobility.appointmentservice.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.cloudmobility.appointmentservice.dto.EventType;
import pt.cloudmobility.appointmentservice.dto.InternalRole;
import pt.cloudmobility.appointmentservice.dto.UserDto;
import pt.cloudmobility.appointmentservice.dto.UserEvent;
import pt.cloudmobility.appointmentservice.service.ScheduleService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserEventFunctionHelperTest {

    @Mock
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDoctorWeekSchedule() {

        var userEvent = new UserEvent();
        userEvent.setId(UUID.randomUUID().hashCode());

        var userDto = new UserDto();
        userDto.setId(2);
        userDto.setFirstName("nam-1231e");
        userDto.setLastName("last");
        userDto.setIdentificationNumber("123");
        userDto.setRole(InternalRole.DOCTOR);
        userDto.setEmail("p@gmail.com");

        userEvent.setSubject(userDto);
        userEvent.setEventType(EventType.USER_CREATED);

        when(this.scheduleService.createDefaultWeekScheduleFor(userDto.getId())).thenReturn(Mono.empty());

        StepVerifier.create(UserEventFunctionHelper.createDoctorWeekSchedule(scheduleService).apply(Flux.just(userEvent)))
                .expectSubscription()
                .verifyComplete();

        verify(this.scheduleService, times(1)).createDefaultWeekScheduleFor(userDto.getId());

    }

    @Test
    void testGivenEmptyEventTypeItShouldFilterEvent() {

        var userEvent = new UserEvent();
        userEvent.setId(UUID.randomUUID().hashCode());

        var userDto = new UserDto();
        userDto.setId(2);
        userDto.setFirstName("nam-1231e");
        userDto.setLastName("last");
        userDto.setIdentificationNumber("123");
        userDto.setRole(InternalRole.PATIENT);
        userDto.setEmail("p@gmail.com");

        userEvent.setSubject(userDto);
        userEvent.setEventType(null);

        StepVerifier.create(UserEventFunctionHelper.createDoctorWeekSchedule(scheduleService).apply(Flux.just(userEvent)))
                .expectSubscription()
                .verifyComplete();

        verify(this.scheduleService, times(0)).createDefaultWeekScheduleFor(userDto.getId());
    }

    @Test
    void testGivenPatientItShouldFilterEvent() {

        var userEvent = new UserEvent();
        userEvent.setId(UUID.randomUUID().hashCode());

        var userDto = new UserDto();
        userDto.setId(2);
        userDto.setFirstName("nam-1231e");
        userDto.setLastName("last");
        userDto.setIdentificationNumber("123");
        userDto.setRole(InternalRole.PATIENT);
        userDto.setEmail("p@gmail.com");

        userEvent.setSubject(userDto);
        userEvent.setEventType(EventType.USER_CREATED);

        StepVerifier.create(UserEventFunctionHelper.createDoctorWeekSchedule(scheduleService).apply(Flux.just(userEvent)))
                .expectSubscription()
                .verifyComplete();

        verify(this.scheduleService, times(0)).createDefaultWeekScheduleFor(userDto.getId());
    }
}
