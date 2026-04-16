package com.saas.barbershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saas.barbershop.dto.MerchantLoginRequest;
import com.saas.barbershop.dto.MerchantRegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setShopName("测试理发店");
        request.setOwnerName("张三");
        request.setPhone("13800138000");
        request.setPassword("123456");
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.shopName").value("测试理发店"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    void testRegisterValidation() throws Exception {
        MerchantRegisterRequest request = new MerchantRegisterRequest();
        request.setShopName("");
        request.setOwnerName("");
        request.setPhone("invalid");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testLogin() throws Exception {
        MerchantRegisterRequest registerRequest = new MerchantRegisterRequest();
        registerRequest.setShopName("测试理发店");
        registerRequest.setOwnerName("张三");
        registerRequest.setPhone("13800138001");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setPhone("13800138001");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        MerchantLoginRequest loginRequest = new MerchantLoginRequest();
        loginRequest.setPhone("13800000000");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
