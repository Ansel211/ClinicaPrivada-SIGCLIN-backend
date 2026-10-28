package com.example.demo.controller;

import com.example.demo.model.Paciente;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*") // Permite la comunicación con Angular
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
}