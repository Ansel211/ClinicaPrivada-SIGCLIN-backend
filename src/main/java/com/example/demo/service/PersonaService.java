package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Persona;

public interface PersonaService {
    List<Persona> listarTodas();
    Persona guardar(Persona persona);
}