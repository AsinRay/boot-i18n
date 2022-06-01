package com.dd.ss.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1393188957993676667L;

    @NotBlank(message = "{valid.test.text.blank}")
    @Size(min = 2, message = "{valid.test.text.min_limit}")
    private String text;

    @NotBlank(message = "{valid.test.email.blank}")
    @Email(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "{valid.test.email.illegal_format}")
    private String email;
}
