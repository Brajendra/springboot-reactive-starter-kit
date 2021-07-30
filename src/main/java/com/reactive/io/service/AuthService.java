package com.reactive.io.service;

import com.reactive.io.entity.dto.UserDto;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<UserDto> signup(UserDto userDto);

    Mono<String> login(String username, String password);
}
