package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicos", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "especialidad", nullable = false)
    private String especialidad;

    @Column(name = "disponibilidad", nullable = false)
    private String disponibilidad;

    public Medico(String nombre, String especialidad, String disponibilidad) {
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.disponibilidad = disponibilidad;
    }
}