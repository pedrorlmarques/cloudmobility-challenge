package pt.cloudmobility.appointmentservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document
public class Slot {

    @Id
    private String id;
    private Integer doctorId;
    private Integer userId;
    private SlotStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Slot() {
    }

    public Slot(String id, Integer doctorId, Integer userId, SlotStatus status, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.doctorId = doctorId;
        this.userId = userId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return Objects.equals(id, slot.id) && Objects.equals(doctorId, slot.doctorId) && Objects.equals(userId, slot.userId) && Objects.equals(status, slot.status) && Objects.equals(startTime, slot.startTime) && Objects.equals(endTime, slot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, doctorId, userId, status, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Slot{" +
                "id='" + id + '\'' +
                ", doctorId=" + doctorId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
