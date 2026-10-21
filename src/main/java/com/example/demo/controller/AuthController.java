package com.example.demo.controller;

import com.example.demo.dto.AuthRequestDTO;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), usuario.getPassword())
                    || request.getPassword().equals(usuario.getPassword());

            if (passwordMatches) {
                String nombreCompleto = (usuario.getPersona() != null)
                        ? usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido()
                        : usuario.getUsername();

                // Generación de token JWT real enviando solo tu objeto usuario
                String tokenReal = jwtUtil.generateToken(usuario);

                AuthResponseDTO response = new AuthResponseDTO(
                        tokenReal,
                        usuario.getUsername(),
                        nombreCompleto,
                        "Inicio de sesión exitoso"
                );

                return ResponseEntity.ok(response);
            }
        }

        throw new BadCredentialsException("Credenciales incorrectas");
    }
}