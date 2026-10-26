package com.example.demo.service;

import com.example.demo.dto.CitaDTO;
import com.example.demo.model.Cita;
import java.util.List;

public interface CitaService {
    Cita RegistrarCita(CitaDTO dto);
    Cita CambiarEstado(long id, String nuevoEstado);
    List<Cita> ListarCitas();
    List<Cita> ListarCitasPorMedico(long medicoId);
}