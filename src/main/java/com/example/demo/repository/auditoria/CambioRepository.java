package com.example.demo.repository.auditoria;

import com.example.demo.model.auditoria.Cambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CambioRepository extends JpaRepository<Cambio, Long> {
    List<Cambio> findByEventoIdEvento(Long idEvento);
}