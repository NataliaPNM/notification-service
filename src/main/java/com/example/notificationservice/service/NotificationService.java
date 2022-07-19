package com.example.notificationservice.service;

import com.example.notificationservice.dto.request.NotificationRequestEvent;
import com.example.notificationservice.dto.request.OperationConfirmEvent;
import com.example.notificationservice.dto.request.PasswordRecoveryNotificationRequest;
import com.example.notificationservice.repository.CodeRepository;
import com.example.notificationservice.senders.SendersFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public  class NotificationService {
  private final SendersFactory sendersFactory;

  public void sendNotification(PasswordRecoveryNotificationRequest notificationRequest, String subject) throws IOException {
      var sender = sendersFactory.getSender("email");
      sender.sendNotification(notificationRequest.getEmail(),subject,notificationRequest.getLink());

  }

    @KafkaListener(topics = "password-recovery", groupId = "send-notification",containerFactory = "kafkaListenerContainerFactoryForPasswordRecoveryNeeds")
    @Transactional
    public void listenNotificationRequest(
            ConsumerRecord<String, PasswordRecoveryNotificationRequest> consumerRecord) throws IOException {
        var passwordRecoveryNotificationRequest = consumerRecord.value();
        sendNotification(passwordRecoveryNotificationRequest,"password-recovery");
    }

}
