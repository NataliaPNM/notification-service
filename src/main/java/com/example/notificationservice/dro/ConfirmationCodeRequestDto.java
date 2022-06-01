package com.example.notificationservice.dro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationCodeRequestDto {
    private String codeType;
    private UUID userId;
}
