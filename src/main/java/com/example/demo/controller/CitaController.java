package com.example.demo.controller;

import com.example.demo.dto.CitaCalendarioDTO;
import com.example.demo.dto.CitaDTO;
import com.example.demo.dto.CitasPorFechaDTO;
import com.example.demo.dto.NCitasXMedicoDTO;
import com.example.demo.model.Cita;
import com.example.demo.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;
    @Autowired
    private com.example.demo.repository.HorarioMedicoRepository horarioRepository;
    @Autowired
    private com.example.demo.repository.CitaRepository citaRepository; // Asegúrate de tenerlo inyectado

    @GetMapping
    public List<Cita> listarCitas() {
        return citaService.ListarCitas();
    }

    @PostMapping
    public ResponseEntity<?> RegistrarCita(@RequestBody CitaDTO dto) {
        try {
            Cita guardada = citaService.RegistrarCita(dto);
            return ResponseEntity.ok(guardada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/hoy/total")
    public ResponseEntity<Long> obtenerTotalCitasHoy() {
        return ResponseEntity.ok(0L);
    }

    @GetMapping("/NCitasXMedico")
    public ResponseEntity<List<NCitasXMedicoDTO>> getNCitasXMedico() {
        List<NCitasXMedicoDTO> lista = citaService.obtenerNCitasPorMedico();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }
    
    @GetMapping("/CitasPorFecha")
    public ResponseEntity<List<CitasPorFechaDTO>> getCitasPorFecha() {
    List<CitasPorFechaDTO> lista = citaService.obtenerCitasPorFecha();
    if (lista.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(lista);
    }

    @GetMapping("/calendario")
public ResponseEntity<List<CitaCalendarioDTO>> obtenerCitasCalendario(
        @RequestParam(required = false) Long idMedico,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
    
    List<CitaCalendarioDTO> calendario = citaService.obtenerCitasCalendario(idMedico, fechaInicio, fechaFin);
    return ResponseEntity.ok(calendario);
}

@GetMapping("/cupos-libres")
public ResponseEntity<List<String>> obtenerCuposLibres(
        @RequestParam Long idMedico,
        @RequestParam String fecha,
        @RequestParam int duracionMinutos) {
    
    System.out.println(">>> [INICIO] Calculando cupos - Medico: " + idMedico + " | Fecha: " + fecha);

    try {
        java.time.LocalDate fechaCita = java.time.LocalDate.parse(fecha);
        int diaSemana = fechaCita.getDayOfWeek().getValue();

        System.out.println("1. Buscando horario de trabajo en BD...");
        List<com.example.demo.model.HorarioMedico> horarios = horarioRepository.findByMedicoIdAndDiaSemana(idMedico, diaSemana);
        
        if (horarios.isEmpty()) {
            System.out.println("-> El médico no atiende este día. Devolviendo lista vacía.");
            return ResponseEntity.ok(java.util.Collections.emptyList()); 
        }

        com.example.demo.model.HorarioMedico turno = horarios.get(0); 
        System.out.println("-> Horario encontrado: " + turno.getHoraInicio() + " a " + turno.getHoraFin());

        System.out.println("2. Buscando citas previamente agendadas...");
        List<Cita> citasDelDia = citaRepository.findCitasPorMedicoYFecha(idMedico, fechaCita);
        System.out.println("-> Citas encontradas en BD para este día: " + citasDelDia.size());

        List<String> cuposDisponibles = new java.util.ArrayList<>();
        java.time.LocalTime horaEvaluada = turno.getHoraInicio();
        java.time.LocalTime horaFinTurno = turno.getHoraFin();

        System.out.println("3. Generando bloques de " + duracionMinutos + " minutos...");
        int iteracion = 0;

        // SEGURO ANTI-BUCLE: Máximo 60 iteraciones (equivale a 15 horas de turno)
        while (iteracion < 60) { 
            java.time.LocalTime finPosible = horaEvaluada.plusMinutes(duracionMinutos);
            
            if (finPosible.isAfter(horaFinTurno)) {
                System.out.println("-> Límite del turno alcanzado. Bucle terminado.");
                break; 
            }

            boolean choca = false;
            for (Cita c : citasDelDia) {
                if (c.getHora() == null) continue; // Protección contra datos corruptos en BD
                
                java.time.LocalTime cInicio = c.getHora();
                int duracionCita = (c.getDuracionMinutos() != null) ? c.getDuracionMinutos() : 30;
                java.time.LocalTime cFin = cInicio.plusMinutes(duracionCita);
                
                if (horaEvaluada.isBefore(cFin) && finPosible.isAfter(cInicio)) {
                    choca = true;
                    break;
                }
            }

            if (!choca) {
                // Parseo seguro de la hora a String (evita cortes de caracteres)
                String cupoTexto = horaEvaluada.toString();
                if (cupoTexto.length() > 5) {
                    cupoTexto = cupoTexto.substring(0, 5);
                }
                cuposDisponibles.add(cupoTexto);
            }
            
            horaEvaluada = horaEvaluada.plusMinutes(15); 
            iteracion++;
        }

        System.out.println(">>> [ÉXITO] Cálculo finalizado. Cupos libres generados: " + cuposDisponibles.size());
        return ResponseEntity.ok(cuposDisponibles);

    } catch (Exception e) {
        System.out.println("!!! [ERROR FATAL] Algo falló en la lógica o en la Base de Datos !!!");
        e.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}


}