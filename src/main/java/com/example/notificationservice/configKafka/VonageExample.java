package com.example.notificationservice.configKafka;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VonageExample {
    VonageClient client = VonageClient.builder().apiKey("b46c8636").apiSecret("sFAEOK3ChDkM99gj").build();
    public void sendSms(Long phone, String messageTest){

        TextMessage message = new TextMessage("GreenBank",
                phone.toString(),
                messageTest
        );
        System.out.println(phone.toString());
        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
        }
    }
}
