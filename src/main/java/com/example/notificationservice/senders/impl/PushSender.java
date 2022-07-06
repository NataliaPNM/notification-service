package com.example.notificationservice.senders.impl;

import com.example.notificationservice.senders.Sender;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PushSender implements Sender {

  private final FirebaseMessaging firebaseMessaging;

  @Override
  public Boolean sendNotification(String personContact, String subject, String body)
      throws IOException {

        Message message =
            Message.builder()
                .setWebpushConfig(
                    WebpushConfig.builder()
                        .setNotification(
                            WebpushNotification.builder()
                                .setTitle(subject)
                                .setBody(body)
                                .build())
                        .build())
                .setToken(personContact)
                .build();

        try {
          firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
          e.printStackTrace();
        }
    return true;
  }
}
