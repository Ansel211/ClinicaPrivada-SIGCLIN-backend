package com.example.demo.repository;

import com.example.demo.model.HorarioMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Long> {
    List<HorarioMedico> findByMedicoId(Long idMedico);
    List<HorarioMedico> findByMedicoIdAndDiaSemana(Long idMedico, Integer diaSemana);
}