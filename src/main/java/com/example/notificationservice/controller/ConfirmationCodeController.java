package com.example.notificationservice.controller;

import com.example.notificationservice.dto.request.ConfirmCodeRequest;
import com.example.notificationservice.dto.request.ResentCodeRequest;
import com.example.notificationservice.dto.response.CodeConfirmationResponse;
import com.example.notificationservice.dto.response.ResentCodeResponse;
import com.example.notificationservice.service.ConfirmationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/msg")
@RequiredArgsConstructor
@RefreshScope
public class ConfirmationCodeController {

  private final ConfirmationCodeService confirmationCodeService;

  @PostMapping("/resent")
  public ResentCodeResponse resentCode(@RequestBody ResentCodeRequest codeRequestDto) throws IOException {

    return confirmationCodeService.resentCode(codeRequestDto);
  }

  @PostMapping("/confirm")
  public ResponseEntity<CodeConfirmationResponse> confirmCode(
      @RequestBody ConfirmCodeRequest confirmCodeRequest) {
    var body = confirmationCodeService.confirmCode(confirmCodeRequest);
    return ResponseEntity.status(body.getStatus()).body(body);
  }
}
