package dsw.msinventario.repository;

import dsw.msinventario.model.PagoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoProveedorRepository extends JpaRepository<PagoProveedor, Long> {
    List<PagoProveedor> findAllByOrderByIdDesc();
    List<PagoProveedor> findByFacturaId(Long facturaId);
}
