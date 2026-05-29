package cl.paris.marketplace.ms.usuarios.dto;

import java.util.UUID;

public record RolResponse(UUID id,
    String nombreRol
) {}