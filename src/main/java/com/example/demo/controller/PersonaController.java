package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.NUsuariosXRol;
import com.example.demo.model.Persona;
import com.example.demo.service.PersonaService;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public List<Persona> listar() {
        return personaService.listarTodas();
    }

    @PostMapping
    public Persona guardar(@RequestBody Persona persona) {
        return personaService.guardar(persona);
    }

    @GetMapping("/NUsuariosXRol")
    public ResponseEntity<?> getNUsuariosXRol() {
      List<NUsuariosXRol> listaNUsuariosXRol = personaService.getNUsuariosXRol();
       if (listaNUsuariosXRol.isEmpty()) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NUsuariosXRol no encontrado");
      }
      return ResponseEntity.ok(listaNUsuariosXRol);
    }
}