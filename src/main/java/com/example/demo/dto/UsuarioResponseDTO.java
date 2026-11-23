package com.example.demo.dto;

import com.example.demo.model.Persona;
import com.example.demo.model.Usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String email;
    private String username;
    private Boolean estado;
    private Persona persona;
    private RolResumenDTO rol;

    @Data
    @Builder
    public static class RolResumenDTO {
        private Integer idRol;
        private String codigo;
        private String descripcion;
    }

    public static UsuarioResponseDTO fromEntity(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .email(u.getEmail())
                .username(u.getUsername())
                .estado(u.getEstado())
                .persona(u.getPersona())
                .rol(u.getRol() != null ? RolResumenDTO.builder()
                        .idRol(u.getRol().getIdRol())
                        .codigo(u.getRol().getCodigo())
                        .descripcion(u.getRol().getDescripcion())
                        .build() : null)
                .build();
    }
}