package cl.paris.marketplace.ms.legacy.dto;

import jakarta.validation.constraints.NotBlank;

public record LegacySyncRequest(
    @NotBlank(message = "El tipo de entidad (PRODUCTO/CLIENTE/STOCK) es obligatorio") String tipoEntidad,
    @NotBlank(message = "El código antiguo del sistema ERP es obligatorio") String codigoAntiguo,
    @NotBlank(message = "El payload o datos migrados no pueden estar vacíos") String datosMigrados
) {}