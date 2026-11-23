package com.example.demo.controller;

import com.example.demo.dto.UsuarioRequestDTO;
import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
    List<UsuarioResponseDTO> lista = usuarioService.listarUsuarios()
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = usuarioService.crearUsuario(dto);
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}