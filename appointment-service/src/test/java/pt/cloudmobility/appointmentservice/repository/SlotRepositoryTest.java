package pt.cloudmobility.appointmentservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@DataMongoTest
class SlotRepositoryTest {

    @Autowired
    private SlotRepository slotRepository;

    @Test
    void testAllBookedSlotsInBetween() {

        var doctorId = 1;
        var atNineAM = LocalDate.now().atTime(9, 0);

        Flux.just(new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        //expecting slots from 9-12 and since the slot of 12-13 starts at 12 so we should include
        StepVerifier.create(this.slotRepository
                        .findAllByDoctorIdAndStatusAndStartTimeIsBetween(atNineAM,
                                atNineAM.plusHours(3),
                                SlotStatus.BOOKED,
                                doctorId))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

}
