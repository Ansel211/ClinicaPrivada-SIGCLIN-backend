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

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Rol rolDetalles) {
        return rolRepository.findById(id).map(rol -> {
            rol.setCodigo(rolDetalles.getCodigo());
            rol.setDescripcion(rolDetalles.getDescripcion());
            if (rolDetalles.getActivo() != null) {
                rol.setActivo(rolDetalles.getActivo());
            }
            // Actualiza automáticamente la tabla intermedia rol_modulo
            rol.setModulos(rolDetalles.getModulos());
            Rol actualizado = rolRepository.save(rol);
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!rolRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            rolRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No se puede eliminar el rol porque tiene usuarios asignados.");
        }
    }
}