package com.dd.arena.ex;

import org.springframework.http.HttpStatus;

public class AbstractException extends RuntimeException{
    public HttpStatus httpStatus;

    public AbstractException(String message){
        super(message);
    }

    public AbstractException(String message, Exception e){
        super(message,e);
    }
    public HttpStatus getHttpStatus(){
        return httpStatus;
    }
    public void setHttpStatus(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
    }
}
