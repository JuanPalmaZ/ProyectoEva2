package cl.paris.marketplace.ms.legacy.repository;

import cl.paris.marketplace.ms.legacy.model.LegacyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LegacyRecordRepository extends JpaRepository<LegacyRecord, UUID> {

    // Permite auditar las migraciones filtrando por tipo de datos (ej: PRODUCTO)
    List<LegacyRecord> findByTipoEntidadOrderByFechaSincronizacionDesc(String tipoEntidad);
}