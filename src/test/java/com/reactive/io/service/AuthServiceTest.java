package com.reactive.io.service;

import com.reactive.io.entity.dto.ResponseDto;
import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.entity.model.User;
import com.reactive.io.errors.exception.UserAlreadyExistException;
import com.reactive.io.errors.exception.WrongCredentialException;
import com.reactive.io.respository.UserRepository;
import com.reactive.io.security.JWTUtil;
import com.reactive.io.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtil jwtUtil;

    private final String TEST_EMAIL = "test@gmail.com";

    @Test
    public void shouldSignup() {

        UserDto userDto = UserDto.builder()
                .email(TEST_EMAIL)
                .phone("phone")
                .role(UserRoles.ADMIN)
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .build();

        User user = User.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("encodedPassword")
                .active(false)
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        Mono<UserDto> monoResult = authService.signup(userDto);

        StepVerifier
                .create(monoResult)
                .consumeNextWith(newUser -> {
                    assertNull(newUser.getPassword());
                    assertNotNull(newUser.getId());
                    assertEquals(newUser.getEmail(), TEST_EMAIL);
                })
                .verifyComplete();
    }

    @Test
    public void shouldSignupForExistingMemberFail() {

        UserDto userDto = UserDto.builder()
                .email(anyString())
                .role(UserRoles.ADMIN)
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .build();

        User user = User.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("encodedPassword")
                .active(false)
                .build();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Mono.just(user));

        Mono<UserDto> monoResult = authService.signup(userDto);

        StepVerifier
                .create(monoResult)
                .expectErrorMatches(throwable -> throwable instanceof UserAlreadyExistException)
                .verify();
    }

    @Test
    public void shouldLogin() {

        User user = User.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("encodedPassword")
                .active(false)
                .build();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(anyString());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Mono.just(user));

        Mono<ResponseDto> monoResult = authService.login(TEST_EMAIL, anyString());

        StepVerifier
                .create(monoResult)
                .consumeNextWith(responseDto -> {
                    assertNotNull(responseDto.getData());
                })
                .verifyComplete();
    }

    @Test
    public void shouldLoginFailUserNotFound() {

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Mono.empty());

        Mono<ResponseDto> monoResult = authService.login(TEST_EMAIL, anyString());

        StepVerifier
                .create(monoResult)
                .expectErrorMatches(throwable -> throwable instanceof WrongCredentialException)
                .verify();
    }

    @Test
    public void shouldLoginFailPasswordIncorrect() {

        User user = User.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .password("encodedPassword")
                .active(false)
                .build();

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Mono.just(user));

        Mono<ResponseDto> monoResult = authService.login(TEST_EMAIL, anyString());

        StepVerifier
                .create(monoResult)
                .expectErrorMatches(throwable -> throwable instanceof WrongCredentialException)
                .verify();
    }
}