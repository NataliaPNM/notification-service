package com.example.notificationservice.service;

import com.example.notificationservice.dto.request.ConfirmCodeRequest;
import com.example.notificationservice.dto.request.NotificationRequestEvent;
import com.example.notificationservice.dto.request.OperationConfirmEvent;
import com.example.notificationservice.dto.request.ResentCodeRequest;
import com.example.notificationservice.dto.response.CodeConfirmationResponse;
import com.example.notificationservice.dto.response.ResentCodeResponse;
import com.example.notificationservice.exception.*;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.repository.CodeRepository;
import com.example.notificationservice.senders.SendersFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationCodeService {
  private final SendersFactory sendersFactory;
  private final CodeRepository codeRepository;
  private final KafkaTemplate<String, OperationConfirmEvent> kafkaTemplate;
  private int DEFAULT_LOCK_MINUTES = 30;
  private int LOCK_SET = 0;
  private int ATTEMPTS_COUNT = 5;

  public static String generateCode() {
    var randomCode = (int) (Math.random() * 9000) + 1000;
    return String.valueOf(randomCode);
  }

  public ResentCodeResponse resentCode(ResentCodeRequest resentCodeRequest) throws IOException {
    if(!resentCodeRequest.getType().equals("email") && !resentCodeRequest.getType().equals("push")){
      throw new IncorrectCodeTypeException("Incorrect confirmation type");
    }
    var code =
        getConfirmationCode(resentCodeRequest.getOperationId(), resentCodeRequest.getType())
            .orElseThrow(
                () -> new IncorrectOperationIdException("No codes available for this operation"));

    updateCode(resentCodeRequest.getOperationId(), resentCodeRequest.getType());
    var newCode = generateCode();
    var sender = sendersFactory.getSender(resentCodeRequest.getType());
    sender.sendNotification(code.getPersonContact(), "Confirmation code", newCode);
    saveCode(
        newCode,
        code.getPersonId(),
        resentCodeRequest.getType(),
        code.getPersonContact(),
        resentCodeRequest.getOperationId(),
        code.getSource());
    return ResentCodeResponse.builder().personContact(code.getPersonContact()).build();
  }

  public void updateCodeRepository() {
    var codes = codeRepository.findAll();
    for (ConfirmationCode code : codes) {
      if (code.getSendTime().plusHours(1L).isBefore(LocalDateTime.now())) {
        codeRepository.delete(code);
      }
    }
  }

  public void sendCode(NotificationRequestEvent notificationRequestEvent) throws IOException {
    updateCodeRepository();
    updateCode(notificationRequestEvent.getOperationId(), notificationRequestEvent.getType());

    var newCode = generateCode();
    var sender = sendersFactory.getSender(notificationRequestEvent.getType());
    sender.sendNotification(notificationRequestEvent.getContact(), "Confirmation code", newCode);
    saveCode(
        newCode,
        notificationRequestEvent.getPersonId(),
        notificationRequestEvent.getType(),
        notificationRequestEvent.getContact(),
        notificationRequestEvent.getOperationId(),
        notificationRequestEvent.getSource());
  }

  public void updateCode(UUID operationId, String codeType) {
    var code = getConfirmationCode(operationId, codeType);

    if (code.isPresent() && code.get().isLockCode()) {

      if (code.get().getLockTime().isAfter(LocalDateTime.now())) {
        throw new CodeLockException(getUnlockTimeInMs(code.get().getLockTime()));
      } else if (code.get().getLockTime().isBefore(LocalDateTime.now())) {
        codeRepository.delete(code.get());
        LOCK_SET = 0;
      }
    } else if (code.isPresent() && !code.get().isLockCode()) {
      codeRepository.delete(code.get());
    }
  }
  // ?????????? ???????????????????????? ?????????? ???? ??????????
  public void checkCodeInput(ConfirmationCode code, ConfirmCodeRequest confirmCodeRequest) {
    if (code.isLockCode() && code.getLockTime().isAfter(LocalDateTime.now())) {
      throw new CodeLockException(getUnlockTimeInMs(code.getLockTime()));
    } else if (code.isLockCode() && code.getLockTime().isBefore(LocalDateTime.now())) {
      codeRepository.delete(code);
      throw new ConfirmationCodeExpiredException("Code not valid");
    }
    if (code.getCode().equals(confirmCodeRequest.getConfirmationCode())
        && code.getSendTime().plusMinutes(5L).isBefore(LocalDateTime.now())) {
      codeRepository.delete(code);
      throw new ConfirmationCodeExpiredException("Code not valid");
    }
  }

  @Transactional
  public CodeConfirmationResponse countIncorrectAttempts(ConfirmationCode code) {
    ATTEMPTS_COUNT--;
    if (ATTEMPTS_COUNT == 0) {
      var lockTime = DEFAULT_LOCK_MINUTES + LOCK_SET * 15;
      code.setLockCode(true);
      code.setLockTime(LocalDateTime.now().plusMinutes(lockTime));
      codeRepository.save(code);
      LOCK_SET++;
      ATTEMPTS_COUNT = 5;


      return updatePersonLocks(code);
    }
    throw new IncorrectCodeException(String.valueOf(ATTEMPTS_COUNT));
  }

  @Transactional
  public CodeConfirmationResponse updatePersonLocks(ConfirmationCode code) {
    var codes = codeRepository.findByPersonIdAndLockCode(code.getPersonId(), true);
    boolean emailLock = false;
    boolean pushLock = false;
    var minTime = LocalDateTime.now().plusDays(1L);
    for (Optional<ConfirmationCode> codeLock : codes) {
      if (codeLock.isPresent() && codeLock.get().getCodeType().equals("email")) {
        emailLock = true;
      } else if (codeLock.isPresent() && codeLock.get().getCodeType().equals("push")) {
        pushLock = true;
      }
      if (codeLock.isPresent() && codeLock.get().getLockTime().isBefore(minTime)) {
        minTime = codeLock.get().getLockTime();
      }
    }
    if (emailLock && pushLock) {
      sendOperationConfirmEvent(code.getOperationId(), code, "fullLock", minTime.toString());
    }

    return sendOperationConfirmEvent(
        code.getOperationId(), code, "lock", code.getLockTime().toString());
  }

  public String getUnlockTimeInMs(LocalDateTime to) {

    return String.valueOf(ChronoUnit.MILLIS.between(LocalDateTime.now(), to));
  }

  @Transactional
  public CodeConfirmationResponse confirmCode(ConfirmCodeRequest confirmCodeRequest) {
    if(!confirmCodeRequest.getType().equals("email") && !confirmCodeRequest.getType().equals("push")){
      throw new IncorrectCodeTypeException("Incorrect confirmation type");
    }
    var code =
        getConfirmationCode(confirmCodeRequest.getOperationId(), confirmCodeRequest.getType())
            .orElseThrow(
                () -> new IncorrectOperationIdException("No codes available for this operation"));
    checkCodeInput(code, confirmCodeRequest);
    if (code.getCode().equals(confirmCodeRequest.getConfirmationCode())) {
      return sendOperationConfirmEvent(confirmCodeRequest.getOperationId(), code, "confirm", "");
    }
    return countIncorrectAttempts(code);
  }

  public Optional<ConfirmationCode> getConfirmationCode(UUID operationId, String codeType) {
    return codeRepository.findByOperationIdAndCodeType(operationId, codeType);
  }

  @Transactional(transactionManager = "kafkaTransactionManager")
  public CodeConfirmationResponse sendOperationConfirmEvent(
      UUID operationId, ConfirmationCode code, String status, String lockTime) {
    kafkaTemplate.send(
        code.getSource(),
        OperationConfirmEvent.builder()
            .status(status)
            .operationId(operationId)
            .lockTime(lockTime)
            .personId(code.getPersonId())
            .build());

    switch (status) {
      case "confirm":
        ATTEMPTS_COUNT = 5;
        codeRepository.delete(code);
        return CodeConfirmationResponse.builder()
            .status(HttpStatus.OK)
            .message("Operation confirm")
            .build();
      case "lock":

        return CodeConfirmationResponse.builder()
            .status(HttpStatus.LOCKED)
            .message("Choose another confirmation type")
            .unlockTime(getUnlockTimeInMs(code.getLockTime()))
            .build();
      case "fullLock":
        return CodeConfirmationResponse.builder()
            .status(HttpStatus.FORBIDDEN)
            .message("All confirmation ways locked")
            .unlockTime(getUnlockTimeInMs(LocalDateTime.parse(lockTime)))
            .build();
      default:
        throw new IllegalStateException("Unexpected value: " + status);
    }
  }

  public void saveCode(
      String code,
      UUID personId,
      String codeType,
      String personContact,
      UUID operationId,
      String source) {
    var confirmationCode =
        ConfirmationCode.builder()
            .code(code)
            .personId(personId)
            .codeType(codeType)
            .operationId(operationId)
            .personContact(personContact)
            .source(source)
            .sendTime(LocalDateTime.now())
            .lockCode(false)
            .lockTime(null)
            .build();
    codeRepository.save(confirmationCode);
  }

  @KafkaListener(topics = "notification-request", groupId = "send-notification",containerFactory = "kafkaListenerContainerFactory")
  @Transactional
  public void listenNotificationRequest(
      ConsumerRecord<String, NotificationRequestEvent> consumerRecord) throws IOException {
    var notificationRequest = consumerRecord.value();
    sendCode(notificationRequest);
  }

  @KafkaListener(topics = "delete-code-request", groupId = "send-notification")
  @Transactional
  public void listenDeleteCodeRequestTopic(
      ConsumerRecord<String, NotificationRequestEvent> consumerRecord){
    var notificationRequest = consumerRecord.value();
    deleteCode(notificationRequest);
  }

  public void deleteCode(NotificationRequestEvent notificationRequestEvent) {
    var code =
        codeRepository.findByOperationIdAndLockCode(
            notificationRequestEvent.getOperationId(), false);
    if (code.isPresent() && code.get().getCodeType().equals(notificationRequestEvent.getType())) {
      codeRepository.delete(code.get());
    }
  }
}
