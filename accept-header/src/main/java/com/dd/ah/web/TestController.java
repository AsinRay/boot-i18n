package com.dd.ah.web;

import com.dd.ah.vo.Login;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TestController {
    @RequestMapping("log")
    public Login login(@Valid @RequestBody Login login){
        // post payload : {"name":"myname","nickname":"NMKJPQWZXIC","password":"Length greater than 16."}
        // post payload : {"name":"myname","nickname":"LENGTH_GREATER_THAN_20_NMKJPQWZXIC","password":"Length greater than 16."}
        System.out.printf("Login valid for [MethodArgumentNotValidException:password] and [ConstraintViolationException:nickname]");
        System.out.println(LocaleContextHolder.getLocale());
        return login;
    }
    @RequestMapping("r")
    public R getI18nMsg(){
        return R.fail(9999);
    }

    @RequestMapping("undefined")
    public R getUndefinedI18nMsg(){
        return R.fail(1111);
    }
}
