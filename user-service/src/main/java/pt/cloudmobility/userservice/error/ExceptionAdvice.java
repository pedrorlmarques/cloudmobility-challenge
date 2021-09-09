package pt.cloudmobility.userservice.error;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * Controller advice to translate exceptions
 * The error response follows RFC7807 - Problem Details for HTTP APIs
 * (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
public class ExceptionAdvice implements ProblemHandling {

    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final String VIOLATIONS_KEY = "violations";

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public Mono<ResponseEntity<Problem>> process(ResponseEntity<Problem> entity, ServerWebExchange request) {

        if (entity == null) {
            return Mono.empty();
        }
        var problem = entity.getBody();

        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return Mono.just(entity);
        }

        var builder = Problem
                .builder()
                .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ErrorConstants.DEFAULT_TYPE
                        : problem.getType())
                .withStatus(problem.getStatus()).withTitle(problem.getTitle())
                .with(PATH_KEY, request.getRequest().getPath().value());

        if (problem instanceof ConstraintViolationProblem) {
            builder.with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations()).with(MESSAGE_KEY,
                    ErrorConstants.ERR_VALIDATION);
        } else {
            builder.withCause(((DefaultProblem) problem).getCause()).withDetail(problem.getDetail())
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return Mono.just(new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode()));
    }

    @Override
    public Mono<ResponseEntity<Problem>> handleBindingResult(WebExchangeBindException ex, ServerWebExchange request) {
        var result = ex.getBindingResult();
        var fieldErrors = result.getFieldErrors().stream()
                .map(f -> new FieldError(f.getObjectName().replaceFirst("Dto$", ""), f.getField(), f.getCode()))
                .collect(Collectors.toList());

        var problem = Problem
                .builder()
                .withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
                .withTitle("Data binding and validation failure")
                .withStatus(Status.BAD_REQUEST)
                .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION)
                .with(FIELD_ERRORS_KEY, fieldErrors).build();

        return create(ex, problem, request);
    }

    @ExceptionHandler
    public Mono<ResponseEntity<Problem>> handleBadRequestException(BadRequestException ex, ServerWebExchange request) {
        return create(ex, request, HeaderUtil.createFailureAlert(applicationName, false, ex.getEntityName(),
                ex.getErrorKey(), ex.getMessage()));
    }

}
