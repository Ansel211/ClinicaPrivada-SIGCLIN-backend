package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException; // Usando tu clase personalizada de la carpeta exception
import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.CitaDTO;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service 
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public Cita RegistrarCita(CitaDTO dto) {
      if (dto.getMedico() == null || dto.getMedico().getId() == null) {
        throw new IllegalArgumentException("Debe seleccionar un médico válido.");
      }

      LocalDateTime fechaHoraPropuesta = LocalDateTime.of(dto.getFecha(), dto.getHora());
      if (fechaHoraPropuesta.isBefore(LocalDateTime.now())) {
          throw new IllegalArgumentException("No se puede registrar una cita en una fecha u hora pasada.");
      }

      Long medicoId = dto.getMedico().getId();
      Medico medico = medicoRepository.findById(medicoId)
              .orElseThrow(() -> new IllegalArgumentException("El médico seleccionado no existe en la base de datos."));

      if ("ocupado".equalsIgnoreCase(medico.getDisponibilidad())) {
          throw new IllegalArgumentException("El médico se encuentra actualmente no disponible.");
      }

      boolean ocupado = citaRepository.existsByMedicoIdAndFechaAndHora(medicoId, dto.getFecha(), dto.getHora());
      if (ocupado) {
          throw new IllegalArgumentException("El médico ya tiene una cita programada para esa fecha y hora.");
      }

      Cita nuevaCita = new Cita();
      nuevaCita.setNombrePaciente(dto.getNombrePaciente());
      nuevaCita.setMedico(medico);
      nuevaCita.setFecha(dto.getFecha());
      nuevaCita.setHora(dto.getHora());
      nuevaCita.setEstado("pendiente");

      return citaRepository.save(nuevaCita);
    }

    @Override
    public Cita CambiarEstado(long id, String nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    @Override
    public List<Cita> ListarCitas() {
        return citaRepository.findAll();
    }

    @Override
    public List<Cita> ListarCitasPorMedico(long medicoId) {
        return citaRepository.findByMedicoId(medicoId);
    }
}