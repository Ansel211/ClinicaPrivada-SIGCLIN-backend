package com.example.demo.controller;

import com.example.demo.model.Modulo;
import com.example.demo.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modulos")
public class ModuloController {

    @Autowired
    private ModuloRepository moduloRepository;

    @GetMapping
    public ResponseEntity<List<Modulo>> listar() {
        List<Modulo> modulos = moduloRepository.findAll();
        return ResponseEntity.ok(modulos);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Modulo modulo) {
            if (modulo.getActivo() == null) {
                modulo.setActivo(true);
            }
            Modulo nuevoModulo = moduloRepository.save(modulo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoModulo);
    }
}