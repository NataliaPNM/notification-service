package com.example.notificationservice;

import com.example.notificationservice.email.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
@EnableCaching
public class NotificationServiceApplication{
    private final UserService userService;


    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    }

