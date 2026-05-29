package cl.paris.marketplace.ms.administracion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminAccionResponse(
    UUID id,
    UUID usuarioId,
    String accion,
    String detalle,
    LocalDateTime fechaAccion
) {}