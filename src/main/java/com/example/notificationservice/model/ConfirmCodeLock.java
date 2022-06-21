package com.example.notificationservice.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmCodeLock {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID lockId;
    private UUID userId;
    private String lockTime;
    private String codeType;
    @OneToOne
    @JoinColumn(name = "code_id")
    private ConfirmationCode confirmationCode;
}
