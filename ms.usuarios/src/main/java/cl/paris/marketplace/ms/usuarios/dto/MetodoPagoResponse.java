package cl.paris.marketplace.ms.usuarios.dto;

// Este es solo un molde para atrapar el JSON que nos enviará ms-clientes

import java.util.UUID;

public record MetodoPagoResponse(
    UUID id,
    UUID usuarioId,
    String tipo
) {}