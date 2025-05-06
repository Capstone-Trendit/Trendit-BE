package com.develop25.trendit.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String userId;
    private String password;
    private String name;
    private Integer age;
    private String gender;
}
