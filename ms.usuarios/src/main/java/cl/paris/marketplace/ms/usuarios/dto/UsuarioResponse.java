package cl.paris.marketplace.ms.usuarios.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String email,
    String rol,
    Boolean baneado,
    LocalDateTime fechaIngreso
) {}