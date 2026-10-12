package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Medico;
import com.example.demo.repository.MedicoRepository;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*") // Permite la comunicación con Angular sin bloqueos CORS
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @GetMapping
    public List<Medico> listar() {
        return medicoRepository.findAll();
    }

    @PostMapping
    public Medico guardar(@RequestBody Medico medico) {
        return medicoRepository.save(medico);
    }
}