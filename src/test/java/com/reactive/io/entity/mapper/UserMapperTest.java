package com.reactive.io.entity.mapper;

import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.entity.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @Test
    public void shouldReturnToDTO() {
        User user = User.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email("test@gmail.com")
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        UserDto userDto = UserMapper.INSTANCE.toDTO(user);

        assertTrue(userDto != null);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void shouldReturnToDTONull() {

        UserDto userDto = UserMapper.INSTANCE.toDTO(null);
        assertTrue(userDto == null);
    }

    @Test
    public void shouldReturnFromDTO() {
        UserDto userDto = UserDto.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email("test@gmail.com")
                .role(UserRoles.ADMIN)
                .password("password")
                .build();
        User user = UserMapper.INSTANCE.fromDTO(userDto);

        assertTrue(userDto != null);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void shouldReturnFromDTONull() {

        User user = UserMapper.INSTANCE.fromDTO(null);
        assertTrue(user == null);
    }
}