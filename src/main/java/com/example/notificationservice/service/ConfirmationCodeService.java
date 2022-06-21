package com.example.notificationservice.service;

import com.example.notificationservice.dto.ConfirmDto;
import com.example.notificationservice.dto.ConfirmationCodeRequestDto;
import com.example.notificationservice.exception.CodeLockException;
import com.example.notificationservice.exception.IncorrectCodeException;
import com.example.notificationservice.model.ConfirmCodeLock;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.repository.CodeRepository;
import com.example.notificationservice.repository.ConfirmCodeLocksRepository;
import com.example.notificationservice.senders.SendersFactory;
import com.example.notificationservice.webclient.UserConfigurationWeBclient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationCodeService {
    private final SendersFactory sendersFactory;
    private final CodeRepository codeRepository;
    private final ConfirmCodeLocksRepository locksRepository;
    private final UserConfigurationWeBclient userService;

    private long minutesLock = 30;
    private int setOfLock = 0;
    private int countOfAttempts = 5;

    public static String generateCode() {
        var randomCode = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(randomCode);
    }

    public String sendCode(ConfirmationCodeRequestDto codeRequestDto) throws IOException {
        String lock = checkLock(codeRequestDto.getUserId(), codeRequestDto.getCodeType());
        if (!lock.equals("true")) {
            return lock;
        }
        var userContacts = userService.getUserByIdSync(codeRequestDto.getUserId());
        var newCode = generateCode();

        if (codeRequestDto.getCodeType().equals("basic")) {
            saveCode(newCode, codeRequestDto.getCodeType(), null, codeRequestDto.getUserId());
            return newCode;
        }
        var sender = sendersFactory.getSender(codeRequestDto.getCodeType());
        if (sender.sendNotification(userContacts.getEmail(), codeRequestDto.getSubject(), newCode).equals("true")) {
            saveCode(newCode, codeRequestDto.getCodeType(), userContacts.getEmail(), codeRequestDto.getUserId());
        }
        return "true";
    }

    public String updateLock(ConfirmCodeLock confirmCodeLock) {

        if (LocalDateTime.parse(confirmCodeLock.getLockTime()).isAfter(LocalDateTime.now())) {
            throw new CodeLockException(
                    String.valueOf(ChronoUnit.MINUTES.between(LocalDateTime.now(), LocalDateTime.parse(confirmCodeLock.getLockTime()))+1));
        } else {
            codeRepository.delete(confirmCodeLock.getConfirmationCode());
            setOfLock = 0;
            locksRepository.delete(confirmCodeLock);
            return "true";
        }
    }

    public String checkLock(UUID userId, String codeType) {
        var storedLock = locksRepository.findByUserIdAndCodeType(userId, codeType);
        if (storedLock.isPresent()) {
            return updateLock(storedLock.get());
        }
        var storedConfirmationCode = codeRepository.findByUserIdAndCodeType(userId, codeType);
        if (storedConfirmationCode.isPresent() && storedConfirmationCode.get().getSendTime().plusMinutes(5L).isAfter(LocalDateTime.now())) {
            long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), storedConfirmationCode.get().getSendTime().plusMinutes(5L));
            throw new ResponseStatusException(
                    HttpStatus.TOO_EARLY, "Code was sent. Next chance will be available after " + minutes + " minutes");
        } else if (storedConfirmationCode.isPresent() && !storedConfirmationCode.get().getSendTime().plusMinutes(5L).isAfter(LocalDateTime.now())) {
            codeRepository.delete(storedConfirmationCode.get());
        }
        return "true";
    }

    public String confirmCode(ConfirmDto confirmDto) {
        var confirmationCode = codeRepository.findByUserIdAndLockCode(confirmDto.getUserId(), false);
        if (confirmationCode.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No codes sent for this user");
        }
        var code = confirmationCode.get();
        if (code.getCode().equals(confirmDto.getConfirmationCode())) {
            codeRepository.delete(confirmationCode.get());
            return "true";
        }
        countOfAttempts--;
        if (countOfAttempts == 0) {
            long lockTime = minutesLock + setOfLock * 15;
            code.setLockCode(true);
            locksRepository.save(ConfirmCodeLock.builder()
                    .lockId(UUID.randomUUID())
                    .confirmationCode(code)
                    .userId(confirmDto.getUserId())
                    .codeType(code.getCodeType()).lockTime(LocalDateTime.now().plusMinutes(lockTime).toString())
                    .build());
            setOfLock++;
            countOfAttempts = 5;
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Attempts are used up, try sending the code again via " + lockTime + " minutes");
        }
        throw new IncorrectCodeException(String.valueOf(countOfAttempts));
    }

    public void saveCode(String code, String codeType, String personContact, UUID userId) {
        var confirmationCode = ConfirmationCode.builder().code(code).codeType(codeType).userId(userId).personContact(personContact).sendTime(LocalDateTime.now()).build();
        codeRepository.save(confirmationCode);
    }
}
