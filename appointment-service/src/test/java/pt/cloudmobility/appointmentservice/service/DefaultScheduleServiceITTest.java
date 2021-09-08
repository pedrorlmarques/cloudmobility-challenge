package pt.cloudmobility.appointmentservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cloudmobility.appointmentservice.AppointmentServiceApplication;
import pt.cloudmobility.appointmentservice.KafkaContainerTestingSupport;
import pt.cloudmobility.appointmentservice.MongoDBContainerTestingSupport;
import pt.cloudmobility.appointmentservice.configuration.TestSecurityConfiguration;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = {AppointmentServiceApplication.class, TestSecurityConfiguration.class})
class DefaultScheduleServiceITTest implements KafkaContainerTestingSupport, MongoDBContainerTestingSupport {

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

        Flux.just(new Slot(null, doctorId, null, SlotStatus.OPEN, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId, null, SlotStatus.OPEN, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId, null, SlotStatus.OPEN, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        Flux.just(new Slot(null, doctorId2, null, SlotStatus.OPEN, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId2, null, SlotStatus.OPEN, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId2, null, SlotStatus.OPEN, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId2, null, SlotStatus.OPEN, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
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

    @Test
    void testGivenSlotIdItShouldReserveForUserId() {

        var doctorId = 1;
        var userId = 1;
        var now = LocalDateTime.now();

        var slot = Mono.just(new Slot(null, doctorId, null, SlotStatus.OPEN, now, now.plusHours(1)))
                .flatMap(this.slotRepository::save)
                .block();

        //verify response
        StepVerifier.create(this.defaultScheduleService.reserveSlot(slot.getId(), userId))
                .expectSubscription()
                .verifyComplete();

        await("verify reserved slot on the database").untilAsserted(() -> {
            var reservedSlot = this.slotRepository.findById(slot.getId()).block();
            assertThat(reservedSlot).isNotNull();
            assertThat(reservedSlot.getUserId()).isNotNull().isEqualTo(userId);
            assertThat(reservedSlot.getStatus()).isNotNull().isEqualTo(SlotStatus.BOOKED);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNAVAILABLE", "BOOKED"})
    void testGivenAnUnavailableSlotItShouldSendError(String slotStatus) {

        var doctorId = 1;
        var userId = 1;
        var now = LocalDateTime.now();

        var slot = Mono.just(new Slot(null, doctorId, null, SlotStatus.valueOf(slotStatus), now, now.plusHours(1)))
                .flatMap(this.slotRepository::save)
                .block();

        //verify response
        StepVerifier.create(this.defaultScheduleService.reserveSlot(slot.getId(), userId))
                .expectSubscription()
                .expectErrorMessage("Slot is unavailable")
                .verify();

    }

    @Test
    void testGivenUnexistingSlotItShouldReceiveException() {

        //verify response
        StepVerifier.create(this.defaultScheduleService.reserveSlot("123", 1))
                .expectSubscription()
                .expectError(IllegalAccessException.class)
                .verify();

    }

    @Test
    void testGivenStartTimeAndEndTimeITShouldMarkSlotsAsUnavailable() {

        var doctorId = 1;
        var atNineAM = LocalDate.now().atTime(9, 0);

        Flux.just(new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        StepVerifier.create(this.defaultScheduleService.blockSlots(doctorId, atNineAM, atNineAM.plusHours(2)))
                .expectSubscription()
                .verifyComplete();

        await("verify that the block slots are blocked").untilAsserted(() -> {
            var block = this.slotRepository.findAllByDoctorIdAndStatusOrderByStartTimeAsc(doctorId, SlotStatus.UNAVAILABLE).collectList().block();
            assertThat(block).isNotNull().hasSize(3);
            assertThat(block).extracting(Slot::getStatus).allSatisfy(status -> assertThat(status).isEqualTo(SlotStatus.UNAVAILABLE));
            assertThat(block).extracting(Slot::getStartTime).isNotNull().containsExactlyInAnyOrder(atNineAM, atNineAM.plusHours(1), atNineAM.plusHours(2));
            assertThat(block).extracting(Slot::getEndTime).isNotNull().containsExactlyInAnyOrder(atNineAM.plusHours(1), atNineAM.plusHours(2), atNineAM.plusHours(3));
        });

        await("verify that the booked slots aren't changed").untilAsserted(() -> {
            var block = this.slotRepository.findAllByDoctorIdAndStatusOrderByStartTimeAsc(doctorId, SlotStatus.BOOKED).collectList().block();
            assertThat(block).isNotNull().hasSize(2);
            assertThat(block).extracting(Slot::getStatus).allSatisfy(status -> assertThat(status).isEqualTo(SlotStatus.BOOKED));
            assertThat(block).extracting(Slot::getStartTime).isNotNull().containsExactlyInAnyOrder(atNineAM.plusHours(3), atNineAM.plusHours(4));
            assertThat(block).extracting(Slot::getEndTime).isNotNull().containsExactlyInAnyOrder(atNineAM.plusHours(4), atNineAM.plusHours(5));
        });
    }
}
