package com.example.notificationservice.webclient;

import com.example.notificationservice.model.Person;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PersonContactsService {
  private static final String USERS_URL_TEMPLATE = "/user/contact";
  private final WebClient webClient;

  @Cacheable("user")
  public Person getUserByIdSync(final UUID userId) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(USERS_URL_TEMPLATE).build())
        .bodyValue(userId)
        .retrieve()
        .onStatus(
            HttpStatus::is4xxClientError,
            error -> Mono.error(new RuntimeException("API not found")))
        .onStatus(
            HttpStatus::is5xxServerError,
            error -> Mono.error(new RuntimeException("Server is not responding")))
        .bodyToMono(Person.class)
        .block();
  }
}
