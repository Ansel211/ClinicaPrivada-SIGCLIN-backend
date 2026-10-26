package com.example.demo.repository;

import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // 1. Método usando el objeto Medico (sin anotación @Query)
    boolean existsByMedicoAndFechaAndHora(Medico medico, LocalDate fecha, LocalTime hora);

    // 2. Método usando el id de Medico por convención de propiedad (medico.id)
    boolean existsByMedicoIdAndFechaAndHora(Long medicoId, LocalDate fecha, LocalTime hora);

    // 3. Método para buscar citas por el ID del médico
    List<Cita> findByMedicoId(Long medicoId);

    // 4. Método para contar citas por fecha (lo solicita CitaController.java)
    long countByFecha(LocalDate fecha);
}