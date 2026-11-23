package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.auditoria.AuditoriaService;
import com.example.demo.model.Persona;
import com.example.demo.repository.PersonaRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Override
    public List<Persona> listarTodas() {
        return personaRepository.findAll();
    }

    @Override
    @Transactional
    public Persona guardar(Persona persona) {
        boolean esNuevo = (persona.getId() == null);
        Persona anterior = (!esNuevo)
                ? personaRepository.findById(persona.getId()).map(Persona::new).orElse(null)
                : null;

        Persona guardada = personaRepository.saveAndFlush(persona);

        if (esNuevo || anterior == null) {
            auditoriaService.registrarCreacion("public", "persona", guardada.getId().toString(), "PERSONAS");
        } else {
            auditoriaService.registrarActualizacion(
                    "public", "persona", guardada.getId().toString(), "PERSONAS",
                    anterior, guardada
            );
        }

        return guardada;
    }
}