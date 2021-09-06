package pt.cloudmobility.appointmentservice.utils;

import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ScheduleUtils {

    private ScheduleUtils() {
        //private constructor
    }

    public static List<Slot> createDefaultWeekScheduleFor(int days, int startSlotHour, int endSlotHour, int doctorId) {

        var slotStartTime = LocalDate.now().atTime(startSlotHour, 0);
        var slotEndTime = LocalDate.now().atTime(endSlotHour, 0);

        var currentSlotTime = slotStartTime;

        var weekSchedule = new ArrayList<Slot>();

        while (days != 0) {

            //skip weekend
            while (currentSlotTime.getDayOfWeek() == DayOfWeek.SATURDAY || currentSlotTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
                currentSlotTime = currentSlotTime.plusDays(1);
                slotEndTime = slotEndTime.plusDays(1);
            }

            var daySlotOf = createDaySlotOf(currentSlotTime, slotEndTime, Duration.ofHours(1), doctorId);

            weekSchedule.addAll(daySlotOf);

            currentSlotTime = currentSlotTime.plusDays(1);
            slotEndTime = slotEndTime.plusDays(1);

            days--;
        }

        return weekSchedule;
    }

    public static List<Slot> createDaySlotOf(LocalDateTime slotStartTime, LocalDateTime slotEndTime, Duration slotDuration, Integer doctorId) {

        var slots = new ArrayList<Slot>();

        while (!slotStartTime.equals(slotEndTime)) {
            var slot = new Slot();
            slot.setStatus(SlotStatus.OPEN);
            slot.setDoctorId(doctorId);
            slot.setStartTime(slotStartTime);
            slotStartTime = slotStartTime.plus(slotDuration);
            slot.setEndTime(slotStartTime);
            slots.add(slot);
        }

        return slots;
    }
}
