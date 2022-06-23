package com.example.notificationservice.swagger;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI springShopOpenAPI() {
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList("Auth JWT"))
        .info(
            new Info()
                .title("Notification-service")
                .description("Service for sending and confirming the code")
                .version("v0.0.1"));
  }
}
