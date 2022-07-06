package com.example.notificationservice.service;
//
//import com.example.notificationservice.Fixtures;
//import com.example.notificationservice.exception.CodeLockException;
//import com.example.notificationservice.exception.IncorrectCodeException;
//import com.example.notificationservice.model.ConfirmationCode;
//import com.example.notificationservice.repository.CodeRepository;
//import com.example.notificationservice.senders.SendersFactory;
//import com.example.notificationservice.senders.impl.EmailSender;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.UUID;
//
//import static com.example.notificationservice.Fixtures.getConfirmationCode;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@Slf4j
//class ConfirmationCodeServiceTest {
//
//    @InjectMocks
//    private ConfirmationCodeService confirmationCodeService;
//    @Mock
//    private CodeRepository codeRepository;
//
//    @Mock
//    private SendersFactory sendersFactory;
//    @Mock
//    private EmailSender emailSender;
//

//    @Test
//    void sendCodeTest() throws IOException {
//        OperationDto person = Fixtures.getUser("ttt@yandex.ru");
//        String expectedResult = "true";
//        ConfirmationCode confirmationCode = getConfirmationCode("", "email", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "ttt@yandex.ru", LocalDateTime.now());
//
//       lenient().when(codeRepository.findByUserIdAndCodeType(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "email"))
//                .thenReturn(Optional.empty());
//        when(sendersFactory.getSender("email")).thenReturn(emailSender);
//        when(emailSender.sendNotification(eq("ttt@yandex.ru"), eq("code"), any())).thenReturn(true);
//        when(codeRepository.save(any())).thenReturn(confirmationCode);
//
//        String actualResult =
//                confirmationCodeService.sendCode(Fixtures.getConfirmationCodeRequestDto("email", "code", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")));
//
//        assertEquals(expectedResult, actualResult);
//    }
//
//    @Test
//    void confirmCodeReturnTrueTest() {
//        ConfirmCodeDto confirmCodeDto = Fixtures.getConfirmDto("1234", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"));
//        boolean expectedResult = true;
//        when(codeRepository.findByUserIdAndLockCode(confirmCodeDto.getUserId(), false))
//                .thenReturn(Optional.ofNullable(getConfirmationCode("1234", "email", UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"), "ttt@yandex.ru", LocalDateTime.now())));
//
//        boolean actualResult =
//                confirmationCodeService.confirmCode(confirmCodeDto);
//        assertEquals(expectedResult, actualResult);
//    }
//
//    @Test
//    void confirmCodeThrowNotFoundExceptionTest() {
//        UUID userId = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
//        when(codeRepository.findByUserIdAndLockCode(userId, false)).thenReturn(Optional.empty());
//        ResponseStatusException thrown = assertThrows(
//                ResponseStatusException.class,
//                () -> confirmationCodeService.confirmCode(Fixtures.getConfirmDto("1234",userId)));
//
//        assertTrue(thrown.getMessage().contains("No codes sent for this user"));
//    }
//    @Test
//    void confirmCodeThrowIncorrectCodeExceptionTest() {
//        ConfirmationCode code = Fixtures.getConfirmationCode("1223", "email", null, null, LocalDateTime.now().minusMinutes(5L));
//        UUID userId = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
//        when(codeRepository.findByUserIdAndLockCode(userId, false)).thenReturn(Optional.ofNullable(code));
//        IncorrectCodeException thrown = assertThrows(
//                IncorrectCodeException.class,
//                () -> confirmationCodeService.confirmCode(Fixtures.getConfirmDto("1234",userId)));
//
//        assertTrue(thrown.getMessage().contains("4"));
//    }
//
//    @Test
//    void updateLockThrowCodeLockExceptionTest() {
//        ConfirmationCodeLock codeLock = Fixtures.getConfirmCodeLock(null, "email", LocalDateTime.now().plusMinutes(5L).toString(), null, null);
//
//        CodeLockException thrown = assertThrows(
//                CodeLockException.class,
//                () -> confirmationCodeService.updateLock(codeLock));
//
//        assertTrue(thrown.getMessage().contains("5"));
//    }
//
//    @Test
//    void updateLockReturnTrueTest() {
//        ConfirmationCodeLock codeLock = Fixtures.getConfirmCodeLock(null, "email", LocalDateTime.now().toString(), null, null);
//        boolean expectedResult = true;
//        boolean actualResult = confirmationCodeService.updateLock(codeLock);
//
//        assertEquals(expectedResult, actualResult);
//    }
//
//    @Test
//    void checkLockThrowExceptionWithStatusTooEarlyTest() {
//        ConfirmationCode code = Fixtures.getConfirmationCode("1223", "email", null, null, LocalDateTime.now());
//        UUID userId = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
//        String codeType = "email";
//        when(codeRepository.findByUserIdAndLockCode(userId, false)).thenReturn(Optional.ofNullable(code));
//        when(confirmationCodeLockRepository.findByUserIdAndCodeType(userId, codeType)).thenReturn(Optional.empty());
//        ResponseStatusException thrown = assertThrows(
//                ResponseStatusException.class,
//                () -> confirmationCodeService.checkLock(userId, codeType));
//
//        assertTrue(thrown.getMessage().contains("Code was sent. Next chance will be available after 4 minutes"));
//    }
//
//    @Test
//    void checkLockReturnTrueTest() {
//        UUID userId = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");
//        String codeType = "email";
//        ConfirmationCode code = Fixtures.getConfirmationCode("1223", "email", null, null, LocalDateTime.now().minusMinutes(5L));
//        when(codeRepository.findByUserIdAndLockCode(userId, false)).thenReturn(Optional.ofNullable(code));
//        when(confirmationCodeLockRepository.findByUserIdAndCodeType(userId, codeType)).thenReturn(Optional.empty());
//        boolean expectedResult = true;
//        boolean actualResult = confirmationCodeService.checkLock(userId, codeType);
//
//        assertEquals(expectedResult, actualResult);
//    }
//}

