package com.example.demo.util;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String timestamp = LocalDateTime.now().toString();
    private Integer status;
    private String error;
    private String message;
    private String detail;

    public ErrorResponse() {}

    public ErrorResponse(Integer status, String error, String message, String detail) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.detail = detail;
    }

    // Getters y Setters
    public String getTimestamp() { return timestamp; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}