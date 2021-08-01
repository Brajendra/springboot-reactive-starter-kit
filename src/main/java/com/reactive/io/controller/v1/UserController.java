package com.reactive.io.controller.v1;

import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Mono<UserDto> getUser(Principal principal) {
        return userService.getUser(principal.getName());
    }
}
