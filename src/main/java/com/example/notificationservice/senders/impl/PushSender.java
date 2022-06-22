package com.example.notificationservice.senders.impl;

import com.example.notificationservice.senders.Sender;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushSender implements Sender {

  private final FirebaseMessaging firebaseMessaging;

  @SneakyThrows
  @Override
  public Boolean sendNotification(String deviceToken, String subject, String body) {

    Notification notification = Notification.builder().setTitle(subject).setBody(body).build();

    Message message = Message.builder().setTopic("test").setNotification(notification).build();
    firebaseMessaging.send(message);
    return true;
  }
}
