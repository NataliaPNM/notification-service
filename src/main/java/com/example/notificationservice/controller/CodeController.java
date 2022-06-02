package com.example.notificationservice.controller;

import com.example.notificationservice.dro.ConfirmDto;
import com.example.notificationservice.dro.ConfirmationCodeRequestDto;
import com.example.notificationservice.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("msg")
@RequiredArgsConstructor
@RefreshScope
public class CodeController {

    private final CodeService codeService;

    @PostMapping("/sendCode")
    public String sendCode(@RequestBody ConfirmationCodeRequestDto codeRequestDto) throws IOException {

        return codeService.sendCode(codeRequestDto);

    }
    @PostMapping("/confirm")
    public String confirmCode(@RequestBody ConfirmDto confirmDto){

        return codeService.confirmCode(confirmDto);
    }
}
