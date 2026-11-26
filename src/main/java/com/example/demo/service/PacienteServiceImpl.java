package com.example.demo.service;

import com.example.demo.auditoria.AuditoriaService;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Override
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    @Override
    @Transactional
    public Paciente registrarPaciente(Paciente paciente) {
        Paciente nuevoPaciente = pacienteRepository.saveAndFlush(paciente);
        auditoriaService.registrarCreacion(
                "public",
                "paciente",
                nuevoPaciente.getIdPaciente().toString(),
                "PACIENTES"
        );
        return nuevoPaciente;
    }

    @Override
    @Transactional
    public Paciente actualizarPaciente(Long id, Paciente pacienteDetalles) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));

        Paciente anterior = new Paciente();
        anterior.setIdPaciente(paciente.getIdPaciente());
        anterior.setNombres(paciente.getNombres());
        anterior.setApellidoPaterno(paciente.getApellidoPaterno());
        anterior.setApellidoMaterno(paciente.getApellidoMaterno());
        anterior.setNumDocumento(paciente.getNumDocumento());
        anterior.setTelefono(paciente.getTelefono());
        anterior.setDireccion(paciente.getDireccion());
        anterior.setFechaNacimiento(paciente.getFechaNacimiento());

        paciente.setNombres(pacienteDetalles.getNombres());
        paciente.setApellidoPaterno(pacienteDetalles.getApellidoPaterno());
        paciente.setApellidoMaterno(pacienteDetalles.getApellidoMaterno());
        paciente.setNumDocumento(pacienteDetalles.getNumDocumento());
        paciente.setTelefono(pacienteDetalles.getTelefono());
        paciente.setDireccion(pacienteDetalles.getDireccion());
        paciente.setFechaNacimiento(pacienteDetalles.getFechaNacimiento());

        Paciente actualizado = pacienteRepository.saveAndFlush(paciente);

        auditoriaService.registrarActualizacion(
                "public",
                "paciente",
                actualizado.getIdPaciente().toString(),
                "PACIENTES",
                anterior,
                actualizado
        );

        return actualizado;
    }

    @Override
    @Transactional
    public void eliminarPaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));

        pacienteRepository.delete(paciente);
        auditoriaService.registrarEliminacion(
                "public",
                "paciente",
                id.toString(),
                "PACIENTES",
                paciente
        );
    }
}