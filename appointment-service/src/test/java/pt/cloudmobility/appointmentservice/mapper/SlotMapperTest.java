package pt.cloudmobility.appointmentservice.mapper;

import org.junit.jupiter.api.Test;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.domain.SlotStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SlotMapperTest {

    @Test
    void testGivenSlotItShouldConvertToSlotDto() {

        var slot = new Slot("1", 1, 1, SlotStatus.OPEN, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        var slotDto = SlotMapper.INSTANCE.convertTo(slot);
        assertThat(slotDto).isNotNull();
        assertThat(slotDto.getId()).isNotNull().isEqualTo(slot.getId());
        assertThat(slotDto.getDoctorId()).isNotNull().isEqualTo(slot.getDoctorId());
        assertThat(slotDto.getUserId()).isNotNull().isEqualTo(slot.getUserId());
        assertThat(slotDto.getStatus()).isNotNull().isEqualTo(slot.getStatus());
        assertThat(slotDto.getStartTime()).isNotNull().isEqualTo(slot.getStartTime());
        assertThat(slotDto.getEndTime()).isNotNull().isEqualTo(slot.getEndTime());
    }

}
