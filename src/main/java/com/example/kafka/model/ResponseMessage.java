package com.example.kafka.model;

public class ResponseMessage {
    private String payload;
    private String correlationId;
    private String error;

    public ResponseMessage() {}

    public ResponseMessage(String payload, String correlationId) {
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
