package com.reactive.io.service;

import com.reactive.io.entity.dto.ResponseDto;
import com.reactive.io.entity.dto.UserDto;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<UserDto> signup(UserDto userDto);

    Mono<ResponseDto> login(String email, String password);
}
