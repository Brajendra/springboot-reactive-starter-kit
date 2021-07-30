package com.reactive.io.util;

import com.reactive.io.entity.dto.ResponseDto;

public class ApiResponseHelper {

    public static <T> ResponseDto getResponse(T data) {

        return ResponseDto.builder()
                .data(data)
                .build();
    }
}
