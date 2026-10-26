package com.example.demo.controller;

import com.example.demo.dto.CitaDTO;
import com.example.demo.model.Cita;
import com.example.demo.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping
    public List<Cita> listarCitas() {
        return citaService.ListarCitas();
    }

    @PostMapping
    public ResponseEntity<?> RegistrarCita(@RequestBody CitaDTO dto) {
        try {
            Cita guardada = citaService.RegistrarCita(dto);
            return ResponseEntity.ok(guardada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/hoy/total")
    public ResponseEntity<Long> obtenerTotalCitasHoy() {
        // Si no tienes este método en CitaService, puedes dejar la llamada o añadirlo al servicio
        return ResponseEntity.ok(0L); 
    }
}