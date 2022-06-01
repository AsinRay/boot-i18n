package com.dd.arena.ex;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractException{
    public BadRequestException(String message) {
        super(message);
        setHttpStatus(HttpStatus.BAD_REQUEST);
    }
}
