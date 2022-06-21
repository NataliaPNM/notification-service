package com.example.notificationservice.senders.impl;

import com.example.notificationservice.model.Note;
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
    public String sendNotification(String deviceToken, String subject, String body) {
        Note note = new Note();
        note.setContent(body);
        note.setSubject(subject);


        Notification notification = Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .build();

        Message message = Message
                .builder()
                .setTopic("gold")
                .setNotification(notification)
                //.putAllData(note.getData())
                .build();
        firebaseMessaging.send(message);
        return "true";
    }

}
