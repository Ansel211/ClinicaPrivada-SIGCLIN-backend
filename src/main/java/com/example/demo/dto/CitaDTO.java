package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaDTO {

    private String nombrePaciente;
    private MedicoDTO medico;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer duracionMinutos; // <-- ¡Este campo faltaba!

    // Clase interna para mapear { "id": X }
    public static class MedicoDTO {
        private Long id;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    // Getters y Setters
    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public MedicoDTO getMedico() { return medico; }
    public void setMedico(MedicoDTO medico) { this.medico = medico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
}