package dsw.msinventario.repository;

import dsw.msinventario.model.FacturaProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacturaProveedorRepository extends JpaRepository<FacturaProveedor, Long> {
    List<FacturaProveedor> findAllByOrderByIdDesc();
    List<FacturaProveedor> findByProveedorId(Long proveedorId);
    List<FacturaProveedor> findByCompraId(Long compraId);
}
