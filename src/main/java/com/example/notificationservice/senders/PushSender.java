package com.example.notificationservice.senders;

public class PushSender implements Sender {

    @Override
    public String sendCode(String deviceToken, String code) {
        return "send code!";
    }
}
