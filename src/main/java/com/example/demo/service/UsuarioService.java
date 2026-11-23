package com.example.demo.service;

import com.example.demo.dto.UsuarioRequestDTO;
import com.example.demo.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> listarUsuarios();
    Usuario crearUsuario(UsuarioRequestDTO dto);
    Usuario actualizarUsuario(Long id, UsuarioRequestDTO dto);
    void eliminarUsuario(Long id);
}