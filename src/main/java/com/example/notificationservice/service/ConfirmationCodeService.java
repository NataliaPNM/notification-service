package com.example.notificationservice.service;

import com.example.notificationservice.dto.ConfirmCodeDto;
import com.example.notificationservice.dto.ConfirmationCodeRequestDto;
import com.example.notificationservice.exception.CodeLockException;
import com.example.notificationservice.exception.IncorrectCodeException;
import com.example.notificationservice.model.ConfirmationCodeLock;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.repository.CodeRepository;
import com.example.notificationservice.repository.ConfirmationCodeLockRepository;
import com.example.notificationservice.senders.SendersFactory;
import com.example.notificationservice.webclient.PersonContactsService;
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
  private final ConfirmationCodeLockRepository locksRepository;
  private final PersonContactsService userService;

  private int DEFAULT_MINUTES = 30;
  private int LOCK_NUMBER = 0;
  private int ATTEMPTS_COUNT = 5;

  public static String generateCode() {
    var randomCode = (int) (Math.random() * 9000) + 1000;
    return String.valueOf(randomCode);
  }

  public String sendCode(ConfirmationCodeRequestDto codeRequestDto) throws IOException {
    boolean lock = checkLock(codeRequestDto.getUserId(), codeRequestDto.getCodeType());
    if (!lock) {
      return "false";
    }
    var newCode = generateCode();
    if (codeRequestDto.getCodeType().equals("basic")) {
      saveCode(newCode, codeRequestDto.getCodeType(), null, codeRequestDto.getUserId());
      return newCode;
    } else {
      var userContacts = userService.getUserByIdSync(codeRequestDto.getUserId());
      var sender = sendersFactory.getSender(codeRequestDto.getCodeType());
      sender.sendNotification(userContacts.getEmail(), codeRequestDto.getSubject(), newCode);
      saveCode(
          newCode,
          codeRequestDto.getCodeType(),
          userContacts.getEmail(),
          codeRequestDto.getUserId());
    }
    return "true";
  }

  public boolean updateLock(ConfirmationCodeLock confirmationCodeLock) {

    if (LocalDateTime.parse(confirmationCodeLock.getLockTime()).isAfter(LocalDateTime.now())) {
      throw new CodeLockException(
          String.valueOf(
              ChronoUnit.MINUTES.between(
                      LocalDateTime.now(), LocalDateTime.parse(confirmationCodeLock.getLockTime()))
                  + 1));
    } else {
      codeRepository.delete(confirmationCodeLock.getConfirmationCode());
      LOCK_NUMBER = 0;
      locksRepository.delete(confirmationCodeLock);
      return true;
    }
  }

  public boolean checkLock(UUID userId, String codeType) {
    var storedLock = locksRepository.findByUserIdAndCodeType(userId, codeType);
    if (storedLock.isPresent()) {
      return updateLock(storedLock.get());
    }
    var storedConfirmationCode = codeRepository.findByUserIdAndLockCode(userId, false);
    if (storedConfirmationCode.isPresent()
        && storedConfirmationCode
            .get()
            .getSendTime()
            .plusMinutes(5L)
            .isAfter(LocalDateTime.now())) {
      long minutes =
          ChronoUnit.MINUTES.between(
              LocalDateTime.now(), storedConfirmationCode.get().getSendTime().plusMinutes(5L));
      throw new ResponseStatusException(
          HttpStatus.TOO_EARLY,
          "Code was sent. Next chance will be available after " + minutes + " minutes");
    } else if (storedConfirmationCode.isPresent()
        && !storedConfirmationCode
            .get()
            .getSendTime()
            .plusMinutes(5L)
            .isAfter(LocalDateTime.now())) {
      codeRepository.delete(storedConfirmationCode.get());
    }
    return true;
  }

  public boolean confirmCode(ConfirmCodeDto confirmCodeDto) {
    var confirmationCode =
        codeRepository.findByUserIdAndLockCode(confirmCodeDto.getUserId(), false);
    if (confirmationCode.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No codes sent for this user");
    }
    var code = confirmationCode.get();
    if (code.getCode().equals(confirmCodeDto.getConfirmationCode())) {
      codeRepository.delete(confirmationCode.get());
      return true;
    }
    ATTEMPTS_COUNT--;
    if (ATTEMPTS_COUNT == 0) {
      int lockTime = DEFAULT_MINUTES + LOCK_NUMBER * 15;
      code.setLockCode(true);
      locksRepository.save(
          ConfirmationCodeLock.builder()
              .lockId(UUID.randomUUID())
              .confirmationCode(code)
              .userId(confirmCodeDto.getUserId())
              .codeType(code.getCodeType())
              .lockTime(LocalDateTime.now().plusMinutes(lockTime).toString())
              .build());
      LOCK_NUMBER++;
      ATTEMPTS_COUNT = 5;
      throw new IncorrectCodeException(ATTEMPTS_COUNT+","+lockTime);
    }
    throw new IncorrectCodeException(String.valueOf(ATTEMPTS_COUNT));
  }

  public void saveCode(String code, String codeType, String personContact, UUID userId) {
    var confirmationCode =
        ConfirmationCode.builder()
            .code(code)
            .codeType(codeType)
            .userId(userId)
            .personContact(personContact)
            .sendTime(LocalDateTime.now())
            .build();
    codeRepository.save(confirmationCode);
  }
}
