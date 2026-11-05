package com.example.demo.service;

import com.example.demo.dto.AuthRequestDTO;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.model.Modulo;
import com.example.demo.repository.ModuloRepository;
import com.example.demo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ModuloRepository moduloRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;

   public AuthResponseDTO login(AuthRequestDTO request) {
    var usuario = usuarioRepository.findByEmail(request.getEmail().trim())
            .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + request.getEmail()));

    // Validar contraseña soportando texto plano Y encriptación BCrypt
    boolean passwordMatches = passwordEncoder.matches(request.getPassword(), usuario.getPassword()) 
            || request.getPassword().equals(usuario.getPassword());

    if (!passwordMatches) {
        throw new BadCredentialsException("Contraseña incorrecta");
    }

    // Continuar con la carga de módulos y generación del token...
    List<Modulo> modules = Collections.emptyList();
    if (usuario.getRol() != null && usuario.getRol().getIdRol() != null) {
        try {
            modules = moduloRepository.findByRolId(usuario.getRol().getIdRol());
            if (modules == null) {
                modules = Collections.emptyList();
            }
        } catch (Exception e) {
            modules = Collections.emptyList();
        }
    }

    var token = jwtService.generateToken(usuario, modules);

    AuthResponseDTO response = new AuthResponseDTO();
    response.setToken(token);
    return response;
 }
}