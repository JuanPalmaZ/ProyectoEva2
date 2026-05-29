package cl.paris.marketplace.ms.usuarios.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.usuarios.model.Perfil;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID>{
    // Query Method para obtener el perfil mediante el ID del Usuario (Relación OneToOne)
    Optional<Perfil> findByUsuarioId(UUID usuarioId);

}
