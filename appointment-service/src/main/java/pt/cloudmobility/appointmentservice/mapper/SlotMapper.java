package pt.cloudmobility.appointmentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pt.cloudmobility.appointmentservice.domain.Slot;
import pt.cloudmobility.appointmentservice.dto.SlotDto;

@Mapper
public interface SlotMapper {

    SlotMapper INSTANCE = Mappers.getMapper(SlotMapper.class);

    SlotDto convertTo(Slot slotDto);

}
