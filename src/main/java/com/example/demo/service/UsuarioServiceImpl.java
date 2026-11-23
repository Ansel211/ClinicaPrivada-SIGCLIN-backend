package com.example.demo.service;

import com.example.demo.auditoria.AuditoriaService;
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
    private final AuditoriaService auditoriaService;

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
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        if (personaRepository.existsByNumeroDocumento(dto.getNumeroDocumento().trim())) {
            throw new IllegalArgumentException("El número de documento ya está registrado.");
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));

        Persona persona = new Persona();
        persona.setNombre(dto.getNombre().trim());
        persona.setApellido(dto.getApellido().trim());
        persona.setNumeroDocumento(dto.getNumeroDocumento().trim());
        persona.setCorreo(dto.getEmail().trim());
        Persona personaGuardada = personaRepository.saveAndFlush(persona);

        // Auditoría INSERT en persona
        auditoriaService.registrarCreacion("public", "persona", personaGuardada.getId().toString(), "USUARIOS");

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail().trim());
        usuario.setUsername(dto.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        usuario.setPersona(personaGuardada);
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepository.saveAndFlush(usuario);

        // Auditoría INSERT en usuario
        auditoriaService.registrarCreacion("public", "usuario", usuarioGuardado.getId().toString(), "USUARIOS");

        return usuarioGuardado;
    }

    @Override
    @Transactional
    public Usuario actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // 1. Capturar snapshot anterior de Usuario ANTES de modificar
        Usuario usuarioAnterior = new Usuario(usuario);

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));

        Persona persona = usuario.getPersona();
        Persona personaAnterior = (persona != null) ? new Persona(persona) : null;

        if (persona == null) {
            persona = new Persona();
        }
        persona.setNombre(dto.getNombre().trim());
        persona.setApellido(dto.getApellido().trim());
        persona.setNumeroDocumento(dto.getNumeroDocumento().trim());
        persona.setCorreo(dto.getEmail().trim());
        Persona personaGuardada = personaRepository.saveAndFlush(persona);

        // Auditoría Persona (UPDATE si ya existía, INSERT si se creó nueva)
        if (personaAnterior != null) {
            auditoriaService.registrarActualizacion(
                    "public", "persona", personaGuardada.getId().toString(), "USUARIOS",
                    personaAnterior, personaGuardada
            );
        } else {
            auditoriaService.registrarCreacion("public", "persona", personaGuardada.getId().toString(), "USUARIOS");
        }

        usuario.setPersona(personaGuardada);
        usuario.setEmail(dto.getEmail().trim());
        usuario.setUsername(dto.getUsername().trim());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());
        }
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepository.saveAndFlush(usuario);

        // Auditoría UPDATE en usuario (compara campo a campo vía reflexión)
        auditoriaService.registrarActualizacion(
                "public", "usuario", usuarioGuardado.getId().toString(), "USUARIOS",
                usuarioAnterior, usuarioGuardado
        );

        return usuarioGuardado;
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Snapshot anterior antes del borrado lógico
        Usuario usuarioAnterior = new Usuario(usuario);

        usuario.setEstado(false); // Borrado lógico para mantener integridad referencial
        Usuario usuarioGuardado = usuarioRepository.saveAndFlush(usuario);

        // Se registra como UPDATE (estado: true -> false) o DELETE según prefieras;
        // registrarActualizacion guarda exactamente el cambio estado: true -> false en auditoria.cambio
        auditoriaService.registrarActualizacion(
                "public", "usuario", usuarioGuardado.getId().toString(), "USUARIOS",
                usuarioAnterior, usuarioGuardado
        );
    }
}