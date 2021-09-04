package pt.cloudmobility.userservice.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public abstract class DomainEvent<T, ID> implements Serializable {

    private ID id;

    private Long createdAt = OffsetDateTime.now().toEpochSecond();

    private Long lastModified = OffsetDateTime.now().toEpochSecond();

    protected DomainEvent() {
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
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

    public abstract T getSubject();

    public abstract void setSubject(T subject);

    public abstract EventType getEventType();

    public abstract void setEventType(EventType eventType);

    @Override
    public String toString() {
        return "DomainEvent{" + "id=" + id + ", createdAt=" + createdAt + ", lastModified=" + lastModified + '}';
    }

}
