package com.develop25.trendit.service;

import com.develop25.trendit.config.JwtProvider;
import com.develop25.trendit.domain.User;
import com.develop25.trendit.dto.UserLoginDto;
import com.develop25.trendit.dto.UserRegisterDto;
import com.develop25.trendit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void register(UserRegisterDto dto) {
        if (userRepository.existsById(dto.getUserId())) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        User user = new User();
        user.setUserId(dto.getUserId());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setReceiveAlarm(false);
        userRepository.save(user);
    }

    public String login(UserLoginDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }
        return jwtProvider.generateToken(user.getUserId());
    }
}