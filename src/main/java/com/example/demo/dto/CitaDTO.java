package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaDTO {

    private String nombrePaciente;
    private MedicoDTO medico;
    private LocalDate fecha;
    private LocalTime hora;
    private Integer duracionMinutos;
    private String tipoConsulta; 
    private PacienteDTO paciente; 

    public static class PacienteDTO {
        private Long idPaciente;
        public Long getIdPaciente() { return idPaciente; }
        public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }
    }

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

    public String getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(String tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public PacienteDTO getPaciente() { return paciente; } // <--- Agregado
    public void setPaciente(PacienteDTO paciente) { this.paciente = paciente; } // <--- Agregado
}