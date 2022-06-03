package com.example.notificationservice.senders;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EmailSender implements Sender {

    private Session session;
    private Transport transport;

    @Override
    public String sendCode(String emailTo, String code) throws IOException {
        Properties props = new Properties();
        if (session == null) {
            props.put("mail.smtp.auth","true");
            props.put("mail.smtp.protocol","smtps");
            props.put("mail.smtp.host","smtp.yandex.ru");
            props.put(" mail.smtp.port","465");
            session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("ttnpnm", "slcadmocweblqndu");
                        }
                    });
            System.out.println("initialization session");
        }
        try {

            Message messages = new MimeMessage(session);
            messages.setFrom(new InternetAddress("ttnpnm@yandex.ru"));
            messages.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailTo));
            messages.setSubject("Confirmation code");
            messages.setText("Hello, this is your confirmation code: " + code);
            if (transport == null) {
                transport = session.getTransport("smtps");
                transport.connect("smtp.yandex.ru", "ttnpnm", "slcadmocweblqndu");
                System.out.println("initialization transport");
            }

            InternetAddress[] address = InternetAddress.parse(emailTo);
            transport.sendMessage(messages, address);

        } catch (Exception e) {
            e.printStackTrace();
            return "failed to send code";
        }
        return "Sent message successfully....";
    }
}



