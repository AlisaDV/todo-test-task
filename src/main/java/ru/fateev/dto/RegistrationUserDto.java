package ru.fateev.dto;

import lombok.Data;

@Data
public class RegistrationUserDto {
    private String email;
    private String username;
    private String password;

}