package pt.cloudmobility.appointmentservice.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleUtilsTest {

    @Test
    void testGiven10HoursScheduleWith1HourSlotItShouldCreate10Slots() {

        var slotStartTime = LocalDate.now().atTime(9, 0);
        var slotEndTime = LocalDate.now().atTime(19, 0);
        var slotDuration = Duration.ofHours(1);

        var daySlot = ScheduleUtils.createDaySlotOf(slotStartTime, slotEndTime, slotDuration, 1);

        //assert the number of slots depending on the slot duration
        assertThat(daySlot).hasSize(slotEndTime.getHour() - slotStartTime.getHour() / slotDuration.toHoursPart());
    }

    @Test
    void testGiven5weekDaysItShouldAndWorkTime9to19ItShouldCreate50Slots() {

        var startSlotHour = 9;
        var endSlotHour = 19;
        var weekDays = 5;
        var defaultWeekScheduleFor = ScheduleUtils.createDefaultWeekScheduleFor(weekDays, startSlotHour, endSlotHour, 1);

        assertThat(defaultWeekScheduleFor).hasSize((endSlotHour - startSlotHour) * weekDays);
    }

}
