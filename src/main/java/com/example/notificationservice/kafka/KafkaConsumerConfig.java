package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.request.NotificationRequestEvent;
import com.example.notificationservice.dto.request.PasswordRecoveryNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String kafkaServer;

  @Value("${spring.kafka.consumer.group-id}")
  private String kafkaGroupId;

    @Bean
    public Map<String, Object> consumerConfigs() {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationRequestEvent.class.getName());
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
    props.put(
        ConsumerConfig.ISOLATION_LEVEL_CONFIG,
        IsolationLevel.READ_COMMITTED.toString().toLowerCase(Locale.ROOT));
    return props;
  }
  @Bean
  public Map<String, Object> consumerConfigsForPasswordRecoveryNeeds() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PasswordRecoveryNotificationRequest.class.getName());
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
    props.put(
            ConsumerConfig.ISOLATION_LEVEL_CONFIG,
            IsolationLevel.READ_COMMITTED.toString().toLowerCase(Locale.ROOT));
    return props;
  }


  @Bean(name = "kafkaListenerContainerFactoryForPasswordRecoveryNeeds")
  public KafkaListenerContainerFactory<?> kafkaListenerContainerFactoryForPasswordRecoveryNeeds() {
    ConcurrentKafkaListenerContainerFactory<String, PasswordRecoveryNotificationRequest> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactoryForPasswordRecoveryNeeds());

    return factory;
  }
  @Bean(name = "kafkaListenerContainerFactory")
  public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, NotificationRequestEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());

    return factory;
  }


  @Bean
  public ConsumerFactory<String, NotificationRequestEvent> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(
        consumerConfigs(),new StringDeserializer(),
            new JsonDeserializer<>(NotificationRequestEvent.class, false));
  }
  @Bean
  public ConsumerFactory<String, PasswordRecoveryNotificationRequest> consumerFactoryForPasswordRecoveryNeeds() {
    return new DefaultKafkaConsumerFactory<>(
            consumerConfigsForPasswordRecoveryNeeds(),
            new StringDeserializer(),
            new JsonDeserializer<>(PasswordRecoveryNotificationRequest.class, false));
  }
}
