package com.example.notificationservice.model;

import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String email;
    private Long phone;
}