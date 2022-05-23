package com.example.notificationservice.email;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EmailSender {
//    @Value("${spring.mail.username}")
//    private String username;
    private  Session session;
    private Transport transport;
    private JavaMailSender mailSender;
    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailSender.class);

    @Async
    public void send(String emailTo, String subject, String message){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.protocol", "smtps");
        //props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "465");
        if(session==null){
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
            // Set From: header field of the header.
            messages.setFrom(new InternetAddress("ttnpnm@yandex.ru"));
            // Set To: header field of the header.
            messages.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailTo));
            // Set Subject: header field
            messages.setSubject("Confirmation code");
            // Now set the actual message
            messages.setText("Hello, this is your confirmation code: "+message);
            if(transport==null){
                transport = session.getTransport("smtps");
                transport.connect("smtp.yandex.ru","ttnpnm","slcadmocweblqndu");
                System.out.println("initialization transport");
            }

            InternetAddress[] address = InternetAddress.parse(emailTo);
            transport.sendMessage(messages,address);
            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}



