package com.example.demo.repository;

import com.example.demo.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Integer> {

    @Query(value = "SELECT m.* FROM modulo m INNER JOIN rol_modulo rm ON m.id_mod = rm.modulo_id WHERE rm.rol_id = :rolId", nativeQuery = true)
    List<Modulo> findByRolId(@Param("rolId") Integer rolId);
}