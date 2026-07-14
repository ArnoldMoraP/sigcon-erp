package dsw.msventas.repository;

import dsw.msventas.model.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    /**
     * SIN JOIN a seguridad.usuario.
     *
     * Antes esta query cruzaba dos schemas (comercial + seguridad), lo que
     * ataba ms-ventas a la base de datos de ms-auth. Ahora 'vendedor' es una
     * columna propia de comercial.cotizacion, resuelta por REST al crear.
     */
    @Query(value = """
        SELECT c.id, c.codigo, c.cliente, c.ruc, c.producto,
               c.cantidad, c.precio_unitario, c.subtotal,
               c.descuento_porcentaje, c.descuento_monto,
               c.total, c.estado, c.fecha_registro,
               c.vendedor
        FROM public.cotizacion c
        ORDER BY c.id DESC
        """, nativeQuery = true)
    List<Object[]> listarCotizaciones();
}