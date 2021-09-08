package pt.cloudmobility.appointmentservice.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface SlotRepository extends ReactiveMongoRepository<Slot, String> {

    Flux<Slot> findAllByDoctorId(Integer doctorId);

    Flux<Slot> findAllByDoctorIdAndStatusOrderByStartTimeAsc(Integer doctorId, SlotStatus status);

    Flux<Slot> findAllByStatusOrderByStartTimeAsc(SlotStatus status);

    @Query(value = "{'startTime' : { $gte: ?0, $lte: ?1 }, 'status' : ?2, 'doctorId' : ?3 }")
    Flux<Slot> findAllByDoctorIdAndStatusAndStartTimeIsBetween(LocalDateTime startTime, LocalDateTime endTime, SlotStatus slotStatus, Integer doctorId);

    @Query(value = "{'startTime' : { $gte: ?0, $lte: ?1 }, 'doctorId' : ?2 }")
    Flux<Slot> findAllByDoctorIdAndStartTimeIsBetween(LocalDateTime startTime, LocalDateTime endTime, Integer doctorId);

}
