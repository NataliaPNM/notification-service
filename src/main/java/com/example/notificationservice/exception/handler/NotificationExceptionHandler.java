package com.example.notificationservice.exception.handler;

import com.example.notificationservice.exception.*;
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
  private static final String STATUS = "status";
  private static final String ERROR = "error";
  private static final String MESSAGE = "message";
  private final Map<String, String> body = new HashMap<>();

  @ExceptionHandler(value = {IncorrectCodeException.class})
  protected ResponseEntity<Object> handleIncorrectCodeException(
      IncorrectCodeException ex, WebRequest request) {
    body.clear();
    body.put("countOfAttempts", ex.getMessage());
    body.put(STATUS, "400");
    body.put(ERROR, "BAD_REQUEST");
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {CodeLockException.class})
  protected ResponseEntity<Object> handleLockException(CodeLockException ex, WebRequest request) {
    body.clear();
    body.put("lockTime", ex.getMessage());
    body.put(STATUS, "423");
    body.put(ERROR, "LOCKED");
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.LOCKED, request);
  }

  @ExceptionHandler(value = {IncorrectCodeTypeException.class})
  protected ResponseEntity<Object> handleIncorrectCodeTypeException(
      IncorrectCodeTypeException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "422");
    body.put(ERROR, "Unprocessable Entity");
    body.put(MESSAGE, ex.getMessage());
    return handleExceptionInternal(
        ex, body, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

  @ExceptionHandler(value = {ConfirmationCodeExpiredException.class})
  protected ResponseEntity<Object> handleConfirmationCodeExpiredException(
      ConfirmationCodeExpiredException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "408");
    body.put(ERROR, "Request Timeout");
    body.put(MESSAGE, ex.getMessage());
    return handleExceptionInternal(
        ex, body, new HttpHeaders(), HttpStatus.REQUEST_TIMEOUT, request);
  }

  @ExceptionHandler(value = {IncorrectOperationIdException.class})
  protected ResponseEntity<Object> handleIncorrectOperationIdException(
      IncorrectOperationIdException ex, WebRequest request) {
    body.clear();
    body.put(STATUS, "404");
    body.put(ERROR, "Not found");
    body.put(MESSAGE, ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }
}
