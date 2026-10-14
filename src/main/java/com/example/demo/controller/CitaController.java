package com.example.demo.controller;

import com.example.demo.dto.CitaDTO;
import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @GetMapping
    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> registrarCita(@RequestBody CitaDTO dto) {
        Medico medico = medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        Cita nuevaCita = new Cita();
        nuevaCita.setNombrePaciente(dto.getNombrePaciente());
        nuevaCita.setMedico(medico);
        nuevaCita.setFecha(dto.getFecha());
        nuevaCita.setHora(dto.getHora());

        Cita guardada = citaRepository.save(nuevaCita);
        return ResponseEntity.ok(guardada);
    }
}