package com.example.notificationservice;

import com.example.notificationservice.dto.ConfirmDto;
import com.example.notificationservice.model.ConfirmationCode;
import com.example.notificationservice.model.User;
import com.example.notificationservice.repository.CodeRepository;
import com.example.notificationservice.repository.ConfirmCodeLocksRepository;
import com.example.notificationservice.senders.SendersFactory;
import com.example.notificationservice.senders.impl.EmailSender;
import com.example.notificationservice.service.ConfirmationCodeService;
import com.example.notificationservice.webclient.UserConfigurationWeBclient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.example.notificationservice.Fixtures.getConfirmationCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ConfirmationCodeServiceTest {

    @InjectMocks
    private ConfirmationCodeService confirmationCodeService;
    @Mock
    private CodeRepository codeRepository;
    @Mock
    private ConfirmCodeLocksRepository confirmCodeLocksRepository;
    @Mock
    private SendersFactory sendersFactory;
    @Mock
    private EmailSender emailSender;
    @Mock
    private UserConfigurationWeBclient userService;

    @Test
    void sendCodeTest() throws IOException {
        User user = Fixtures.getUser("ttt@yandex.ru");
        String expectedResult = "true";
        ConfirmationCode confirmationCode = getConfirmationCode("", "email", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "ttt@yandex.ru");
        when(userService.getUserByIdSync(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"))).thenReturn(user);
        when(confirmCodeLocksRepository.findByUserIdAndCodeType(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "email"))
                .thenReturn(Optional.empty());
        when(codeRepository.findByUserIdAndCodeType(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "email"))
                .thenReturn(Optional.empty());
        when(sendersFactory.getSender("email")).thenReturn(emailSender);
        lenient().when(emailSender.sendNotification(eq("ttt@yandex.ru"), eq("code"), any())).thenReturn("true");
        when(codeRepository.save(any())).thenReturn(confirmationCode);

        String actualResult =
                confirmationCodeService.sendCode(Fixtures.getConfirmationCodeRequestDto("email", "code", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void confirmCodeTest() {
        ConfirmDto confirmDto = Fixtures.getConfirmDto("1234", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));
        String expectedResult = "true";
        when(codeRepository.findByUserIdAndLockCode(confirmDto.getUserId(), false))
                .thenReturn(Optional.ofNullable(getConfirmationCode("1234", "email", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "ttt@yandex.ru")));

        String actualResult =
                confirmationCodeService.confirmCode(confirmDto);
        assertEquals(expectedResult, actualResult);
    }
}

