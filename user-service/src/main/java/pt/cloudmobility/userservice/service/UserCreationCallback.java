package pt.cloudmobility.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.EventType;
import pt.cloudmobility.userservice.dto.UserEvent;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@Component
public class UserCreationCallback implements Consumer<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserCreationCallback.class);
    private final Sinks.Many<UserEvent> userEventSink;

    public UserCreationCallback(Sinks.Many<UserEvent> userEventSink) {
        this.userEventSink = userEventSink;
    }

    @Override
    public void accept(User entity) {
        logger.info("Database request is pending transaction commit to broker: {}", entity);
        try {
            UserEvent event = new UserEvent(entity, EventType.USER_CREATED);
            // Set the entity payload after it has been updated in the database, but before being committed
            event.setSubject(entity);
            // Attempt to perform a reactive dual-write to message broker by sending a domain event
            this.userEventSink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
            // The application dual-write was a success and the database transaction can commit
        } catch (Exception ex) {
            logger.error("A dual-write transaction to the message broker has failed: {}", entity, ex);
            // This error will cause the database transaction to be rolled back
            throw new IllegalCallerException(ex.getMessage());
        }
        logger.info("Sent User Event");
    }
}
