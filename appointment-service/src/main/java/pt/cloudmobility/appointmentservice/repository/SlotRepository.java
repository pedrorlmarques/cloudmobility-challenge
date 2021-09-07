package pt.cloudmobility.appointmentservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import reactor.core.publisher.Flux;

public interface SlotRepository extends ReactiveMongoRepository<Slot, String> {

    Flux<Slot> findAllByDoctorId(Integer doctorId);

    Flux<Slot> findAllByDoctorIdAndStatus(Integer doctorId, SlotStatus status);
}
