package com.example.demo.service;

import com.example.demo.dto.UsuarioRequestDTO;
import com.example.demo.model.Persona;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PersonaRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario crearUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail().trim())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        if (usuarioRepository.existsByUsername(dto.getUsername().trim())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));

        Persona persona = new Persona();
        persona.setNombre(dto.getNombre().trim());
        persona.setApellido(dto.getApellido().trim());
        persona.setNumeroDocumento(dto.getNumeroDocumento().trim());
        persona.setCorreo(dto.getEmail().trim());
        Persona personaGuardada = personaRepository.save(persona);

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail().trim());
        usuario.setUsername(dto.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        usuario.setPersona(personaGuardada);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));

        Persona persona = usuario.getPersona();
        if (persona == null) {
            persona = new Persona();
        }
        persona.setNombre(dto.getNombre().trim());
        persona.setApellido(dto.getApellido().trim());
        persona.setNumeroDocumento(dto.getNumeroDocumento().trim());
        persona.setCorreo(dto.getEmail().trim());
        usuario.setPersona(personaRepository.save(persona));

        usuario.setEmail(dto.getEmail().trim());
        usuario.setUsername(dto.getUsername().trim());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());
        }
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        usuario.setEstado(false); // Borrado lógico recomendado para mantener integridad referencial
        usuarioRepository.save(usuario);
    }
}