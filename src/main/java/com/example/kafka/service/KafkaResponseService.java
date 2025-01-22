package com.example.kafka.service;

import com.example.kafka.model.RequestMessage;
import com.example.kafka.model.ResponseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class KafkaResponseService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaResponseService.class);
    private final ObjectMapper objectMapper;

    public KafkaResponseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "request-topic", groupId = "response-service-group")
    @SendTo
    public String handleRequest(String requestJson) throws Exception {
        logger.debug("Received request: {}", requestJson);
        RequestMessage request = objectMapper.readValue(requestJson, RequestMessage.class);
        
        logger.debug("Processing request with correlationId: {}", request.getCorrelationId());
        
        // Simuler un traitement
        String processedPayload = "Processed: " + request.getPayload();
        
        ResponseMessage response = new ResponseMessage(processedPayload, request.getCorrelationId());
        String responseJson = objectMapper.writeValueAsString(response);
        logger.debug("Sending response: {}", responseJson);
        
        return responseJson;
    }
}
