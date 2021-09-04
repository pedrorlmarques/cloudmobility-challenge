package pt.cloudmobility.userservice.dto;

import pt.cloudmobility.userservice.domain.User;

import java.util.UUID;

public class UserEvent extends DomainEvent<User, Integer> {

    private EventType eventType;
    private User subject;

    public UserEvent() {
        this.setId(UUID.randomUUID().hashCode());
    }

    public UserEvent(User subject, EventType eventType) {
        this();
        this.subject = subject;
        this.eventType = eventType;
    }

    public UserEvent(EventType userCreated) {
        this.eventType = userCreated;
    }

    @Override
    public User getSubject() {
        return this.subject;
    }

    @Override
    public void setSubject(User subject) {
        this.subject = subject;
    }

    @Override
    public EventType getEventType() {
        return this.eventType;
    }

    @Override
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "eventType=" + eventType +
                ", subject=" + subject +
                "} " + super.toString();
    }
}
