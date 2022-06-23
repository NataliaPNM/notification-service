package com.example.notificationservice.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfiguration {

  @SneakyThrows
  @Bean
  public FirebaseMessaging firebaseMessaging() {
    GoogleCredentials googleCredentials =
        GoogleCredentials.fromStream(
            new ClassPathResource("firebase-service-account.json").getInputStream());
    FirebaseOptions firebaseOptions =
        FirebaseOptions.builder().setCredentials(googleCredentials).build();
    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-apps");
    return FirebaseMessaging.getInstance(app);
  }
}
