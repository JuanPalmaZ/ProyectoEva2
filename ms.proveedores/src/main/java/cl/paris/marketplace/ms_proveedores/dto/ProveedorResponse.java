package cl.paris.marketplace.ms_proveedores.dto;

import java.util.UUID;

public record ProveedorResponse(
        UUID id,
        UUID usuarioId,
        String razonSocial,
        String rut,
        String estadoApro
) {}