package com.reactive.io.controller.v1;

import com.reactive.io.entity.dto.ResponseDto;
import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.service.AuthService;
import com.reactive.io.util.ApiResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("v1/signup")
    public Mono<UserDto> signup(@RequestBody UserDto userDto) {
        return authService.signup(userDto);
    }

    @GetMapping("v1/login")
    public Mono<ResponseDto> signup(@RequestHeader("username") String username, @RequestHeader("password") String password) {
        return authService.login(username, password)
                .map(ApiResponseHelper::getResponse);
    }
}
