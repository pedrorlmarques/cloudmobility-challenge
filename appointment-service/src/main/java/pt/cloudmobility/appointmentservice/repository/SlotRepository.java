package pt.cloudmobility.appointmentservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pt.cloudmobility.appointmentservice.domain.Slot;

public interface SlotRepository extends ReactiveMongoRepository<Slot, String> {
}
