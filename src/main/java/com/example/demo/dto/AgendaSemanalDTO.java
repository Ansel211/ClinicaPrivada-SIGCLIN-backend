package com.example.demo.dto;

import java.util.List;

public class AgendaSemanalDTO {
    private List<TurnoDTO> turnos;
    private List<CitaBloqueDTO> citas;

    public AgendaSemanalDTO(List<TurnoDTO> turnos, List<CitaBloqueDTO> citas) {
        this.turnos = turnos;
        this.citas = citas;
    }

    public List<TurnoDTO> getTurnos() { return turnos; }
    public void setTurnos(List<TurnoDTO> turnos) { this.turnos = turnos; }

    public List<CitaBloqueDTO> getCitas() { return citas; }
    public void setCitas(List<CitaBloqueDTO> citas) { this.citas = citas; }

    public static class TurnoDTO {
        private Integer diaSemana; // 1=Lunes ... 7=Domingo
        private String horaInicio; // "HH:mm"
        private String horaFin;    // "HH:mm"

        public TurnoDTO(Integer diaSemana, String horaInicio, String horaFin) {
            this.diaSemana = diaSemana;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }

        public Integer getDiaSemana() { return diaSemana; }
        public String getHoraInicio() { return horaInicio; }
        public String getHoraFin() { return horaFin; }
    }

    public static class CitaBloqueDTO {
        private Long id;
        private String fecha;      // "yyyy-MM-dd"
        private String horaInicio; // "HH:mm"
        private Integer duracionMinutos;
        private String paciente;
        private String tipoConsulta;
        private String estado;

        public CitaBloqueDTO(Long id, String fecha, String horaInicio, Integer duracionMinutos,
                             String paciente, String tipoConsulta, String estado) {
            this.id = id;
            this.fecha = fecha;
            this.horaInicio = horaInicio;
            this.duracionMinutos = duracionMinutos;
            this.paciente = paciente;
            this.tipoConsulta = tipoConsulta;
            this.estado = estado;
        }

        public Long getId() { return id; }
        public String getFecha() { return fecha; }
        public String getHoraInicio() { return horaInicio; }
        public Integer getDuracionMinutos() { return duracionMinutos; }
        public String getPaciente() { return paciente; }
        public String getTipoConsulta() { return tipoConsulta; }
        public String getEstado() { return estado; }
    }
}