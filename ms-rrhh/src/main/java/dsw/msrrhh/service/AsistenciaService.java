package dsw.msrrhh.service;

import dsw.msrrhh.model.Asistencia;
import dsw.msrrhh.model.IncidenciaPersonal;
import dsw.msrrhh.repository.AsistenciaRepository;
import dsw.msrrhh.repository.EmpleadoRepository;
import dsw.msrrhh.repository.IncidenciaPersonalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final IncidenciaPersonalRepository incidenciaRepository;

    /** Hora limite de entrada: despues de esto se marca TARDANZA. */
    private static final LocalTime HORA_LIMITE = LocalTime.of(8, 10);

    @Transactional(readOnly = true)
    public List<Asistencia> listar() { return asistenciaRepository.findAllByOrderByFechaDesc(); }

    @Transactional(readOnly = true)
    public List<Asistencia> listarPorEmpleado(Long empleadoId) {
        return asistenciaRepository.findByEmpleadoIdOrderByFechaDesc(empleadoId);
    }

    @Transactional(readOnly = true)
    public Asistencia obtenerPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada: " + id));
    }

    /**
     * Marca la entrada. Calcula automaticamente el estado (PUNTUAL/TARDANZA)
     * y, si hay tardanza, genera la incidencia de personal correspondiente.
     */
    @Transactional
    public Asistencia registrar(Asistencia a) {
        if (a.getEmpleadoId() == null || !empleadoRepository.existsById(a.getEmpleadoId()))
            throw new IllegalArgumentException("Empleado no encontrado: " + a.getEmpleadoId());

        if (a.getFecha() == null) a.setFecha(LocalDate.now());

        asistenciaRepository.findByEmpleadoIdAndFecha(a.getEmpleadoId(), a.getFecha())
                .ifPresent(x -> { throw new IllegalArgumentException(
                        "Ya existe una asistencia registrada para ese empleado en la fecha " + a.getFecha()); });

        if (a.getHoraEntrada() == null && !"FALTA".equalsIgnoreCase(String.valueOf(a.getEstado())))
            a.setHoraEntrada(LocalTime.now());

        if (a.getHoraEntrada() != null) {
            a.setEstado(a.getHoraEntrada().isAfter(HORA_LIMITE) ? "TARDANZA" : "PUNTUAL");
        } else {
            a.setEstado("FALTA");
        }

        calcularHoras(a);
        Asistencia guardada = asistenciaRepository.save(a);

        if ("TARDANZA".equals(guardada.getEstado()) || "FALTA".equals(guardada.getEstado())) {
            IncidenciaPersonal inc = new IncidenciaPersonal();
            inc.setEmpleadoId(guardada.getEmpleadoId());
            inc.setTipo(guardada.getEstado());
            inc.setDescripcion("Generada automaticamente desde el registro de asistencia del " + guardada.getFecha());
            inc.setFecha(guardada.getFecha());
            inc.setGravedad("FALTA".equals(guardada.getEstado()) ? "MODERADA" : "LEVE");
            inc.setEstado("ABIERTA");
            incidenciaRepository.save(inc);
            log.info("Incidencia {} generada para empleado {}", inc.getTipo(), guardada.getEmpleadoId());
        }
        return guardada;
    }

    /** Marca la salida y calcula las horas trabajadas. */
    @Transactional
    public Asistencia marcarSalida(Long id, LocalTime horaSalida) {
        Asistencia a = obtenerPorId(id);
        a.setHoraSalida(horaSalida != null ? horaSalida : LocalTime.now());
        calcularHoras(a);
        return asistenciaRepository.save(a);
    }

    private void calcularHoras(Asistencia a) {
        if (a.getHoraEntrada() == null || a.getHoraSalida() == null) return;
        long minutos = Duration.between(a.getHoraEntrada(), a.getHoraSalida()).toMinutes();
        if (minutos < 0) minutos = 0;
        // se descuenta 1 hora de refrigerio si la jornada supera las 6 horas
        if (minutos > 360) minutos -= 60;
        a.setHorasTrabajadas(BigDecimal.valueOf(minutos)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
    }

    @Transactional
    public void eliminar(Long id) { asistenciaRepository.deleteById(id); }
}
