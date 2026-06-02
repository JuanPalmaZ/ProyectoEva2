package cl.paris.marketplace.ms_proveedores.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms_proveedores.model.Documento;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    List<Documento> findByProveedorId(UUID proveedorId);

    boolean existsByProveedorIdAndTipoDocumento(UUID proveedorId, String tipoDocumento);

}