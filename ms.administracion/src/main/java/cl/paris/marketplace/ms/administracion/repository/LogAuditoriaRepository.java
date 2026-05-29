package cl.paris.marketplace.ms.administracion.repository;

import cl.paris.marketplace.ms.administracion.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, UUID> {
    // Permite buscar todo el historial de auditoría de un usuario administrador específico
    List<LogAuditoria> findByUsuarioIdOrderByFechaAccionDesc(UUID usuarioId);
}