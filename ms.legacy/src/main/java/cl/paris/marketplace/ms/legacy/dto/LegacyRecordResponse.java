package cl.paris.marketplace.ms.legacy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LegacyRecordResponse(
    UUID id,
    String codigoAntiguo,
    String tipoEntidad,
    String datosMigrados,
    String estado,
    LocalDateTime fechaSincronizacion
) {}