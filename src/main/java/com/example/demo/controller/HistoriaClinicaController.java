package com.example.demo.controller;

import com.example.demo.model.HistoriaClinica;
import com.example.demo.repository.HistoriaClinicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historias-clinicas")
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;

    // Listar todas las historias clínicas
    @GetMapping
    public List<HistoriaClinica> listarTodas() {
        return historiaClinicaRepository.findAll();
    }

    // Listar historial clínico de un paciente específico por su ID
    @GetMapping("/paciente/{idPaciente}")
    public List<HistoriaClinica> listarPorPaciente(@PathVariable Long idPaciente) {
        return historiaClinicaRepository.findByPacienteIdPaciente(idPaciente);
    }

    // Registrar una nueva consulta / evolución médica
    @PostMapping
    public ResponseEntity<?> registrarHistoria(@RequestBody HistoriaClinica historiaClinica) {
        try {
            HistoriaClinica nueva = historiaClinicaRepository.save(historiaClinica);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar la historia clínica: " + e.getMessage());
        }
    }
}