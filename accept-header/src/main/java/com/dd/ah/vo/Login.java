package com.dd.ah.vo;

import com.dd.ah.anno.UsernameLength;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Login {
    @NotBlank(message = "{valid.login.name}")
    private String name;

    @UsernameLength
    private String nickname;
    @NotNull
    @Size(min = 6, max = 16, message = "{valid.login.passwd}")
    private String password;
}
