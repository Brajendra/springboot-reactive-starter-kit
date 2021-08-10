package com.reactive.io.controller.v1;

import com.reactive.io.entity.dto.ResponseDto;
import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.errors.exception.UserAlreadyExistException;
import com.reactive.io.errors.exception.WrongCredentialException;
import com.reactive.io.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.reactive.io.errors.Error.USER_ALREADY_EXIST;
import static com.reactive.io.errors.Error.WRONG_CREDENTIALS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AuthController.class, excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
public class AuthControllerTest {

    private final String TEST_EMAIL = "test@gmail.com";

    @Autowired
    private WebTestClient webClient;

    @MockBean
    AuthService authService;

    @Test
    public void shouldSignUp() {

        UserDto userDto = UserDto.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .password("password")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .build();

        when(authService.signup(any(UserDto.class))).thenReturn(Mono.just(userDto));

        webClient
                .post().uri("/auth/v1/signup")
                .bodyValue(userDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserDto.class);
    }

    @Test
    public void shouldSignUpFail() {

        UserDto userDto = UserDto.builder()
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .password("password")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .build();

        when(authService.signup(any(UserDto.class))).thenThrow(new UserAlreadyExistException());

        webClient
                .post().uri("/auth/v1/signup")
                .bodyValue(userDto)
                .exchange()
                .expectStatus()
                .isEqualTo(NOT_ACCEPTABLE)
                .expectBody()
                .jsonPath("code").isEqualTo(USER_ALREADY_EXIST.getCode());
    }

    @Test
    public void shouldLogin() {
        ResponseDto responseDto = ResponseDto.builder()
                .data("token")
                .build();

        when(authService.login(anyString(), anyString())).thenReturn(Mono.just(responseDto));

        webClient
                .get().uri("/auth/v1/login")
                .header("email", "email")
                .header("password", "password")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.data").isNotEmpty();
    }

    @Test
    public void shouldLoginFail() {

        when(authService.login(anyString(), anyString())).thenThrow(new WrongCredentialException());

        webClient
                .get().uri("/auth/v1/login")
                .header("email", "email")
                .header("password", "password")
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("code").isEqualTo(WRONG_CREDENTIALS.getCode());
    }
}