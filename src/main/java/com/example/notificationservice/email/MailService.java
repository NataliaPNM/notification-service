package com.example.notificationservice.email;

import com.example.notificationservice.configKafka.VonageExample;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.model.User;
import com.example.notificationservice.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService  {

    private final EmailSender emailSender;
    private final UserService userService;
    private final CodeRepository codeRepository;
    private final VonageExample vonageExample;
    private final KafkaTemplate<Long, Long> kafkaTemplate;
    private String email;
    private Long phone;
    private  String messageText;


    @Transactional
    public String sendCode(String type, Long userId) {
        return switch (type) {
            case "email" -> sendEmailCode(userId);
            case "sms" -> sendSmsCode(userId);
            case "basic" -> sendBasicCode(userId);
            default -> "wrong type";
        };
    }
    public String confirmCode(String code, Long userId) {
        ConfirmationCode confirmationCode = codeRepository.findByUserId(userId).get();
        if (confirmationCode.getCode().equals(code)) {
            return "code confirm!";
        } else return "code not confirm!";
    }
    private String sendBasicCode(Long userId) {
        messageText = generateCode();
        ConfirmationCode confirmationCode = ConfirmationCode.builder().code(messageText)
                .type("email")
                .userId(userId)
                .contact(email)
                .sendTime(LocalDateTime.now()).build();
        codeRepository.save(confirmationCode);
        return "Ваш код подтверждения: "+messageText;
    }

    private String sendSmsCode(Long userId) {
        User userDto = userService.getUserByIdSync(1L);
        phone = userDto.getPhone();
        messageText = generateCode();

        new Thread(new Runnable() {
            @Override
            public void run() {
        vonageExample.sendSms(phone,"Your confirmation code: "+messageText);
                ConfirmationCode confirmationCode = ConfirmationCode.builder().code(messageText)
                        .type("email")
                        .userId(userId)
                        .contact(email)
                        .sendTime(LocalDateTime.now()).build();

                codeRepository.save(confirmationCode);
            }
        }).start();
        return "send code with sms";
    }
    public String generateCode(){
        int min = 1000;
        int max = 10000;
        Integer s = new Random().nextInt(min,max);
       return s.toString();
    }

    public String sendEmailCode(Long userId) {
//        Long msgId = 1L;
//        ListenableFuture<SendResult<Long, Long>> future = kafkaTemplate.send("userId", msgId, userId);
//        future.addCallback(System.out::println, System.err::println);
//        kafkaTemplate.flush();

        email = getEmailFromUserIdentity(userId);
        messageText = generateCode();

                emailSender.send(email, "Activation Code", "Ваш код подтверждения: "+messageText);
                ConfirmationCode confirmationCode = ConfirmationCode.builder().code(messageText)
                        .type("email")
                        .userId(userId)
                        .contact(email)
                        .sendTime(LocalDateTime.now()).build();
                codeRepository.save(confirmationCode);

        return "send code with email";
    }
    @Cacheable(cacheNames="email")
    public String getEmailFromUserIdentity(Long id){

        User userDto = userService.getUserByIdSync(id);
        String result = userDto.getEmail();
        return result;
    }

//    @KafkaListener(topics = "email")
//    public void listen(ConsumerRecord<Long, String> record) {
//        setEmail(record.value());
//        System.out.println(record.value());
//    }
}
