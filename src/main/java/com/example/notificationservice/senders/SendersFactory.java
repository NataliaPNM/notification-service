package com.example.notificationservice.senders;

import com.example.notificationservice.senders.impl.EmailSender;
import com.example.notificationservice.senders.impl.PushSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendersFactory {

    private final EmailSender emailSender;
    private final PushSender pushSender;
    public Sender getSender(String type) {
        return switch (type) {
            case "email" -> emailSender;
            case "push" -> pushSender;
            default -> null;
        };
    }
}

