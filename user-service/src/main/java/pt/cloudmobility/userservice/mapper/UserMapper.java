package pt.cloudmobility.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pt.cloudmobility.userservice.domain.User;
import pt.cloudmobility.userservice.dto.UserDto;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User convertTo(UserDto userDto);

    UserDto convertTo(User user);

}
