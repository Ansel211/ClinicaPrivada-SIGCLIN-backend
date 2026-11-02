package com.example.demo.dto;

import com.example.demo.model.Modulo;
import java.util.Set;

public class AuthResponseDTO {
    private String token;
    private String email;
    private String nombreCompleto;
    private String message;
    private String rolCodigo;
    private Set<Modulo> modulos;

    public AuthResponseDTO() {}

    // Constructor exacto con los 6 parámetros en orden
    public AuthResponseDTO(String token, String email, String nombreCompleto, String message, String rolCodigo, Set<Modulo> modulos) {
        this.token = token;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.message = message;
        this.rolCodigo = rolCodigo;
        this.modulos = modulos;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }

    public Set<Modulo> getModulos() { return modulos; }
    public void setModulos(Set<Modulo> modulos) { this.modulos = modulos; }
}