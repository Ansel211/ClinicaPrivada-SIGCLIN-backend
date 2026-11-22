package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "modulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mod")
    private Integer idMod;

    @Column(nullable = false, unique = true)
    private String descripcion;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "orden_opcion")
    private Integer ordenOpcion;
}