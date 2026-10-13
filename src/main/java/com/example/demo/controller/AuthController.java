package com.example.demo.controller;

import com.example.demo.dto.AuthRequestDTO;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;



@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Valida coincidencia contra BCrypt o en texto plano para desarrollo
            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), usuario.getPassword())
                                   || request.getPassword().equals(usuario.getPassword());

            if (passwordMatches) {
                String nombreCompleto = (usuario.getPersona() != null)
                    ? usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido()
                    : usuario.getUsername();

                AuthResponseDTO response = new AuthResponseDTO(
                    "dummy-jwt-token-sigclin",
                    usuario.getUsername(),
                    nombreCompleto,
                    "Inicio de sesión exitoso"
                );

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }
}
