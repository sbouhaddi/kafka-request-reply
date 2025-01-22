package com.example.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final ProducerFactory<String, String> producerFactory;

    private final ConsumerFactory<String, String> consumerFactory;

    public KafkaConfig(ProducerFactory<String, String> producerFactory, ConsumerFactory<String, String> consumerFactory) {
        this.producerFactory = producerFactory;
        this.consumerFactory = consumerFactory;
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(
) {
        ReplyingKafkaTemplate<String, String, String> template = new ReplyingKafkaTemplate<>(producerFactory, repliesContainer());
        template.setSharedReplyTopic(true);
        return template;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> repliesContainer() {
        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                kafkaListenerContainerFactory().createContainer("reply-topic");
        repliesContainer.getContainerProperties().setGroupId("repliesGroup");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setReplyTemplate(new KafkaTemplate<>(producerFactory));
        return factory;
    }

    @Bean
    public NewTopic requestTopic() {
        return new NewTopic("request-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic replyTopic() {
        return new NewTopic("reply-topic", 1, (short) 1);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
