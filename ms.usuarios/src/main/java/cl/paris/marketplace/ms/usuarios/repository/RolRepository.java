package cl.paris.marketplace.ms.usuarios.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.usuarios.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {
    Optional<Rol> findByNombreRol(String nombreRol);
}


