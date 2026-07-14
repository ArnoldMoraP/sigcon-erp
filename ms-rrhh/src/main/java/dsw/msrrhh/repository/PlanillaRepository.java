package dsw.msrrhh.repository;

import dsw.msrrhh.model.Planilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanillaRepository extends JpaRepository<Planilla, Long> {

    List<Planilla> findAllByOrderByIdDesc();

    Optional<Planilla> findByPeriodo(String periodo);

    boolean existsByPeriodo(String periodo);
}
