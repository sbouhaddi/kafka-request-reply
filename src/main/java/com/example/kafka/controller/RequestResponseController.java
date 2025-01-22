package com.example.kafka.controller;

import com.example.kafka.model.RequestMessage;
import com.example.kafka.model.ResponseMessage;
import com.example.kafka.service.KafkaRequestResponseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/request-response")
public class RequestResponseController {

    private final KafkaRequestResponseService requestResponseService;

    public RequestResponseController(KafkaRequestResponseService requestResponseService) {
        this.requestResponseService = requestResponseService;
    }

    @PostMapping
    public ResponseMessage sendRequest(@RequestBody String message) throws Exception {
        RequestMessage request = new RequestMessage(message, null);
        return requestResponseService.sendAndReceive(request);
    }
}
