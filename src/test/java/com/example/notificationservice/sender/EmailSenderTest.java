package com.example.notificationservice.sender;

import com.example.notificationservice.senders.impl.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class EmailSenderTest {

    @InjectMocks
    private EmailSender emailSender;
    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void sendNotificationReturnTrueTest() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("ttnpnm@yandex.ru");
        msg.setFrom("green.bank@yandex.ru");
        msg.setSubject("code");
        msg.setText("1234");

        Mockito.doNothing().when(javaMailSender).send(msg);

        assertEquals(true, emailSender.sendNotification("ttnpnm@yandex.ru", "code", "1234"));
    }
}
