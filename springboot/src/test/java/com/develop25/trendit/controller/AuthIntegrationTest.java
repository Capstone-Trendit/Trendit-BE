package com.develop25.trendit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원가입_로그인_성공() throws Exception {
        // 회원가입
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "userId": "testuser",
                      "password": "testpass",
                      "name": "테스터",
                      "age": 30,
                      "gender": "male"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입 성공"));

        // 로그인
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "userId": "testuser",
                      "password": "testpass"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
