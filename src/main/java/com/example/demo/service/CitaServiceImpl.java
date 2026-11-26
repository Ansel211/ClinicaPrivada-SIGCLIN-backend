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
import com.example.demo.dto.AgendaSemanalDTO;
import com.example.demo.model.HorarioMedico;
import com.example.demo.repository.HorarioMedicoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;

@Service 
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository; 

    @Autowired
    private HorarioMedicoRepository horarioMedicoRepository;

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
public List<String> obtenerCuposLibres(Long idMedico, String fecha, int duracionMinutos) {
    LocalDate fechaCita = LocalDate.parse(fecha);
    int diaSemana = fechaCita.getDayOfWeek().getValue();

    List<HorarioMedico> horarios = horarioMedicoRepository.findByMedicoIdAndDiaSemana(idMedico, diaSemana);
    if (horarios.isEmpty()) {
        return Collections.emptyList();
    }

    HorarioMedico turno = horarios.get(0);
    List<Cita> citasDelDia = citaRepository.findCitasPorMedicoYFecha(idMedico, fechaCita);

    List<String> cuposDisponibles = new ArrayList<>();
    LocalTime horaEvaluada = turno.getHoraInicio();
    LocalTime horaFinTurno = turno.getHoraFin();

    int iteracion = 0;
    while (iteracion < 60) {
        LocalTime finPosible = horaEvaluada.plusMinutes(duracionMinutos);
        if (finPosible.isAfter(horaFinTurno)) {
            break;
        }

        boolean choca = false;
        for (Cita c : citasDelDia) {
            if (c.getHora() == null) continue;

            LocalTime cInicio = c.getHora();
            int duracionCita = (c.getDuracionMinutos() != null) ? c.getDuracionMinutos() : 30;
            LocalTime cFin = cInicio.plusMinutes(duracionCita);

            if (horaEvaluada.isBefore(cFin) && finPosible.isAfter(cInicio)) {
                choca = true;
                break;
            }
        }

        if (!choca) {
            String cupoTexto = horaEvaluada.toString();
            if (cupoTexto.length() > 5) {
                cupoTexto = cupoTexto.substring(0, 5);
            }
            cuposDisponibles.add(cupoTexto);
        }

        horaEvaluada = horaEvaluada.plusMinutes(15);
        iteracion++;
    }

    return cuposDisponibles;
}

@Override
public AgendaSemanalDTO obtenerAgendaSemanal(Long idMedico, LocalDate fechaInicio, LocalDate fechaFin) {
    List<AgendaSemanalDTO.TurnoDTO> turnos = horarioMedicoRepository.findByMedicoId(idMedico)
            .stream()
            .map(h -> new AgendaSemanalDTO.TurnoDTO(
                    h.getDiaSemana(),
                    h.getHoraInicio().toString().substring(0, 5),
                    h.getHoraFin().toString().substring(0, 5)
            ))
            .collect(Collectors.toList());

    List<AgendaSemanalDTO.CitaBloqueDTO> citas = citaRepository
            .findByMedicoIdAndFechaBetween(idMedico, fechaInicio, fechaFin)
            .stream()
            .filter(c -> c.getEstado() == null || !c.getEstado().equalsIgnoreCase("cancelada"))
            .map(c -> {
                String nombrePac = c.getNombrePaciente();
                if ((nombrePac == null || nombrePac.isBlank()) && c.getPaciente() != null) {
                    String n = c.getPaciente().getNombres() != null ? c.getPaciente().getNombres() : "";
                    String a = c.getPaciente().getApellidoPaterno() != null ? c.getPaciente().getApellidoPaterno() : "";
                    nombrePac = (n + " " + a).trim();
                }
                if (nombrePac == null || nombrePac.isBlank()) {
                    nombrePac = "Paciente reservado";
                }

                return new AgendaSemanalDTO.CitaBloqueDTO(
                        c.getId(),
                        c.getFecha().toString(),
                        c.getHora().toString().substring(0, 5),
                        c.getDuracionMinutos() != null ? c.getDuracionMinutos() : 30,
                        nombrePac,
                        c.getTipoConsulta() != null ? c.getTipoConsulta() : "Consulta",
                        c.getEstado()
                );
            })
            .collect(Collectors.toList());

    return new AgendaSemanalDTO(turnos, citas);
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