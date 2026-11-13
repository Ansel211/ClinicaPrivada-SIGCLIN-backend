package com.example.demo.repository;

import com.example.demo.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {
    // Buscar el historial médico filtrado por el ID del paciente
    List<HistoriaClinica> findByPacienteIdPaciente(Long pacienteId);
}