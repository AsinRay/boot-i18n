package com.dd.ah.anno;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 用户名长度校验Validator：
 * 这里需要实现javax.validation.ConstraintValidator用于定义
 * 具体的校验规则，这里只校验了字符串的长度.
 */
public class UsernameLengthValidator implements ConstraintValidator<UsernameLength, String> {
    public static final int MAX_LENGTH = 20;
    public static final int MIN_LENGTH = 1;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !StringUtils.isEmpty(value) && value.length() >= MIN_LENGTH && value.length() <= MAX_LENGTH;
    }
}