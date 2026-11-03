package com.example.demo.controller;

import com.example.demo.model.Rol;
import com.example.demo.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        List<Rol> roles = rolRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Rol rol) {
        try {
            if (rol.getActivo() == null) {
                rol.setActivo(true);
            }
            Rol nuevoRol = rolRepository.save(rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}