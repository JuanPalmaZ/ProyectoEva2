package cl.paris.marketplace.ms.feedback.dto;

import java.util.UUID;

// DTO interno para mapear la respuesta de ms-productos
public record ProductoResponse(
    UUID id,
    UUID proveedorId
) {}