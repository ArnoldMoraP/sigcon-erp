package dsw.msrrhh.repository;

import dsw.msrrhh.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OJO (migracion Postgres -> MySQL):
 * NO se usa ninguna @Query(nativeQuery = true) en este microservicio.
 * Todo es Spring Data / JPQL, que Hibernate traduce al dialecto MySQL.
 * Los correlativos de codigo (EMP-0001, PLA-0001) se calculan en Java,
 * asi evitamos el operador regex '~', CAST(... AS INTEGER) y los schemas
 * calificados tipo comercial.pedido, que son sintaxis de PostgreSQL.
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    List<Empleado> findAllByOrderByApellidosAsc();

    List<Empleado> findByEstado(String estado);

    Optional<Empleado> findByDni(String dni);

    Optional<Empleado> findByCodigo(String codigo);

    List<Empleado> findByAreaIgnoreCase(String area);

    boolean existsByDni(String dni);
}
