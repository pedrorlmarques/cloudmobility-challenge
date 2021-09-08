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
import pt.cloudmobility.appointmentservice.dto.SlotDto;
import pt.cloudmobility.appointmentservice.repository.SlotRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WithMockJwtToken(subject = "", authorities = {"ROLE_USER"},
        additionalClaims = {
                @WithMockJwtTokenClaim(name = "userId", value = "2")
        })
@AutoConfigureWebTestClient
@SpringBootTest(classes = {AppointmentServiceApplication.class, TestSecurityConfiguration.class})
class PatientsRouterConfigurationITTest implements MongoDBContainerTestingSupport, KafkaContainerTestingSupport {


    public static final String API_PATIENTS = "/api/patients";

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
    void testFetchAllDoctorsOpenAppointments() throws Exception {

        var doctorId = 1;
        var doctorId2 = 2;
        var atNineAM = LocalDate.now().atTime(9, 0);

        //setup Data for both doctors
        Flux.just(doctorId, doctorId2)
                .flatMap(id -> Flux.just(new Slot(null, id, null, SlotStatus.OPEN, atNineAM, atNineAM.plusHours(1)), // 9 - 10
                                new Slot(null, id, null, SlotStatus.BOOKED, atNineAM.plusHours(1), atNineAM.plusHours(2)), // 10 - 11
                                new Slot(null, id, null, SlotStatus.BOOKED, atNineAM.plusHours(2), atNineAM.plusHours(3)),  // 11 - 12
                                new Slot(null, id, null, SlotStatus.OPEN, atNineAM.plusHours(3), atNineAM.plusHours(4)), // 12 - 13
                                new Slot(null, id, null, SlotStatus.BOOKED, atNineAM.plusHours(4), atNineAM.plusHours(5))) // 13 - 14
                        .concatMap(this.slotRepository::save))
                .blockLast();

        var responseBody = this.webTestClient
                .get()
                .uri(API_PATIENTS + "/doctors/availability")
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

        assertThat(slots).isNotNull().hasSize(4);
        assertThat(slots).extracting(SlotDto::getStatus).containsOnly(SlotStatus.OPEN);
        assertThat(slots).extracting(SlotDto::getDoctorId).containsOnly(doctorId, doctorId2);
        assertThat(slots).extracting(SlotDto::getStartTime).containsOnly(atNineAM, atNineAM.plusHours(3));
        assertThat(slots).extracting(SlotDto::getEndTime).containsOnly(atNineAM.plusHours(1), atNineAM.plusHours(4));

    }

}
