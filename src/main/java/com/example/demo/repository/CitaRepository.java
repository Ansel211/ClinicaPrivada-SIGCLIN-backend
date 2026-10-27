package com.example.demo.repository;

import com.example.demo.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // 1. Obtener el historial de citas de un médico
    List<Cita> findByMedicoId(Long medicoId);

    // 2. Obtener la agenda del día para un médico específico
    List<Cita> findByMedicoIdAndFecha(Long medicoId, LocalDate fecha);

    // 3. Contar cuántas citas hay en total en un día (solicitado por CitaController)
    long countByFecha(LocalDate fecha);

    // 4. Validar disponibilidad: Evita que el médico tenga dos citas a la misma hora
    boolean existsByMedicoIdAndFechaAndHora(Long medicoId, LocalDate fecha, LocalTime hora);
}
