package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.NUsuariosXRol;
import com.example.demo.model.Persona;
import com.example.demo.repository.PersonaRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class PersonaServiceImpl implements PersonaService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public List<Persona> listarTodas() {
        return personaRepository.findAll();
    }

    @Override
    public Persona guardar(Persona persona) {
        return personaRepository.save(persona);
    }
    
    @Override
    public List<NUsuariosXRol> getNUsuariosXRol() {
        return usuarioRepository.getNUsuariosXRol();
    }
}