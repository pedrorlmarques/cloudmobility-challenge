package pt.cloudmobility.appointmentservice.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class CreateUnavailabilityRequest {

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public CreateUnavailabilityRequest(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateUnavailabilityRequest that = (CreateUnavailabilityRequest) o;
        return Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return "CreateUnavailabilityRequest{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
