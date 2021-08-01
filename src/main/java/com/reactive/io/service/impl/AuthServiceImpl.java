package com.reactive.io.service.impl;

import com.reactive.io.entity.dto.ResponseDto;
import com.reactive.io.entity.dto.UserDto;
import com.reactive.io.entity.mapper.UserMapper;
import com.reactive.io.errors.exception.UserAlreadyExistException;
import com.reactive.io.errors.exception.WrongCredentialException;
import com.reactive.io.respository.UserRepository;
import com.reactive.io.security.JWTUtil;
import com.reactive.io.service.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public Mono<UserDto> signup(UserDto userDto) {

        return isUserExist(userDto.getEmail())
                .filter(userExist -> !userExist)
                .switchIfEmpty(Mono.error(UserAlreadyExistException::new))
                .map(aBoolean -> userDto)
                .map(UserMapper.INSTANCE::fromDTO)
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(user.getPassword())))
                .flatMap(userRepository::save)
                .map(UserMapper.INSTANCE::toDTO);
    }

    @Override
    public Mono<ResponseDto> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(userDetails -> passwordEncoder.matches(password, userDetails.getPassword()))
                .map(userDetails -> jwtUtil.generateToken(userDetails))
                .map(token -> ResponseDto.builder().data(token).build())
                .switchIfEmpty(Mono.error(WrongCredentialException::new));
    }

    private Mono<Boolean> isUserExist(String email) {
        return userRepository.findByEmail(email)
                .map(user -> true)
                .switchIfEmpty(Mono.just(false));
    }
}
