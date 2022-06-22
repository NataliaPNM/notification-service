package com.example.notificationservice.controller;

import com.example.notificationservice.Fixtures;
import com.example.notificationservice.dto.ConfirmCodeDto;
import com.example.notificationservice.dto.ConfirmationCodeRequestDto;
import com.example.notificationservice.service.ConfirmationCodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
class ConfirmationCodeControllerIntegrationTest {
    @MockBean
    private ConfirmationCodeService confirmationCodeService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendCodeStatusOkTest() throws Exception {
        ConfirmationCodeRequestDto confirmationCodeRequestDto = Fixtures.getConfirmationCodeRequestDto("email","code", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));

        when(confirmationCodeService.sendCode(confirmationCodeRequestDto)).thenReturn("true");
        mockMvc
                .perform(
                        post("/msg/sendCode")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(confirmationCodeRequestDto))
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void confirmCodeStatusOkTest() throws Exception {
        ConfirmCodeDto confirmCodeDto = Fixtures.getConfirmDto("1234",UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));

        when(confirmationCodeService.confirmCode(confirmCodeDto)).thenReturn(true);
        mockMvc
                .perform(
                        post("/msg/confirm")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(confirmCodeDto))
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
