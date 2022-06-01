package com.dd.ah.anno;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(value = { ElementType.FIELD, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Repeatable(UsernameLength.List.class)
@Constraint(validatedBy = UsernameLengthValidator.class)
public @interface UsernameLength {

    String message() default "{ud.valid.usernameLength}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 允许添加多个重复注解.
     */
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        UsernameLength[] value();
    }
}