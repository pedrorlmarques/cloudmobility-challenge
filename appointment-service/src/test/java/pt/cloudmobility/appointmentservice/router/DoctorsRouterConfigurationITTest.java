package pt.cloudmobility.appointmentservice.router;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pedrorlmarques.annotation.WithMockJwtToken;
import com.github.pedrorlmarques.annotation.WithMockJwtTokenClaim;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pt.cloudmobility.appointmentservice.AppointmentServiceApplication;
import pt.cloudmobility.appointmentservice.KafkaContainerTestingSupport;
import pt.cloudmobility.appointmentservice.MongoDBContainerTestingSupport;
import pt.cloudmobility.appointmentservice.configuration.TestSecurityConfiguration;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;
import pt.cloudmobility.appointmentservice.dto.CreateUnavailabilityRequest;
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@WithMockJwtToken(subject = "", authorities = {"ROLE_DOCTOR"},
        additionalClaims = {
                @WithMockJwtTokenClaim(name = "userId", value = "1")
        })
@AutoConfigureWebTestClient
@SpringBootTest(classes = {AppointmentServiceApplication.class, TestSecurityConfiguration.class})
class DoctorsRouterConfigurationITTest implements MongoDBContainerTestingSupport, KafkaContainerTestingSupport {

    public static final String API_DOCTOS = "/api/doctors";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void deleteDatabase() {
        this.slotRepository.deleteAll().block();
    }

    @Test
    void testGivenCreateUnavailabilityRequestItShouldBlockTheSlotsForThatSpecificPeriod() {

        var doctorId = 1;
        var atNineAM = LocalDate.now().atTime(9, 0);

        Flux.just(new Slot(null, doctorId, null, SlotStatus.OPEN, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        this.webTestClient
                .patch()
                .uri(API_DOCTOS + "/unavailability")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new CreateUnavailabilityRequest(atNineAM, atNineAM.plusHours(5))), CreateUnavailabilityRequest.class)
                .exchange()
                .expectStatus()
                .isNoContent();

        await().untilAsserted(() -> {
            var allSlots = this.slotRepository.findAllByDoctorId(doctorId).collectList().block();
            assertThat(allSlots).hasSize(5);
            assertThat(allSlots).extracting(Slot::getStatus).containsOnly(SlotStatus.UNAVAILABLE);
        });
    }

    @Test
    void testGivenDoctorIdItShouldReturnTheBookedAppointmentForThatSpecificPeriod() throws Exception {

        var doctorId = 1;
        var atNineAM = LocalDate.now().atTime(9, 0);

        Flux.just(new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                        new Slot(null, doctorId, null, SlotStatus.OPEN, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                        new Slot(null, doctorId, null, SlotStatus.OPEN, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                        new Slot(null, doctorId, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                .concatMap(this.slotRepository::save)
                .blockLast();

        var responseBody = this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_DOCTOS + "/appointments")
                        .queryParam("startDate", "{startTime}")
                        .queryParam("endDate", "{endTime}")
                        .build(atNineAM, atNineAM.plusHours(2)))
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .returnResult()
                .getResponseBody();

        assertThat(responseBody).isNotEmpty();

        var slots = this.objectMapper.readValue(responseBody, new TypeReference<List<SlotDto>>() {
        });

        assertThat(slots).isNotNull().hasSize(2);
        assertThat(slots).extracting(SlotDto::getStatus).containsOnly(SlotStatus.BOOKED);
        assertThat(slots).extracting(SlotDto::getDoctorId).containsOnly(doctorId);
        assertThat(slots).extracting(SlotDto::getStartTime).containsExactlyInAnyOrder(atNineAM, atNineAM.plusHours(2));
        assertThat(slots).extracting(SlotDto::getEndTime).containsExactlyInAnyOrder(atNineAM.plusHours(1), atNineAM.plusHours(3));
    }

}
