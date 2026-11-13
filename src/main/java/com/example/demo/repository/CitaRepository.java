package com.example.demo.repository;

import com.example.demo.dto.CitasPorFechaDTO;
import com.example.demo.dto.NCitasXMedicoDTO;
import com.example.demo.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // 1. Obtener el historial de citas de un médico
    List<Cita> findByMedicoId(Long medicoId);

    // 2. Obtener la agenda del día para un médico específico
    List<Cita> findByMedicoIdAndFecha(Long medicoId, LocalDate fecha);
   
    //Para ver todas las citas en un rango de fechas
    List<Cita> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
   
    // Para ver las citas de un médico específico en un rango de fechas
    List<Cita> findByMedicoIdAndFechaBetween(Long idMedico, LocalDate fechaInicio, LocalDate fechaFin);

    // 3. Contar cuántas citas hay en total en un día (solicitado por CitaController)
    long countByFecha(LocalDate fecha);

    // 4. Validar disponibilidad: Evita que el médico tenga dos citas a la misma hora
    boolean existsByMedicoIdAndFechaAndHora(Long medicoId, LocalDate fecha, LocalTime hora);

    // 5. Consulta agrupada para Dashboard: Citas por Médico
    @Query("SELECT new com.example.demo.dto.NCitasXMedicoDTO(m.nombre, COUNT(c)) " +
           "FROM Cita c JOIN c.medico m " +
           "GROUP BY m.nombre")
    List<NCitasXMedicoDTO> obtenerNCitasPorMedico();
    
    // consulta para dashboard citas por fechaa
    @Query("SELECT new com.example.demo.dto.CitasPorFechaDTO(CAST(c.fecha AS string), COUNT(c)) " +
       "FROM Cita c GROUP BY c.fecha ORDER BY c.fecha ASC")
    List<CitasPorFechaDTO> obtenerCitasPorFecha();
    
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :idMedico AND c.fecha = :fecha")
    List<Cita> findCitasPorMedicoYFecha(@Param("idMedico") Long idMedico, @Param("fecha") LocalDate fecha);
}
