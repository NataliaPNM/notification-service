package com.example.notificationservice;

import com.example.notificationservice.dto.ConfirmDto;
import com.example.notificationservice.dto.ConfirmationCodeRequestDto;
import com.example.notificationservice.model.ConfirmCodeLock;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.model.User;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.UUID;

public class Fixtures {

    public static ConfirmationCodeRequestDto getConfirmationCodeRequestDto(String codeType, String subject, UUID userId) {
        return ConfirmationCodeRequestDto.builder().codeType(codeType).subject(subject).userId(userId).build();
    }
    public static ConfirmDto getConfirmDto(String code, UUID userId){
        return ConfirmDto.builder().confirmationCode(code).userId(userId).build();
    }
    public static User getUser(String email){
        return User.builder().email(email).build();
    }
    public static ConfirmationCode getConfirmationCode(String code, String codeType, UUID userId, String personContact) {

        return ConfirmationCode.builder()
                .code(code)
                .codeType(codeType)
                .userId(userId)
                .personContact(personContact)
                .sendTime(LocalDateTime.now()).build();
    }

    public static ConfirmCodeLock getConfirmCodeLock(UUID lockId, String codeType, String lockTime, UUID userId, ConfirmationCode confirmationCode) {
        return ConfirmCodeLock.builder()
                .codeType(codeType)
                .userId(userId)
                .lockId(lockId).lockTime(lockTime)
                .confirmationCode(confirmationCode)
                .build();
    }
}
