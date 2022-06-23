package com.example.notificationservice;

import com.example.notificationservice.dto.ConfirmCodeDto;
import com.example.notificationservice.dto.ConfirmationCodeRequestDto;
import com.example.notificationservice.model.ConfirmationCodeLock;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.model.Person;

import java.time.LocalDateTime;
import java.util.UUID;

public class Fixtures {

    public static ConfirmationCodeRequestDto getConfirmationCodeRequestDto(String codeType, String subject, UUID userId) {
        return ConfirmationCodeRequestDto.builder().codeType(codeType).subject(subject).userId(userId).build();
    }
    public static ConfirmCodeDto getConfirmDto(String code, UUID userId){
        return ConfirmCodeDto.builder().confirmationCode(code).userId(userId).build();
    }
    public static Person getUser(String email){
        return Person.builder().email(email).build();
    }
    public static ConfirmationCode getConfirmationCode(String code, String codeType, UUID userId, String personContact,LocalDateTime sendTime) {

        return ConfirmationCode.builder()
                .code(code)
                .codeType(codeType)
                .userId(userId)
                .personContact(personContact)
                .sendTime(sendTime).build();
    }

    public static ConfirmationCodeLock getConfirmCodeLock(UUID lockId, String codeType, String lockTime, UUID userId, ConfirmationCode confirmationCode) {
        return ConfirmationCodeLock.builder()
                .codeType(codeType)
                .userId(userId)
                .lockId(lockId).lockTime(lockTime)
                .confirmationCode(confirmationCode)
                .build();
    }
}
