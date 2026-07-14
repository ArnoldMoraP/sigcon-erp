package dsw.msinventario.repository;

import dsw.msinventario.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    List<Presupuesto> findAllByOrderByIdDesc();
    List<Presupuesto> findByPeriodo(String periodo);
}
