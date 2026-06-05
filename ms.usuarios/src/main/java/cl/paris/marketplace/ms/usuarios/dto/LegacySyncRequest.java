package cl.paris.marketplace.ms.usuarios.dto;

public record LegacySyncRequest(
        String codigoAntiguo,
        String tipoEntidad,
        String datosMigrados
) {}