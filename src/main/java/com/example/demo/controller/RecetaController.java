package com.example.demo.controller;

import com.example.demo.model.DetalleReceta;
import com.example.demo.model.RecetaMedica;
import com.example.demo.repository.RecetaMedicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    @Autowired
    private RecetaMedicaRepository recetaRepository;

    @GetMapping
    public List<RecetaMedica> listarTodas() {
        return recetaRepository.findAll();
    }

    @GetMapping("/paciente/{idPaciente}")
    public List<RecetaMedica> listarPorPaciente(@PathVariable Long idPaciente) {
        return recetaRepository.findByPacienteIdPacienteOrderByFechaEmisionDesc(idPaciente);
    }

    @PostMapping
    public ResponseEntity<?> emitirReceta(@RequestBody RecetaMedica receta) {
        try {
            if (receta.getFechaEmision() == null) {
                receta.setFechaEmision(LocalDate.now());
            }
            receta.setFechaFirma(LocalDateTime.now());
            receta.setFirmada(true);
            receta.setCodigoValidacion(generarFirmaDigital(receta));

            if (receta.getDetalles() != null) {
                for (DetalleReceta detalle : receta.getDetalles()) {
                    detalle.setReceta(receta);
                }
            }

            RecetaMedica guardada = recetaRepository.save(receta);
            return ResponseEntity.ok(guardada);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al emitir receta: " + e.getMessage());
        }
    }

    private String generarFirmaDigital(RecetaMedica receta) {
        try {
            String semilla = receta.getPaciente().getIdPaciente() + "-"
                    + receta.getMedico().getId() + "-"
                    + receta.getDiagnostico() + "-"
                    + System.nanoTime();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(semilla.getBytes(StandardCharsets.UTF_8));
            String hex = HexFormat.of().formatHex(hash).substring(0, 12).toUpperCase();
            return "SIGCLIN-REC-" + LocalDate.now().getYear() + "-" + hex;
        } catch (Exception e) {
            return "SIGCLIN-REC-" + System.currentTimeMillis();
        }
    }
}