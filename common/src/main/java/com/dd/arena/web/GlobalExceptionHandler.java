package com.dd.arena.web;

import com.dd.arena.ex.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;
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


    // Validation Part:
    // ==================================================================================
    //  借助 Spring 中的 AOP，用一个 ControllerAdvice 统一拦截这种 BadRequestException 异常。
    //  和其他 Exception 也一样，做到异常信息统一处理，也不容易出现安全风险.
    //  之前有遇到过某大型网站因为后台发生异常，直接将具体的 SQL 错误暴露出来了，其中还有表结构等敏感信息
    // ==================================================================================
    @Resource
    MessageSource messageSource;
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity handle(HttpServletRequest request, BadRequestException e){
        String i18message = getI18nMessage("BAD_REQ", request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(i18message);
    }

    private String getI18nMessage(String key, HttpServletRequest request) {
        try {
            return messageSource.getMessage(key, null, currentLocale(request));
        } catch (Exception e) {
            // log
            return key;
        }
    }

    public static Locale currentLocale(HttpServletRequest request) {
        // 从 RequestHeader 等等获取相应的语言信息
        // 简单起见，直接从 queryParams 中取, 只模拟中英文
        String locale = request.getParameter("lang");
        if ("zh".equalsIgnoreCase(locale)) {
            return Locale.CHINA;
        } else {
            return Locale.ENGLISH;
        }
    }
}
