package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Cita;
import com.example.demo.model.Medico;
import com.example.demo.model.Paciente;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.MedicoRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.CitaDTO;
import com.example.demo.dto.CitasPorFechaDTO;
import com.example.demo.dto.NCitasXMedicoDTO;
import com.example.demo.dto.CitaCalendarioDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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

      if (dto.getPaciente() == null || dto.getPaciente().getIdPaciente() == null) {
        throw new IllegalArgumentException("Debe seleccionar un paciente válido.");
      }

      LocalDateTime fechaHoraPropuesta = LocalDateTime.of(dto.getFecha(), dto.getHora());
      if (fechaHoraPropuesta.isBefore(LocalDateTime.now())) {
          throw new IllegalArgumentException("No se puede registrar una cita en una fecha u hora pasada.");
      }

      Long medicoId = dto.getMedico().getId();
      Medico medico = medicoRepository.findById(medicoId)
              .orElseThrow(() -> new IllegalArgumentException("El médico seleccionado no existe en la base de datos."));

      Long pacienteId = dto.getPaciente().getIdPaciente();
      Paciente paciente = pacienteRepository.findById(pacienteId)
              .orElseThrow(() -> new IllegalArgumentException("El paciente seleccionado no existe en la base de datos."));

      int duracionMinutos = (dto.getDuracionMinutos() != null) ? dto.getDuracionMinutos() : 30;
      
      LocalTime horaInicioNueva = dto.getHora();
      LocalTime horaFinNueva = horaInicioNueva.plusMinutes(duracionMinutos);

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
    public List<CitasPorFechaDTO> obtenerCitasPorFecha() {
    return citaRepository.obtenerCitasPorFecha();
    }

    @Override
    public List<Cita> ListarCitas() {
        return citaRepository.findAll();
    }

    @Override
    public List<Cita> ListarCitasPorMedico(long medicoId) {
        return citaRepository.findByMedicoId(medicoId);
    }

    // --- NUEVO MÉTODO PARA EL CALENDARIO ---
    @Override
    public List<CitaCalendarioDTO> obtenerCitasCalendario(Long idMedico, LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) fechaInicio = LocalDate.now().minusMonths(1);
        if (fechaFin == null) fechaFin = LocalDate.now().plusMonths(3);

        List<Cita> citas;
        if (idMedico != null) {
            citas = citaRepository.findByMedicoIdAndFechaBetween(idMedico, fechaInicio, fechaFin);
        } else {
            citas = citaRepository.findByFechaBetween(fechaInicio, fechaFin);
        }

        return citas.stream().map(cita -> {
            LocalDateTime startDateTime = LocalDateTime.of(cita.getFecha(), cita.getHora());
            int duracion = cita.getDuracionMinutos() != null ? cita.getDuracionMinutos() : 30;
            LocalDateTime endDateTime = startDateTime.plusMinutes(duracion);

            String nombrePacienteStr = (cita.getPaciente() != null && cita.getPaciente().getNombres() != null)
                ? cita.getPaciente().getNombres() + " " + (cita.getPaciente().getApellidoPaterno() != null ? cita.getPaciente().getApellidoPaterno() : "")
                : (cita.getNombrePaciente() != null ? cita.getNombrePaciente() : "Paciente sin nombre");

            String nombreMedicoStr = (cita.getMedico() != null && cita.getMedico().getNombre() != null)
                ? "Dr. " + cita.getMedico().getNombre()
                : "Médico General";

            String title = "Cita: " + nombrePacienteStr.trim() + " (" + (cita.getTipoConsulta() != null ? cita.getTipoConsulta() : "General") + ")";

            return new CitaCalendarioDTO(
                cita.getId(),
                title,
                startDateTime,
                endDateTime,
                cita.getEstado(),
                cita.getMedico() != null ? cita.getMedico().getId() : null,
                nombreMedicoStr,
                nombrePacienteStr.trim()
            );
        }).collect(Collectors.toList());
    }
}