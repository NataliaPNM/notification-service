package com.example.notificationservice.exception.handler;

import com.example.notificationservice.exception.CodeLockException;
import com.example.notificationservice.exception.IncorrectCodeException;
import com.example.notificationservice.exception.IncorrectCodeTypeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class NotificationExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {IncorrectCodeException.class})
  protected ResponseEntity<Object> handleIncorrectCodeException(
      IncorrectCodeException ex, WebRequest request) {
    Map<String, String> body = new HashMap<>();
    body.put("countOfAttemts", ex.getMessage());
    body.put("status", "400");
    body.put("error", "BAD_REQUEST");
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {CodeLockException.class})
  protected ResponseEntity<Object> handleLockException(CodeLockException ex, WebRequest request) {
    Map<String, String> body = new HashMap<>();
    body.put("lockTime", ex.getMessage());
    body.put("status", "423");
    body.put("error", "LOCKED");
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.LOCKED, request);
  }

  @ExceptionHandler(value = {IncorrectCodeTypeException.class})
  protected ResponseEntity<Object> handleIncorrectCodeTypeException(
      IncorrectCodeTypeException ex, WebRequest request) {
    Map<String, String> body = new HashMap<>();
    body.put("status", "400");
    body.put("error", "BAD_REQUEST");
    body.put("message", ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
