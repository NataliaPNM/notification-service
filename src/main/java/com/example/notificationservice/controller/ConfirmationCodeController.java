package com.example.notificationservice.controller;

import com.example.notificationservice.dto.request.ConfirmCodeRequest;
import com.example.notificationservice.dto.request.ResentCodeRequest;
import com.example.notificationservice.dto.response.CodeConfirmationResponse;
import com.example.notificationservice.dto.response.ResentCodeResponse;
import com.example.notificationservice.service.ConfirmationCodeService;
import io.swagger.v3.oas.annotations.Operation;
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

  @Operation(
      summary = "Отправить код ещё раз",
      description =
          "Если прошло 5 минут с отправки кода или если пользователь не получил код, то он может запросить отправку ещё раз")
  @PostMapping("/resend")
  public ResentCodeResponse resentCode(@RequestBody ResentCodeRequest codeRequestDto)
      throws IOException {

    return confirmationCodeService.resentCode(codeRequestDto);
  }

  @Operation(
      summary = "Подтвердить код",
      description =
          "Пользователь вводит код подтверждения который он получил на почту или через push уведомление." +
                  "Если код верный, то операция для которой был выслан код подтверждается ")
  @PostMapping("/confirm")
  public ResponseEntity<CodeConfirmationResponse> confirmCode(
      @RequestBody ConfirmCodeRequest confirmCodeRequest) {
    var body = confirmationCodeService.confirmCode(confirmCodeRequest);
    return ResponseEntity.status(body.getStatus()).body(body);
  }
}
