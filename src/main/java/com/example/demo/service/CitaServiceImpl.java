package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.model.Paciente; // <--- Importamos el modelo Paciente
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository; // <--- Importamos el repositorio de Paciente
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.CitaDTO;
import com.example.demo.dto.NCitasXMedicoDTO;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service 
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository; 

    @Override
    public Cita RegistrarCita(CitaDTO dto) {
      if (dto.getMedico() == null || dto.getMedico().getId() == null) {
        throw new IllegalArgumentException("Debe seleccionar un médico válido.");
      }

      // Validamos que se haya enviado un paciente con su ID
      if (dto.getPaciente() == null || dto.getPaciente().getIdPaciente() == null) {
        throw new IllegalArgumentException("Debe seleccionar un paciente válido.");
      }

      LocalDateTime fechaHoraPropuesta = LocalDateTime.of(dto.getFecha(), dto.getHora());
      if (fechaHoraPropuesta.isBefore(LocalDateTime.now())) {
          throw new IllegalArgumentException("No se puede registrar una cita en una fecha u hora pasada.");
      }

      // Buscar al medico en la bd
      Long medicoId = dto.getMedico().getId();
      Medico medico = medicoRepository.findById(medicoId)
              .orElseThrow(() -> new IllegalArgumentException("El médico seleccionado no existe en la base de datos."));

      // Buscar al paciente en la bd
      Long pacienteId = dto.getPaciente().getIdPaciente();
      Paciente paciente = pacienteRepository.findById(pacienteId)
              .orElseThrow(() -> new IllegalArgumentException("El paciente seleccionado no existe en la base de datos."));

      // Obtener la duracion enviada en el DTO, si no se envia, asumimos 30 minutos por defecto
      int duracionMinutos = (dto.getDuracionMinutos() != null) ? dto.getDuracionMinutos() : 30;
      
      LocalTime horaInicioNueva = dto.getHora();
      LocalTime horaFinNueva = horaInicioNueva.plusMinutes(duracionMinutos);

      //Buscar todas las citas del médico en la fecha especificada para validar conflictos de horario
      List<Cita> citasDelDia = citaRepository.findByMedicoIdAndFecha(medicoId, dto.getFecha());

      for (Cita citaExistente : citasDelDia) {
          LocalTime horaInicioExistente = citaExistente.getHora();
          int duracionExistente = (citaExistente.getDuracionMinutos() != null) ? citaExistente.getDuracionMinutos() : 30;
          LocalTime horaFinExistente = horaInicioExistente.plusMinutes(duracionExistente);

          boolean seCruzan = horaInicioNueva.isBefore(horaFinExistente) && horaFinNueva.isAfter(horaInicioExistente);
          
          if (seCruzan) {
              throw new IllegalArgumentException("El médico ya se encuentra ocupado en este horario (Conflicto con cita de " 
                  + horaInicioExistente + " a " + horaFinExistente + ").");
          }
      }

      // Guardamos la nueva cita en la base de datos
      Cita nuevaCita = new Cita();
      nuevaCita.setPaciente(paciente); 
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
    public List<NCitasXMedicoDTO> obtenerNCitasPorMedico() {
        return citaRepository.obtenerNCitasPorMedico();
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