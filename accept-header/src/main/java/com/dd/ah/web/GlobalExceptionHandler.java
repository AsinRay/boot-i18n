package com.dd.ah.web;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation Part:
    // ==================================================================================
    //  There are three type of valid exception (http code 400) to be processed manually.
    //  1. BindException
    //  2. MethodArgumentNotValidException
    //  3. ConstraintViolationException
    // ==================================================================================
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public String bindExceptionHandler(final BindException e) {
        String message = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" ; "));
        return "{\"err\":\"" + message + "\"}";
    }

    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // http 5xx
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handler(final MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" ; "));
        return "{\"err\":\"" + message + "\"}";
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handler(final ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" ; "));
        return "{\"errors\":\"" + message + "\"}";
    }
}
