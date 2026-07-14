package dsw.msauth.repository;

import dsw.msauth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    @Query(value = """
        SELECT u.id, u.username, r.nombre AS rol, e.nombre AS estado
        FROM public.usuario u
        INNER JOIN public.rol r ON u.rol_id = r.id
        INNER JOIN public.estado e ON u.estado_id = e.id
        WHERE u.username = :username
        """, nativeQuery = true)
    List<Object[]> findLoginDataByUsername(@Param("username") String username);
}