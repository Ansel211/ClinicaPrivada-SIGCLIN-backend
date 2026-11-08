package com.example.demo.repository;

import com.example.demo.dto.NUsuariosXRol;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    @Query(value = "SELECT r.descripcion AS descripcion, COUNT(u.id) AS cantidad " +
                   "FROM usuario u " +
                   "JOIN rol r ON u.rol_id = r.id_rol " +
                   "GROUP BY r.descripcion " +
                   "ORDER BY r.descripcion", nativeQuery = true)
    List<NUsuariosXRol> getNUsuariosXRol();
}