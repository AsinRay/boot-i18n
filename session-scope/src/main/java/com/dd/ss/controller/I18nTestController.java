package com.dd.ss.controller;


import com.dd.ss.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/i18n")
@RequiredArgsConstructor
public class I18nTestController {

    @PostMapping("/test")
    public Map<String, Object> createUser(@Valid @RequestBody User req, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
            return wrapper(20000, errorMsg, null);
        }
        return wrapper(10000, "ok", req);
    }

    private Map<String, Object> wrapper(int code, String msg, Object data) {
        Map<String, Object> result = new HashMap<>(4);
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }
}
