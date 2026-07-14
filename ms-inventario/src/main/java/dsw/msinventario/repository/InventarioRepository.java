package dsw.msinventario.repository;

import dsw.msinventario.model.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    List<Inventario> findAllByOrderByProductoAsc();

    /**
     * Busqueda por nombre de producto, insensible a mayusculas/minusculas y espacios.
     * ms-ventas manda el producto tal como quedo guardado en comercial.pedido.producto,
     * asi que hacemos el match tolerante para que no falle por un espacio de mas.
     */
    @Query("SELECT i FROM Inventario i WHERE LOWER(TRIM(i.producto)) = LOWER(TRIM(:producto))")
    Optional<Inventario> findByProductoNombre(@Param("producto") String producto);

    /** Igual que el anterior pero bloqueando la fila: evita descuentos concurrentes de stock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventario i WHERE LOWER(TRIM(i.producto)) = LOWER(TRIM(:producto))")
    Optional<Inventario> findByProductoNombreForUpdate(@Param("producto") String producto);

    @Query("SELECT i FROM Inventario i WHERE i.stock <= COALESCE(i.stockMinimo, 0)")
    List<Inventario> findBajoStock();
}
