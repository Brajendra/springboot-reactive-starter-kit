package com.reactive.io.controller.v1;

import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.enums.UserRoles;
import com.reactive.io.errors.exception.UserNotFoundException;
import com.reactive.io.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.reactive.io.errors.Error.USER_NOT_FOUND;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(UserController.class)
public class UserControllerTest {

    private final String TEST_EMAIL = "test@gmail.com";

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = TEST_EMAIL)
    public void shouldGetUser() {

        UserDto user = UserDto.builder()
                .id("id")
                .firstName("firstName")
                .lastName("lastName")
                .phone("phone")
                .email(TEST_EMAIL)
                .role(UserRoles.ADMIN)
                .build();

        when(userService.getUser(TEST_EMAIL)).thenReturn(Mono.just(user));

        webClient
                .get().uri("/user")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserDto.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    public void shouldGetUserFail() {

        when(userService.getUser(TEST_EMAIL)).thenThrow(new UserNotFoundException());

        webClient
                .get().uri("/user")
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody()
                .jsonPath("code").isEqualTo(USER_NOT_FOUND.getCode());
    }
}