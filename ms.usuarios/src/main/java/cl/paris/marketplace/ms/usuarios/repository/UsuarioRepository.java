package cl.paris.marketplace.ms.usuarios.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.usuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    // 1. Método necesario para registrarUsuario() - Verifica si el correo ya está en la BD
    boolean existsByEmail(String email);

    // 2. Método necesario para buscarUsuarios() - Busca coincidencias ignorando mayúsculas/minúsculas
    List<Usuario> findByEmailContainingIgnoreCase(String email);
    
}