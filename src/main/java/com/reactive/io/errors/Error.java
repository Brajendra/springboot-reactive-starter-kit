package com.reactive.io.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Error implements Serializable {

    WRONG_CREDENTIALS(1000, "Wrong credential!"),
    UN_AUTHORIZE(1001, "User is un-authorize to access this resource"),

    ;

    private int code;
    private String message;

    private Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Error setMessage(String message) {
        this.message = message;
        return this;
    }
}