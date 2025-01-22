package com.example.kafka.model;

public class RequestMessage {
    private String payload;
    private String correlationId;

    public RequestMessage() {}

    public RequestMessage(String payload, String correlationId) {
        this.payload = payload;
        this.correlationId = correlationId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
