package com.example.notificationservice.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationCode {

  @Id
  @EqualsAndHashCode.Exclude
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID codeId;

  private UUID personId;
  private UUID operationId;
  private String code;
  private String codeType;
  private String personContact;
  private String source;
  private LocalDateTime sendTime;
  private boolean lockCode;
  private LocalDateTime lockTime;
}
