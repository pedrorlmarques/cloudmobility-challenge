package pt.cloudmobility.appointmentservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cloudmobility.appointmentservice.AppointmentServiceApplication;
import pt.cloudmobility.appointmentservice.KafkaContainerTestingSupport;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;
import reactor.test.StepVerifier;

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

}
