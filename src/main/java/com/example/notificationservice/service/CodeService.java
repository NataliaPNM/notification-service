package com.example.notificationservice.service;

import com.example.notificationservice.dro.ConfirmDto;
import com.example.notificationservice.dro.ConfirmationCodeRequestDto;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.model.User;
import com.example.notificationservice.repository.CodeRepository;
import com.example.notificationservice.senders.Sender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final Sender sender;
    private final CodeRepository codeRepository;
    private final UserConfigurationWeBclient userService;

    @Transactional
    public String sendCode(ConfirmationCodeRequestDto codeRequestDto) throws IOException {
        User userDto = userService.getUserByIdSync(codeRequestDto.getUserId());
        String code = generateCode();
        saveCode(code,codeRequestDto.getCodeType(),codeRequestDto.getUserId());
        if(codeRequestDto.getCodeType().equals("email")){
            sender.sendCode(userDto.getEmail(),code);
        }else if(codeRequestDto.getCodeType().equals("basic")){
            return code;
        }else return "wrong type";
        return "code sent!";
    }
    public String confirmCode(ConfirmDto confirmDto) {
        ConfirmationCode confirmationCode = codeRepository.findByUserId(confirmDto.getUserId()).orElseThrow();
        if (confirmationCode.getConfirmationCode().equals(confirmDto.getConfirmationCode())) {
            codeRepository.delete(confirmationCode);
            return "code confirm!";
        } else return "code not confirm!";
    }
    public String generateCode(){
        int randomCode   = (int)(Math.random()*9000)+1000;
        return String.valueOf(randomCode);
    }
    @Async
    public void saveCode(String code,String codeType, UUID userId){
        ConfirmationCode confirmationCode = ConfirmationCode.builder().confirmationCode(code)
                .codeType("email")
                .userId(userId)
                .personContact(codeType)
                .sendTime(LocalDateTime.now()).build();
        codeRepository.save(confirmationCode);
    }

}
