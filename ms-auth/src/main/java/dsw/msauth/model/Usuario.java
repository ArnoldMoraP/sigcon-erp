package dsw.msauth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Tabla: seguridad.usuario
 *
 * Nota: estado_id apunta a catalogos.estado, pero se mapea como Long plano
 * (NO como relacion JPA) para no arrastrar un tercer schema a este microservicio.
 */
@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    /** Hash BCrypt. Nunca se devuelve en las respuestas JSON. */
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @JsonProperty("estado_id")
    @Column(name = "estado_id")
    private Long estadoId;

    /** Usado por UserDetailsServiceImpl para construir la authority ROLE_xxx. */
    @Transient
    @JsonIgnore
    public String getRolNombre() {
        return (rol != null && rol.getNombre() != null) ? rol.getNombre() : "USER";
    }
}
