package com.example.demo.dto;

public class AuthResponseDTO {
    private String token;
    private String username;
    private String nombreCompleto;
    private String message;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String username, String nombreCompleto, String mensaje) {
        this.token = token;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}