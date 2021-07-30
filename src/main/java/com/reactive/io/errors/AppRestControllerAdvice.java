package com.reactive.io.errors;

import com.reactive.io.errors.exception.WrongCredentialException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.reactive.io.errors.Error.WRONG_CREDENTIALS;

@Log4j2
@RestControllerAdvice
public class AppRestControllerAdvice {

    @ExceptionHandler(value = WrongCredentialException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Error wrongCredentialException(final WrongCredentialException e) {
        return WRONG_CREDENTIALS;
    }
}
