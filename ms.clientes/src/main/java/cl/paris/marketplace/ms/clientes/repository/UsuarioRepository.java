package cl.paris.marketplace.ms.clientes.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.clientes.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    // Query Method para el proceso de Login
    Optional<Usuario> findByEmail(String email);
    
    // Query Method para validar si un correo ya está registrado
    boolean existsByEmail(String email);

    // Ejemplo de Custom Query con JPQL para buscar usuarios por el nombre de su rol
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombreRol = :nombreRol")
    java.util.List<Usuario> buscarUsuariosPorRol(@Param("nombreRol") String nombreRol);

}
