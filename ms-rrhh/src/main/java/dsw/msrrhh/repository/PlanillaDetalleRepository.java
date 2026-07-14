package dsw.msrrhh.repository;

import dsw.msrrhh.model.PlanillaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanillaDetalleRepository extends JpaRepository<PlanillaDetalle, Long> {
    List<PlanillaDetalle> findByPlanillaId(Long planillaId);
    List<PlanillaDetalle> findByEmpleadoId(Long empleadoId);
}
