package com.example.demo.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    // Datos de Persona
    private String nombre;
    private String apellido;
    private String numeroDocumento;
    private String correo;

    // Datos de Usuario
    private String email;
    private String username;
    private String password; 
    private Boolean estado;
    private Integer rolId;
}