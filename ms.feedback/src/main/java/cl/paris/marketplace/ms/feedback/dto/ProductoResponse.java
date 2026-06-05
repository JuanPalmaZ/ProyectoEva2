package cl.paris.marketplace.ms.feedback.dto;

import java.util.UUID;

public record ProductoResponse(
    UUID id,
    UUID proveedorId
) {}