package ru.kpfu.forum.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {
    private String username;
    private String email;
    private String password;
}
