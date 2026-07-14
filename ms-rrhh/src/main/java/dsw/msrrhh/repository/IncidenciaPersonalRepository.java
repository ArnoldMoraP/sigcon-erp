package dsw.msrrhh.repository;

import dsw.msrrhh.model.IncidenciaPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaPersonalRepository extends JpaRepository<IncidenciaPersonal, Long> {
    List<IncidenciaPersonal> findAllByOrderByFechaDesc();
    List<IncidenciaPersonal> findByEmpleadoIdOrderByFechaDesc(Long empleadoId);
    List<IncidenciaPersonal> findByEstado(String estado);
}
