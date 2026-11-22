package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_paciente")
    private String nombrePaciente;

    @Column(name = "tipo_consulta")
    private String tipoConsulta;

    @ManyToOne
    @JoinColumn(name = "id_medico")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "id_paciente")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Paciente paciente;

    private LocalDate fecha;
    private LocalTime hora;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos = 30;

    private String estado = "pendiente";
}