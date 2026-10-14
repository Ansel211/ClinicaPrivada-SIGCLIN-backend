package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Cita;
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    long countByFecha(LocalDate fecha);
    boolean existsByMedicoIdAndFechaAndHora(Long idMedico, LocalDate fecha, LocalTime hora);
}