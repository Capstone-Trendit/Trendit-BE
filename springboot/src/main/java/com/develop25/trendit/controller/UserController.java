package com.develop25.trendit.controller;

import com.develop25.trendit.dto.UserLoginDto;
import com.develop25.trendit.dto.UserRegisterDto;
import com.develop25.trendit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto dto) {
        userService.register(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto dto) {
        String token = userService.login(dto);
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}