package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> registrarPaciente(@RequestBody Paciente paciente) {
        try {
            Paciente nuevoPaciente = pacienteRepository.save(paciente);
            return ResponseEntity.ok(nuevoPaciente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar el paciente: " + e.getMessage());
        }
    }

    // <-- ENDPOINT PARA ACTUALIZAR -->
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente pacienteDetalles) {
        return pacienteRepository.findById(id).map(paciente -> {
            paciente.setNombres(pacienteDetalles.getNombres());
            paciente.setApellidoPaterno(pacienteDetalles.getApellidoPaterno());
            paciente.setApellidoMaterno(pacienteDetalles.getApellidoMaterno());
            paciente.setNumDocumento(pacienteDetalles.getNumDocumento());
            paciente.setTelefono(pacienteDetalles.getTelefono());
            paciente.setDireccion(pacienteDetalles.getDireccion());
            paciente.setFechaNacimiento(pacienteDetalles.getFechaNacimiento());
            Paciente actualizado = pacienteRepository.save(paciente);
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
public ResponseEntity<?> eliminarPaciente(@PathVariable Long id) {
    if (!pacienteRepository.existsById(id)) {
        return ResponseEntity.notFound().build();
    }
    try {
        pacienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("No se puede eliminar el paciente porque tiene citas asociadas.");
    }
}

}