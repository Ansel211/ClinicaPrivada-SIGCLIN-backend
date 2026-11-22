package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "historia_clinica")
@Getter // Genera automáticamente todos los getId(), getDiagnostico(), etc.
@Setter // Genera automáticamente todos los setId(), setDiagnostico(), etc.
@NoArgsConstructor // Genera el constructor vacío obligatorio para JPA: public HistoriaClinica() {}
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String anamnesis;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;
    
   
}