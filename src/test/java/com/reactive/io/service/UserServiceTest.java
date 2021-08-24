package com.reactive.io.service;

import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.entity.model.User;
import com.reactive.io.errors.exception.UserNotFoundException;
import com.reactive.io.respository.UserRepository;
import com.reactive.io.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    private String TEST_EMAIL = "test@gmail.com";

    @Test
    public void shouldGetUser() {
        User user = User.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("password")
                .active(false)
                .build();
        when(userRepository.findUserByEmail(TEST_EMAIL)).thenReturn(Mono.just(user));
        Mono<UserDto> userMono = userService.getUser(TEST_EMAIL);
        StepVerifier
                .create(userMono)
                .consumeNextWith(newUser -> {
                    assertEquals(newUser.getEmail(), TEST_EMAIL);
                })
                .verifyComplete();
    }

    @Test
    public void shouldGetUserNotFound() {
        when(userRepository.findUserByEmail(TEST_EMAIL)).thenReturn(Mono.empty());
        Mono<UserDto> userMono = userService.getUser(TEST_EMAIL);
        StepVerifier
                .create(userMono)
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException)
                .verify();
    }
}