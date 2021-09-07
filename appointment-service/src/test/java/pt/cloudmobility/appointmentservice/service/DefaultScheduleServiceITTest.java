package pt.cloudmobility.appointmentservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cloudmobility.appointmentservice.AppointmentServiceApplication;
import pt.cloudmobility.appointmentservice.KafkaContainerTestingSupport;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AppointmentServiceApplication.class)
class DefaultScheduleServiceITTest implements KafkaContainerTestingSupport {

    @Autowired
    private DefaultScheduleService defaultScheduleService;

    @Autowired
    private SlotRepository slotRepository;

    @AfterEach
    void deleteDatabase() {
        this.slotRepository.deleteAll().block();
    }

    @Test
    void testGivenDoctorIdItShouldCreateTheWeekSchedule() {

        StepVerifier.create(
                        this.defaultScheduleService.createDefaultWeekScheduleFor(1))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(
                        this.slotRepository.findAll())
                .expectSubscription()
                .expectNextCount(50)
                .verifyComplete();
    }

    @Test
    void testGivenDoctorIdItShouldReturnTheOpenSlotsAssociated() {
        //setupData
        var doctorId = 1;

        Flux.just(new Slot(null, doctorId, null, SlotStatus.OPEN, LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
                        new Slot(null, doctorId, null, SlotStatus.OPEN, LocalDateTime.now(), LocalDateTime.now().plusHours(2)),
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, LocalDateTime.now(), LocalDateTime.now().plusHours(3)),
                        new Slot(null, doctorId, null, SlotStatus.UNAVAILABLE, LocalDateTime.now(), LocalDateTime.now().plusHours(4)),
                        new Slot(null, doctorId, null, SlotStatus.OPEN, LocalDateTime.now(), LocalDateTime.now().plusHours(5)))
                .flatMap(this.slotRepository::save)
                .blockLast();

        StepVerifier.create(this.defaultScheduleService.fetchDoctorAvailability(doctorId))
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testFetchAllDoctorsAvailability() {
        //setupData
        var doctorId = 1;
        var doctorId2 = 2;


        var atNineAM = LocalDate.now().atTime(9, 0);

        Flux.just(new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        Flux.just(new Slot(null, doctorId2, null, SlotStatus.BOOKED, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId2, null, SlotStatus.BOOKED, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId2, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId2, null, SlotStatus.BOOKED, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId2, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        var doctorsAvailability = this.defaultScheduleService.fetchDoctorsAvailability().collectList().block();

        assertThat(doctorsAvailability).isNotNull();

        assertThat(doctorsAvailability.stream().filter(slotDto -> slotDto.getDoctorId().equals(doctorId)).collect(Collectors.toList()))
                .hasSize(3);

        assertThat(doctorsAvailability.stream().filter(slotDto -> slotDto.getDoctorId().equals(doctorId2)).collect(Collectors.toList()))
                .hasSize(4);
    }

    @Test
    void testGivenStartTimeAndEndTimeITShouldReturnTheAppointmentsForThatPeriod() {

        var doctorId = 1;
        var atNineAM = LocalDate.now().atTime(9, 0);

        Flux.just(new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        var appointments = this.defaultScheduleService
                .fetchAppointments(doctorId, atNineAM,
                        atNineAM.plusHours(3)).collectList().block();

        //expecting slots from 9-12 and since the slot of 12-13 starts at 12 so we should include
        assertThat(appointments)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(slot -> {
                    assertThat(slot.getDoctorId()).isEqualTo(doctorId);
                    assertThat(slot.getStatus()).isEqualTo(SlotStatus.BOOKED);
                    assertThat(slot.getStartTime()).isAfterOrEqualTo(atNineAM);
                    assertThat(slot.getStartTime()).isBeforeOrEqualTo(atNineAM.plusHours(3));
                });
    }

}
