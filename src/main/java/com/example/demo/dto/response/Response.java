package com.example.demo.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Response<T> {
    private String code;
    private String message;
    private T data;

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }
}