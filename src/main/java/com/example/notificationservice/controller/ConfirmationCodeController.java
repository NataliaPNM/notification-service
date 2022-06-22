package com.example.notificationservice.controller;

import com.example.notificationservice.dto.ConfirmCodeDto;
import com.example.notificationservice.dto.ConfirmationCodeRequestDto;
import com.example.notificationservice.service.ConfirmationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("msg")
@RequiredArgsConstructor
@RefreshScope
public class ConfirmationCodeController {

  private final ConfirmationCodeService confirmationCodeService;

  @PostMapping("/sendCode")
  public String sendCode(@RequestBody ConfirmationCodeRequestDto codeRequestDto)
      throws IOException {

    return confirmationCodeService.sendCode(codeRequestDto);
  }

  @PostMapping("/confirm")
  public boolean confirmCode(@RequestBody ConfirmCodeDto confirmCodeDto) {

    return confirmationCodeService.confirmCode(confirmCodeDto);
  }
}
