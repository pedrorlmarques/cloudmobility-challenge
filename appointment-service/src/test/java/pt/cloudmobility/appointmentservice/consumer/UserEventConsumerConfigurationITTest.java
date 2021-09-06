package pt.cloudmobility.appointmentservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import pt.cloudmobility.appointmentservice.AppointmentServiceApplication;
import pt.cloudmobility.appointmentservice.KafkaContainerTestingSupport;
import pt.cloudmobility.appointmentservice.dto.EventType;
import pt.cloudmobility.appointmentservice.dto.InternalRole;
import pt.cloudmobility.appointmentservice.dto.UserDto;
import pt.cloudmobility.appointmentservice.dto.UserEvent;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = AppointmentServiceApplication.class)
class UserEventConsumerConfigurationITTest implements KafkaContainerTestingSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SlotRepository slotRepository;

    @Value("${spring.cloud.stream.bindings.onUserEvent-in-0.destination}")
    private String userEventsTopic;

    // By default each time the producer sends a message to non-existing topic kafka will create.
    @AfterEach
    public void teardownKafka() {
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()))) {
            admin.deleteTopics(List.of(this.userEventsTopic));
        }
    }

    @AfterEach
    public void deleteDatabase() {
        this.slotRepository.deleteAll().block();
    }

    @Test
    void testGivenUserEventItShouldCreateDefaultWeekSchedule() throws Exception {

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

        try (var userEventsProducer = createProducer()) {
            final var message = this.objectMapper.writeValueAsString(userEvent);
            final var record =
                    new ProducerRecord<String, Object>(this.userEventsTopic, userEvent.getId().toString(), message);
            userEventsProducer.send(record).get();
        }

        await().untilAsserted(() -> {
            var doctorSchedule = this.slotRepository.findAllByDoctorId(userDto.getId()).collectList().block();
            assertThat(doctorSchedule).hasSize(50);
            assertThat(doctorSchedule).allSatisfy(slot -> assertThat(slot.getDoctorId()).isEqualTo(userDto.getId()));
        });

    }
}
