package com.example.notificationservice.senders.impl;

import com.example.notificationservice.senders.Sender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class EmailSender implements Sender {

  @Value("${email.sender.host}")
  private String host;

  @Value("${email.sender.port}")
  private String port;

  @Value("${email.sender.username}")
  private String username;

  @Value("${email.sender.password}")
  private String password;

  @Value("${email.transport.protocol}")
  private String protocol;

  // private final JavaMailSender javaMailSender;

  @Override
  public Boolean sendNotification(String email, String subject, String body) {
      Properties properties = System.getProperties();

      properties.put("mail.smtp.host", host);
      properties.put("mail.smtp.port", port);
      properties.put("mail.smtp.ssl.enable", "true");
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.protocol", protocol);


      Session session =
          Session.getInstance(
              properties,
              new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(username, password);
                }
              });

      session.setDebug(true);

      try {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("green.bank@yandex.ru"));
        message.addRecipient(Message.RecipientType.TO, new
   InternetAddress(email));
        message.setSubject(subject);
        message.setContent(
            body,
            "text/html");
        System.out.println("sending...");
        Transport.send(message);
        System.out.println("Sent message successfully....");
      } catch (MessagingException mex) {
        mex.printStackTrace();
      }
      return true;
    }
    }
//
//  @Override
//  public Boolean sendNotification(String email, String subject, String body) {
//    try {
//      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//      mimeMessage.setContent(body, "text/html");
//      mimeMessage.setFrom("green.bank@yandex.ru");
//      mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//      mimeMessage.setSubject(subject);
//
//      javaMailSender.send(mimeMessage);
//    } catch (MailException maex) {
//      maex.printStackTrace();
//      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, maex.getMessage());
//    } catch (MessagingException e) {
//      e.printStackTrace();
//    }
//    return true;
//  }
//}
