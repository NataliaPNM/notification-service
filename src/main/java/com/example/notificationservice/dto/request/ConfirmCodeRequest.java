package com.example.notificationservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmCodeRequest {
  private String confirmationCode;
  private UUID operationId;
  private String type;
}
