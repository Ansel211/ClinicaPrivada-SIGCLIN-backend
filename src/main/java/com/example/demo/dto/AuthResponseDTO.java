package com.example.demo.dto;

public class AuthResponseDTO {
    private String token;
    private String email;
    private String nombreCompleto;
    private String message;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String email, String nombreCompleto, String message) {
        this.token = token;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}