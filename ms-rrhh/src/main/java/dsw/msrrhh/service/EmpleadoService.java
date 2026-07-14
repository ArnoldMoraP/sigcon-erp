package dsw.msrrhh.service;

import dsw.msrrhh.model.Empleado;
import dsw.msrrhh.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    /** Correlativo calculado en Java (portable Postgres/MySQL). */
    private String generarCodigo() {
        int max = 0;
        for (Empleado e : empleadoRepository.findAll()) {
            String c = e.getCodigo();
            if (c == null || !c.startsWith("EMP-")) continue;
            try {
                max = Math.max(max, Integer.parseInt(c.substring(4).trim()));
            } catch (NumberFormatException ignored) { }
        }
        return String.format("EMP-%04d", max + 1);
    }

    @Transactional(readOnly = true)
    public List<Empleado> listar() { return empleadoRepository.findAllByOrderByApellidosAsc(); }

    @Transactional(readOnly = true)
    public List<Empleado> listarActivos() { return empleadoRepository.findByEstado("ACTIVO"); }

    @Transactional(readOnly = true)
    public Empleado obtenerPorId(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + id));
    }

    @Transactional
    public Empleado crear(Empleado e) {
        if (e.getDni() == null || e.getDni().isBlank())
            throw new IllegalArgumentException("El DNI es obligatorio");
        if (empleadoRepository.existsByDni(e.getDni()))
            throw new IllegalArgumentException("Ya existe un empleado con el DNI " + e.getDni());
        if (e.getCodigo() == null || e.getCodigo().isBlank()) e.setCodigo(generarCodigo());
        if (e.getEstado() == null) e.setEstado("ACTIVO");
        Empleado guardado = empleadoRepository.save(e);
        log.info("Empleado {} registrado ({} {})", guardado.getCodigo(), guardado.getNombres(), guardado.getApellidos());
        return guardado;
    }

    @Transactional
    public Empleado actualizar(Long id, Empleado datos) {
        Empleado e = obtenerPorId(id);
        if (datos.getNombres() != null)      e.setNombres(datos.getNombres());
        if (datos.getApellidos() != null)    e.setApellidos(datos.getApellidos());
        if (datos.getCargo() != null)        e.setCargo(datos.getCargo());
        if (datos.getArea() != null)         e.setArea(datos.getArea());
        if (datos.getSalario() != null)      e.setSalario(datos.getSalario());
        if (datos.getEmail() != null)        e.setEmail(datos.getEmail());
        if (datos.getTelefono() != null)     e.setTelefono(datos.getTelefono());
        if (datos.getFechaIngreso() != null) e.setFechaIngreso(datos.getFechaIngreso());
        if (datos.getEstado() != null)       e.setEstado(datos.getEstado());
        return empleadoRepository.save(e);
    }

    /** Baja logica: no borramos al empleado, lo pasamos a INACTIVO. */
    @Transactional
    public Empleado desactivar(Long id) {
        Empleado e = obtenerPorId(id);
        e.setEstado("INACTIVO");
        return empleadoRepository.save(e);
    }

    @Transactional
    public void eliminar(Long id) { empleadoRepository.deleteById(id); }
}
