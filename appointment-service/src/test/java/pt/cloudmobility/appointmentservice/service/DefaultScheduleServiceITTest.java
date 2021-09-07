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

import java.time.LocalDateTime;

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


}
