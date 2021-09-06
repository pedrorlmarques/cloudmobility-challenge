package pt.cloudmobility.appointmentservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "appointment")
public class AppointmentProperties {

    private Integer weekdays;
    private Integer startSlotHour;
    private Integer endSlotHour;

    public AppointmentProperties() {
    }

    public AppointmentProperties(Integer weekdays, Integer startSlotHour, Integer endSlotHour) {
        this.weekdays = weekdays;
        this.startSlotHour = startSlotHour;
        this.endSlotHour = endSlotHour;
    }

    public Integer getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(Integer weekdays) {
        this.weekdays = weekdays;
    }

    public Integer getStartSlotHour() {
        return startSlotHour;
    }

    public void setStartSlotHour(Integer startSlotHour) {
        this.startSlotHour = startSlotHour;
    }

    public Integer getEndSlotHour() {
        return endSlotHour;
    }

    public void setEndSlotHour(Integer endSlotHour) {
        this.endSlotHour = endSlotHour;
    }
}
