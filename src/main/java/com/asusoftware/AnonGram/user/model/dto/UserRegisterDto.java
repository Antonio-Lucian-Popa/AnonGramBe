package com.asusoftware.AnonGram.user.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {
    private String email;
    private String password;
    private String alias;
    private String userRole;
}
