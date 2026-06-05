package cl.paris.marketplace.ms.usuarios.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.usuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    // 1. Para registrarUsuario() - Verifica si el correo ya existe
    boolean existsByEmail(String email);

    // 2. Para buscarUsuarios() del Administrador - Trae una lista de coincidencias parciales
    List<Usuario> findByEmailContainingIgnoreCase(String email);

    // 3. Para el Login de Spring Security (Fase 3) - Trae un unico usuario exacto (Opcional)
    Optional<Usuario> findByEmail(String email);
    
}