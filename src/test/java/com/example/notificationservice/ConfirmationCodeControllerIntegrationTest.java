package com.example.notificationservice;

import com.example.notificationservice.dto.ConfirmDto;
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
        ConfirmationCodeRequestDto confirmationCodeRequestDto = new ConfirmationCodeRequestDto();
        confirmationCodeRequestDto.setCodeType("email");
        confirmationCodeRequestDto.setUserId(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));
        when(confirmationCodeService.sendCode(confirmationCodeRequestDto)).thenReturn("Sent message successfully....");
        mockMvc
                .perform(
                        post("/msg/sendCode")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(confirmationCodeRequestDto))
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Sent message successfully...."));
    }

    @Test
    void confirmCodeStatusOkTest() throws Exception {
        ConfirmDto confirmDto = new ConfirmDto();
        confirmDto.setConfirmationCode("1234");
        confirmDto.setUserId(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));

        when(confirmationCodeService.confirmCode(confirmDto)).thenReturn("code confirm!");
        mockMvc
                .perform(
                        post("/msg/confirm")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(confirmDto))
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("code confirm!"));
    }
}
