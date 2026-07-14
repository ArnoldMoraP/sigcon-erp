package dsw.msrrhh.repository;

import dsw.msrrhh.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findAllByOrderByFechaDesc();

    List<Asistencia> findByEmpleadoIdOrderByFechaDesc(Long empleadoId);

    List<Asistencia> findByFecha(LocalDate fecha);

    Optional<Asistencia> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    List<Asistencia> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate desde, LocalDate hasta);
}
