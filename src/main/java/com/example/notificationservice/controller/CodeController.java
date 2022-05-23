package com.example.notificationservice.controller;

import com.example.notificationservice.dro.ConfirmDto;
import com.example.notificationservice.email.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("msg")
public class CodeController {
    @Autowired
    private MailService mailService;

    @GetMapping("/sendCode")
    public String sendCode(String type, Long id){

        return mailService.sendCode(type, id);

    }
    @PostMapping("/confirm")
    public String confirmCode(@RequestBody ConfirmDto confirmDto){

        return mailService.confirmCode(confirmDto.getCode(),confirmDto.getUserId());
    }
}

