package dsw.msrrhh.repository;

import dsw.msrrhh.model.PagoPlanilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoPlanillaRepository extends JpaRepository<PagoPlanilla, Long> {
    List<PagoPlanilla> findAllByOrderByIdDesc();
    List<PagoPlanilla> findByPlanillaId(Long planillaId);
}
