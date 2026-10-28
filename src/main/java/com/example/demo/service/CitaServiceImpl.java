package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.CitaDTO;
import java.time.LocalDateTime;
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

      // 1. Obtener la duración enviada desde el frontend (si viene nula, por defecto 30 minutos)
      int duracionMinutos = (dto.getDuracionMinutos() != null) ? dto.getDuracionMinutos() : 30;
      
      LocalTime horaInicioNueva = dto.getHora();
      LocalTime horaFinNueva = horaInicioNueva.plusMinutes(duracionMinutos);

      // 2. Buscar todas las citas de ese médico en esa misma fecha
      List<Cita> citasDelDia = citaRepository.findByMedicoIdAndFecha(medicoId, dto.getFecha());

      // 3. Validar si hay cruce de horarios con la duración dinámica
      for (Cita citaExistente : citasDelDia) {
          LocalTime horaInicioExistente = citaExistente.getHora();
          int duracionExistente = (citaExistente.getDuracionMinutos() != null) ? citaExistente.getDuracionMinutos() : 30;
          LocalTime horaFinExistente = horaInicioExistente.plusMinutes(duracionExistente);

          // Condición matemática de cruce de intervalos: (InicioA < FinB) y (FinA > InicioB)
          boolean seCruzan = horaInicioNueva.isBefore(horaFinExistente) && horaFinNueva.isAfter(horaInicioExistente);
          
          if (seCruzan) {
              throw new IllegalArgumentException("El médico ya se encuentra ocupado en este horario (Conflicto con cita de " 
                  + horaInicioExistente + " a " + horaFinExistente + ").");
          }
      }

      // Guardamos la cita con su duracion y el tipo de consulta personalizado
      Cita nuevaCita = new Cita();
      nuevaCita.setNombrePaciente(dto.getNombrePaciente());
      nuevaCita.setMedico(medico);
      nuevaCita.setFecha(dto.getFecha());
      nuevaCita.setHora(dto.getHora());
      nuevaCita.setDuracionMinutos(duracionMinutos);
      nuevaCita.setTipoConsulta(dto.getTipoConsulta()); 
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