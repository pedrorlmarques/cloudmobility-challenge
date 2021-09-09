package pt.cloudmobility.appointmentservice.error;

import com.github.pedrorlmarques.annotation.WithMockJwtToken;
import com.github.pedrorlmarques.annotation.WithMockJwtTokenClaim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pt.cloudmobility.appointmentservice.AppointmentServiceApplication;
import pt.cloudmobility.appointmentservice.KafkaContainerTestingSupport;
import pt.cloudmobility.appointmentservice.MongoDBContainerTestingSupport;
import pt.cloudmobility.appointmentservice.configuration.TestSecurityConfiguration;

@WithMockJwtToken(subject = "", authorities = {"ROLE_DOCTOR"},
        additionalClaims = {
                @WithMockJwtTokenClaim(name = "userId", value = "1")
        })
@AutoConfigureWebTestClient
@SpringBootTest(classes = {AppointmentServiceApplication.class, TestSecurityConfiguration.class})
class ExceptionAdviceITTest implements KafkaContainerTestingSupport, MongoDBContainerTestingSupport {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testMethodArgumentNotValid() {
        webTestClient
                .post()
                .uri("/api/exception-advice-test/method-argument")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo(ErrorConstants.ERR_VALIDATION)
                .jsonPath("$.fieldErrors.[0].objectName")
                .isEqualTo("test")
                .jsonPath("$.fieldErrors.[0].field")
                .isEqualTo("test")
                .jsonPath("$.fieldErrors.[0].message")
                .isEqualTo("NotNull");
    }

    @Test
    void testMissingRequestPart() {
        webTestClient
                .get()
                .uri("/api/exception-advice-test/missing-servlet-request-part")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("error.http.400");
    }

    @Test
    void testMissingRequestParameter() {
        webTestClient
                .get()
                .uri("/api/exception-advice-test/missing-servlet-request-parameter")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("error.http.400");
    }

    @Test
    void testAccessDenied() {
        webTestClient
                .get()
                .uri("/api/exception-advice-test/access-denied")
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("error.http.403")
                .jsonPath("$.detail")
                .isEqualTo("test access denied!");
    }

    @Test
    void testUnauthorized() {
        webTestClient
                .get()
                .uri("/api/exception-advice-test/unauthorized")
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("error.http.401")
                .jsonPath("$.path")
                .isEqualTo("/api/exception-advice-test/unauthorized")
                .jsonPath("$.detail")
                .isEqualTo("test authentication failed!");
    }

    @Test
    void testMethodNotSupported() {
        webTestClient
                .post()
                .uri("/api/exception-advice-test/access-denied")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("error.http.405")
                .jsonPath("$.detail")
                .isEqualTo("405 METHOD_NOT_ALLOWED \"Request method 'POST' not supported\"");
    }

    @Test
    void testExceptionWithResponseStatus() {
        webTestClient
                .get()
                .uri("/api/exception-advice-test/response-status")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("error.http.400")
                .jsonPath("$.title")
                .isEqualTo("test response status");
    }

    @Test
    void testInternalServerError() {
        webTestClient
                .get()
                .uri("/api/exception-advice-test/internal-server-error")
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("error.http.500")
                .jsonPath("$.title")
                .isEqualTo("Internal Server Error");
    }

}
