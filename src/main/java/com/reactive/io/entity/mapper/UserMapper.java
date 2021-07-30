package com.reactive.io.entity.mapper;

import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.entity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.HashSet;

@Mapper(imports = {HashSet.class, Collections.class, SimpleGrantedAuthority.class,UserRoles.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDTO(final User user);

    User fromDTO(final UserDto userDto);
}
