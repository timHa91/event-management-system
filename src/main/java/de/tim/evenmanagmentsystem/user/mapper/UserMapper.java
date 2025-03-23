package de.tim.evenmanagmentsystem.user.mapper;

import de.tim.evenmanagmentsystem.user.dto.UserDTO;
import de.tim.evenmanagmentsystem.user.dto.UserRegistrationDTO;
import de.tim.evenmanagmentsystem.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
//
//    UserDTO toDTO(User user);
//
//    @Mapping(target = "password", ignore = true)
//    User toEntity(UserRegistrationDTO dto);
}
