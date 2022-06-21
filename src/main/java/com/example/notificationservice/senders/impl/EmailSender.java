package com.example.notificationservice.senders.impl;


import com.example.notificationservice.senders.Sender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class EmailSender implements Sender {

    private final JavaMailSender javaMailSender;

    @Override
    public String sendNotification(String email, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setFrom("green.bank@yandex.ru");
            msg.setSubject(subject);
            msg.setText(body);

            javaMailSender.send(msg);
        } catch (MailAuthenticationException maex) {
            maex.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, maex.getMessage());
        }
        return "true";
    }
}



