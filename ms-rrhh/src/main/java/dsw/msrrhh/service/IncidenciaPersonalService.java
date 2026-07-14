package dsw.msrrhh.service;

import dsw.msrrhh.model.IncidenciaPersonal;
import dsw.msrrhh.repository.EmpleadoRepository;
import dsw.msrrhh.repository.IncidenciaPersonalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenciaPersonalService {

    private final IncidenciaPersonalRepository incidenciaRepository;
    private final EmpleadoRepository empleadoRepository;

    @Transactional(readOnly = true)
    public List<IncidenciaPersonal> listar() { return incidenciaRepository.findAllByOrderByFechaDesc(); }

    @Transactional(readOnly = true)
    public List<IncidenciaPersonal> listarPorEmpleado(Long empleadoId) {
        return incidenciaRepository.findByEmpleadoIdOrderByFechaDesc(empleadoId);
    }

    @Transactional(readOnly = true)
    public IncidenciaPersonal obtenerPorId(Long id) {
        return incidenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidencia no encontrada: " + id));
    }

    @Transactional
    public IncidenciaPersonal crear(IncidenciaPersonal i) {
        if (i.getEmpleadoId() == null || !empleadoRepository.existsById(i.getEmpleadoId()))
            throw new IllegalArgumentException("Empleado no encontrado: " + i.getEmpleadoId());
        if (i.getTipo() == null || i.getTipo().isBlank())
            throw new IllegalArgumentException("El tipo de incidencia es obligatorio");
        if (i.getFecha() == null)    i.setFecha(LocalDate.now());
        if (i.getGravedad() == null) i.setGravedad("LEVE");
        i.setEstado("ABIERTA");
        return incidenciaRepository.save(i);
    }

    @Transactional
    public IncidenciaPersonal cerrar(Long id) {
        IncidenciaPersonal i = obtenerPorId(id);
        i.setEstado("CERRADA");
        return incidenciaRepository.save(i);
    }

    @Transactional
    public void eliminar(Long id) { incidenciaRepository.deleteById(id); }
}
