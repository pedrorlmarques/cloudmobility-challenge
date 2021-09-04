package pt.cloudmobility.idpmanagerservice.dto;

import java.util.Objects;

public class UserEvent {

    private Integer id;
    private Long createdAt;
    private Long lastModified;
    private EventType eventType;
    private UserDto subject;

    public UserEvent() {
    }

    public UserEvent(Integer id, Long createdAt, Long lastModified, EventType eventType, UserDto subject) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.eventType = eventType;
        this.subject = subject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public UserDto getSubject() {
        return subject;
    }

    public void setSubject(UserDto subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEvent userEvent = (UserEvent) o;
        return Objects.equals(id, userEvent.id) && Objects.equals(createdAt, userEvent.createdAt) && Objects.equals(lastModified, userEvent.lastModified) && eventType == userEvent.eventType && Objects.equals(subject, userEvent.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, lastModified, eventType, subject);
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                ", eventType=" + eventType +
                ", subject=" + subject +
                '}';
    }
}
