package com.example.kafka.service;

import com.example.kafka.model.RequestMessage;
import com.example.kafka.model.ResponseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaRequestResponseService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaRequestResponseService.class);
    private final ReplyingKafkaTemplate<String, String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String requestTopic;
    private final String replyTopic;

    public KafkaRequestResponseService(
            ReplyingKafkaTemplate<String, String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.requestTopic = "request-topic";
        this.replyTopic = "reply-topic";
    }

    public ResponseMessage sendAndReceive(RequestMessage request) throws Exception {
        String correlationId = java.util.UUID.randomUUID().toString();
        request.setCorrelationId(correlationId);
        
        logger.debug("Sending request with correlationId: {}", correlationId);

        ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, objectMapper.writeValueAsString(request));
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));
        record.headers().add(new RecordHeader(KafkaHeaders.CORRELATION_ID, correlationId.getBytes()));

        logger.debug("Sending message to topic: {} with payload: {}", requestTopic, request.getPayload());
        RequestReplyFuture<String, String, String> replyFuture = kafkaTemplate.sendAndReceive(record);

        try {
            logger.debug("Waiting for response...");
            ConsumerRecord<String, String> response = replyFuture.get(10, TimeUnit.SECONDS);
            logger.debug("Received response: {}", response.value());
            return objectMapper.readValue(response.value(), ResponseMessage.class);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Error processing request", e);
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setCorrelationId(correlationId);
            errorResponse.setError("Error processing request: " + e.getMessage());
            return errorResponse;
        }
    }
}
