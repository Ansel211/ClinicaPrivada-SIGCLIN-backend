package com.example.demo.service;

import com.example.demo.dto.CitaCalendarioDTO;
import com.example.demo.dto.CitaDTO;
import com.example.demo.dto.NCitasXMedicoDTO;
import com.example.demo.dto.CitasPorFechaDTO;
import com.example.demo.model.Cita;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {
    Cita RegistrarCita(CitaDTO dto);
    Cita CambiarEstado(long id, String nuevoEstado);
    List<Cita> ListarCitas();
    List<Cita> ListarCitasPorMedico(long medicoId);
    List<NCitasXMedicoDTO> obtenerNCitasPorMedico();
    List<CitaCalendarioDTO> obtenerCitasCalendario(Long idMedico, LocalDate fechaInicio, LocalDate fechaFin);
    List<CitasPorFechaDTO> obtenerCitasPorFecha();
}