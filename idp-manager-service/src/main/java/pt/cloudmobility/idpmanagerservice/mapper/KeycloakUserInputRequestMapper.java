package pt.cloudmobility.idpmanagerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pt.cloudmobility.idpmanagerservice.dto.KeycloakUserInputRequest;
import pt.cloudmobility.idpmanagerservice.dto.UserEvent;

@Mapper
public interface KeycloakUserInputRequestMapper {

    KeycloakUserInputRequestMapper INSTANCE = Mappers.getMapper(KeycloakUserInputRequestMapper.class);

    @Mapping(source = "subject.id", target = "userId")
    @Mapping(source = "subject.firstName", target = "firstName")
    @Mapping(source = "subject.lastName", target = "lastName")
    @Mapping(source = "subject.email", target = "email")
    @Mapping(source = "subject.role", target = "role")
    KeycloakUserInputRequest convertTo(UserEvent userEvent);
}
