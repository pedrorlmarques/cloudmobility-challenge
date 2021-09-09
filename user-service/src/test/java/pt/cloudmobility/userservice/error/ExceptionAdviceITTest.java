package pt.cloudmobility.userservice.error;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pt.cloudmobility.userservice.KafkaContainerTestingSupport;
import pt.cloudmobility.userservice.UserServiceApplication;

@AutoConfigureWebTestClient
@SpringBootTest(classes = UserServiceApplication.class)
class ExceptionAdviceITTest implements KafkaContainerTestingSupport {

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
