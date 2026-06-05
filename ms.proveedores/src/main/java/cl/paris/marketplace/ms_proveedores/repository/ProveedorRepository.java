package cl.paris.marketplace.ms_proveedores.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms_proveedores.model.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {

    boolean existsByRut(String rut);

    Optional<Proveedor> findByRut(String rut);

    List<Proveedor> findByUsuarioId(UUID usuarioId);
    
    // Método para sacar el perfil de forma segura
    Optional<Proveedor> findFirstByUsuarioId(UUID usuarioId);

    List<Proveedor> findByEstadoApro(String estadoApro);

}