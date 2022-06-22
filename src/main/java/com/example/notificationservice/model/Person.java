package com.example.notificationservice.model;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
  private UUID id;
  private String email;
  private Long phone;
  private String login;
  private String deviceToken;
}
