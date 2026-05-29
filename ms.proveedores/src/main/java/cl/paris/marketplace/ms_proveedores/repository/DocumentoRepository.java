package cl.paris.marketplace.ms_proveedores.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms_proveedores.model.Documento;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    // ==========================================
    // REGLAS DE BÚSQUEDA Y VALIDACIÓN
    // ==========================================

    // 1. Busca todos los documentos que le pertenecen a un proveedor específico.
    // Esto es vital para cuando entres al perfil del proveedor y quieras listar sus archivos.
    List<Documento> findByProveedorId(UUID proveedorId);

    // 2. Verifica si un proveedor YA subió un tipo de documento en específico.
    // Muy útil para evitar que te suban dos veces el mismo "CONTRATO" o "CERTIFICADO".
    boolean existsByProveedorIdAndTipoDocumento(UUID proveedorId, String tipoDocumento);

}