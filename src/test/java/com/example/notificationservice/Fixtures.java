package com.example.notificationservice;

import com.example.notificationservice.dto.request.ConfirmCodeRequest;
import com.example.notificationservice.dto.request.ResentCodeRequest;
import com.example.notificationservice.model.ConfirmationCode;

import java.time.LocalDateTime;
import java.util.UUID;

public class Fixtures {

    public static ResentCodeRequest getConfirmationCodeRequestDto(String codeType, String subject, UUID userId) {
        return ResentCodeRequest.builder().operationId(userId).build();
    }
    public static ConfirmCodeRequest getConfirmDto(String code, UUID userId){
        return ConfirmCodeRequest.builder().confirmationCode(code).operationId(userId).build();
    }

    public static ConfirmationCode getConfirmationCode(String code, String codeType, UUID userId, String personContact,LocalDateTime sendTime) {

        return ConfirmationCode.builder()
                .code(code)
                .codeType(codeType)
                .personId(userId)
                .personContact(personContact)
                .sendTime(sendTime).build();
    }


}
