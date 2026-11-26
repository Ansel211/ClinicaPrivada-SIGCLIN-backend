package com.example.demo.service;

import com.example.demo.model.Paciente;
import java.util.List;

public interface PacienteService {
    List<Paciente> listarPacientes();
    Paciente registrarPaciente(Paciente paciente);
    Paciente actualizarPaciente(Long id, Paciente pacienteDetalles);
    void eliminarPaciente(Long id);
}