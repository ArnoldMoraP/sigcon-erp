package dsw.msinventario.repository;

import dsw.msinventario.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    List<OrdenCompra> findAllByOrderByIdDesc();
    List<OrdenCompra> findByEstado(String estado);
    List<OrdenCompra> findByCodigoStartingWith(String prefijo);
}
